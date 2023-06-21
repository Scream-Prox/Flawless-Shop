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
import com.example.fshop.databases.FavAdapter
import com.example.fshop.databases.FavDao
import com.example.fshop.databases.FavDb
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.FragmentFavBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavFragment : Fragment() {

    private lateinit var binding: FragmentFavBinding
    private lateinit var userDao: UserDao
    private lateinit var favDao: FavDao
    private lateinit var navController: NavController
    private lateinit var recyclerView: RecyclerView
    private lateinit var favAdapter: FavAdapter
    private var currentUserEmail: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")
        favDao = FavDb.getDb(requireContext()).getDao()
        userDao = UserDb.getDb(requireContext()).getDao()
        navController = findNavController()

        favAdapter = FavAdapter({ favItem ->
            favItem.id?.let { id ->
                lifecycleScope.launch {
                    favDao.deleteFavItem(id)
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
        viewFav()

        return binding.root
    }

    private fun viewFav() {
        lifecycleScope.launch {
            val currentUser = withContext(Dispatchers.IO) {
                userDao.getUserByAuthStatus(1)
            }
            currentUserEmail = currentUser?.email ?: ""

            favDao.getFavouriteByUser(currentUserEmail).onEach { favourites ->
                favAdapter.setFav(favourites)
            }.launchIn(lifecycleScope)
        }
    }

    private fun initRecyclerView() {
        recyclerView.adapter = favAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
