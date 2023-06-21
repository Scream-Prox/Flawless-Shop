package com.example.fshop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fshop.R
import com.example.fshop.databases.CartDao
import com.example.fshop.databases.CartDb
import com.example.fshop.databases.OrderDao
import com.example.fshop.databases.OrderDb
import com.example.fshop.databases.OrderItem
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.FragmentPaymentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var userDao: UserDao
    private lateinit var cartDao: CartDao
    private lateinit var orderDao: OrderDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        val controller = findNavController()
        val backBtn = binding.backBtn
        backBtn.setOnClickListener{ controller.navigate(R.id.cartFragment) }

        val mastercardView = binding.cvMastercard // замените на ваше имя id для CardView
        mastercardView.setOnClickListener {
            binding.cardNumberEdit.setText("2200 7004 0606 8098")
            binding.EditCVV.setText("832")
            binding.EditDate.setText("12/30")
        }

        val appleView = binding.cvApplePay // замените на ваше имя id для CardView
        appleView.setOnClickListener {
            binding.cardNumberEdit.setText("")
            binding.EditCVV.setText("")
            binding.EditDate.setText("")
        }

        userDao = UserDb.getDb(requireContext()).getDao()
        cartDao = CartDb.getDb(requireContext()).getDao()
        orderDao = OrderDb.getDb(requireContext()).getDao()

        binding.btnPay.setOnClickListener {
            lifecycleScope.launch {
                val currentUser = withContext(Dispatchers.IO) {
                    userDao.getUserByAuthStatus(1)
                }
                currentUser?.let {
                    withContext(Dispatchers.IO) {
                        val cartItems = cartDao.getCartItemsByUser(it.email).first()
                        cartItems.forEach { cartItem ->
                            val orderItem = OrderItem(
                                name = cartItem.name,
                                price = cartItem.price,
                                size = cartItem.size,
                                user_info = it.email,
                                imageRes = cartItem.imageRes
                            )
                            orderDao.insertOrder(orderItem)
                        }
                        cartDao.deleteCartItemsByUser(it.email)
                    }
                    controller.navigate(R.id.successFragment)
                }
            }
        }


        return binding.root
    }
}
