package com.example.fshop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R
import com.example.fshop.databases.OrderDao
import com.example.fshop.databases.OrderDb
import com.example.fshop.databases.OrdersAdapter
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.FragmentOrdersBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private lateinit var userDao: UserDao
    private lateinit var orderDao: OrderDao
    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private var currentUserEmail: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")
        orderDao = OrderDb.getDb(requireContext()).getDao()
        userDao = UserDb.getDb(requireContext()).getDao()
        navController = findNavController()

        ordersAdapter = OrdersAdapter({ orderItem ->
            orderItem.id?.let { id ->
                lifecycleScope.launch {
                    orderDao.deleteOrderItem(id)
                }
            }
        }, navController)

        binding.btnBack.setOnClickListener { navController.navigate(R.id.profileFragment) }

        initRecyclerView()

        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }
            currentUserEmail = currentUser?.email ?: ""
        }
        viewOrder()

        return binding.root
    }

    private fun viewOrder() {
        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }
            currentUserEmail = currentUser?.email ?: ""

            orderDao.getOrderByUser(currentUserEmail).onEach { orders ->
                ordersAdapter.setOrder(orders)
            }.launchIn(lifecycleScope)
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = ordersAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
