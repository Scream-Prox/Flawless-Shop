package com.example.fshop.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fshop.R
import com.example.fshop.databases.CartDao
import com.example.fshop.databases.CartDb
import com.example.fshop.databases.CartItem
import com.example.fshop.databases.FavDao
import com.example.fshop.databases.FavDb
import com.example.fshop.databases.FavItem
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.FragmentProdCartBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProdCartFragment : Fragment() {

    private lateinit var binding: FragmentProdCartBinding
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private lateinit var favDao: FavDao
    private var name: String? = null

    private var selectedSize: String? = null
    private var selectedCardView: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProdCartBinding.inflate(inflater, container, false)
        userDao = UserDb.getDb(requireContext()).getDao()
        cartDao = CartDb.getDb(requireContext()).getDao()
        favDao = FavDb.getDb(requireContext()).getDao()

        val btnCart = binding.btnCart
        btnCart.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите добавить товар в корзину?")
                .setPositiveButton("Да") { dialog, which ->
                    addToCart()
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Нет") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

        val btnFav = binding.btnFav

        val id = arguments?.getInt("id")
        name = arguments?.getString("name")
        val price = arguments?.getString("price")
        val size = arguments?.getString("size")
        val desc = arguments?.getString("desc")
        val sizeM = arguments?.getString("sizeM")
        val sizeL = arguments?.getString("sizeL")
        val imgRes = arguments?.getString("imgRes")

        val controller = findNavController()
        val backBtn = binding.backBtn
        backBtn.setOnClickListener { controller.navigate(R.id.homeFragment) }

        val nameTextView = binding.name
        val priceTextView = binding.price
        val sizeTextView = binding.sizeS
        val descTextView = binding.desc
        val imageView = binding.prodImg
        val sizeMTv = binding.sizeM
        val sizeLTv = binding.sizeL

        nameTextView.text = name
        priceTextView.text = price
        sizeTextView.text = size
        descTextView.text = desc
        sizeMTv.text = sizeM
        sizeLTv.text = sizeL

        checkIfFavouriteAndUpdateButton(name)

        selectedSize = size

        val CvM = binding.CvSizeM
        val CvL = binding.CvSizeL
        val CvS = binding.CvSizeS

        sizeMTv.setOnClickListener {
            selectedSize = sizeM
            setSelectedCardView(CvM)
        }

        sizeLTv.setOnClickListener {
            selectedSize = sizeL
            setSelectedCardView(CvL)
        }
        sizeTextView.setOnClickListener {
            selectedSize = size
            setSelectedCardView(CvS)
        }

        if (imgRes != null) {
            val imageResId = resources.getIdentifier(
                imgRes,
                "drawable",
                requireContext().packageName
            )
            imageView.setImageResource(imageResId)
        } else {
            imageView.setImageResource(R.drawable.flogo)
        }

        btnFav.setOnClickListener {
            addToFavourites()
            lifecycleScope.launch {
                delay(1000)
                checkIfFavouriteAndUpdateButton(name)
            }
        }

        checkIfFavouriteAndUpdateButton(name)
        return binding.root
    }

    private fun setSelectedCardView(cardView: CardView) {
        selectedCardView?.setCardBackgroundColor(Color.parseColor("#F24235")) // Изменяем фон предыдущего выбранного CardView на красный
        selectedCardView = cardView // Обновляем ссылку на текущий выбранный CardView
        selectedCardView?.setCardBackgroundColor(Color.parseColor("#9E9292")) // Устанавливаем фон текущего выбранного CardView на серый
    }

    private fun addToCart() {
        val name = binding.name.text.toString()
        val price = binding.price.text.toString()
        val imgRes = arguments?.getString("imgRes")
        val size = selectedSize ?: return

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val currentUser = userDao.getUserByAuthStatus(1)
                if (currentUser != null) {
                    val userInfo = currentUser.email
                    val cartItem = CartItem(name = name, price = price, size = size, user_info = userInfo, imageRes = imgRes)
                    cartDao.insertCart(cartItem)
                }
            }
        }
    }

    private fun addToFavourites() {
        val name = binding.name.text.toString()
        val price = binding.price.text.toString()
        val size = selectedSize ?: return
        val imgRes = arguments?.getString("imgRes")
        val text = binding.desc.text.toString()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val currentUser = userDao.getUserByAuthStatus(1)
                if (currentUser != null) {
                    val userInfo = currentUser.email
                    val favourites = favDao.getFavouriteByUser(userInfo).firstOrNull()
                    val existingFavItem = favourites?.firstOrNull { it.name == name }
                    if (existingFavItem != null) {
                        favDao.deleteFavItem(existingFavItem.id ?: return@withContext)
                        withContext(Dispatchers.Main) {
                            binding.btnFav.setImageResource(R.drawable.favourite_gray)
                        }
                    } else {
                        val favItem = FavItem(name = name, price = price, size = size, user_info = userInfo, imageRes = imgRes, text = text)
                        favDao.insertFavourite(favItem)
                        withContext(Dispatchers.Main) {
                            binding.btnFav.setImageResource(R.drawable.favourite_red)
                        }
                    }
                }
            }
        }
    }

    private fun checkIfFavouriteAndUpdateButton(prodName: String?) {
        lifecycleScope.launch {
            val currentUser = userDao.getUserByAuthStatus(1)
            if (currentUser != null) {
                val userInfo = currentUser.email
                favDao.getFavouriteByUser(userInfo).collect { favourites ->
                    if (prodName != null && favourites.any { it.name == prodName }) {
                        withContext(Dispatchers.Main) {
                            binding.btnFav.setImageResource(R.drawable.favourite_red)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.btnFav.setImageResource(R.drawable.favourite_gray)
                        }
                    }
                }
            }
        }
    }
}