package com.example.fshop.fragments

import android.os.Bundle
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R
import com.example.fshop.databases.ClothAdapter
import com.example.fshop.databases.ClothDao
import com.example.fshop.databases.ClothDb
import com.example.fshop.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var clothDao: ClothDao
    private lateinit var clothAdapter: ClothAdapter

    // создаем экземпляр класса
    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            clothAdapter.getFilter().filter(newText)
            return true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView ?: throw RuntimeException("RecyclerView not found in layout")

        clothDao = ClothDb.getDb(requireContext()).getDao()

        val navController = findNavController()
        clothAdapter = ClothAdapter(clothDao, viewLifecycleOwner.lifecycleScope) { bundle ->
            navController.navigate(R.id.prodCartFragment, bundle)
        }

        recyclerView.adapter = clothAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        viewCloth()

        return binding.root
    }

    private fun viewCloth() {
        clothDao.getUniqueNameLowestSize().onEach { clothes ->
            clothAdapter.setCloth(clothes)
        }.launchIn(lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                clothAdapter.getFilter().filter(newText)
                return true
            }
        })
    }
}