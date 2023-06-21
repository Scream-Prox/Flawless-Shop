package com.example.fshop.fragments

import androidx.lifecycle.lifecycleScope
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.databinding.FragmentCartBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.fshop.R
import com.example.fshop.databases.CartAdapter
import com.example.fshop.databases.CartDb
import com.example.fshop.viewmodels.CartViewModel
import com.example.fshop.repositories.CartRepository
import com.example.fshop.databases.UserDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: CartViewModel
    private lateinit var navController: NavController
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")
        navController = findNavController()

        lifecycleScope.launch {
            val currentUserEmail = withContext(Dispatchers.IO) {
                UserDb.getDb(requireContext()).getDao().getUserByAuthStatus(1)?.email ?: ""
            }

            val cartDao = CartDb.getDb(requireContext()).getDao()
            val repository = CartRepository(cartDao)
            viewModel = CartViewModel(repository, currentUserEmail)

            initRecyclerView()
            observeCartItems()
            observeTotalPrice()
        }

        binding.toPay.setOnClickListener {
            navController.navigate(R.id.paymentFragment)
        }

        return binding.root
    }

    private fun observeCartItems() {
        lifecycleScope.launch {
            viewModel.cartItems.collect { carts ->
            cartAdapter.setCart(carts)
            // Обновляем видимость кнопки в зависимости от того, есть ли товары в корзине
            binding.toPay.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
            binding.totalPrice.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
            binding.totalTv.visibility = if (cartAdapter.isCartEmpty()) View.GONE else View.VISIBLE
        }
        }
    }

    private fun observeTotalPrice() {
        lifecycleScope.launch {
            viewModel.totalPrice.collect { total ->
                // Здесь вы можете обновить текстовое поле с общей стоимостью товаров в корзине
                // Например, если у вас есть TextView с id totalPrice, вы можете сделать следующее:
                binding.totalPrice?.text = total.toString()
            }
        }
    }

    private fun initRecyclerView() {
        cartAdapter = CartAdapter(viewModel, findNavController())
        recyclerView.adapter = cartAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
