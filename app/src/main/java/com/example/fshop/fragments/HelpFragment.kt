package com.example.fshop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R
import com.example.fshop.databases.ClothAdapter
import com.example.fshop.databases.ClothDao
import com.example.fshop.databases.ClothDb
import com.example.fshop.databases.ClothItem
import com.example.fshop.databinding.FragmentHelpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HelpFragment : Fragment() {

    private lateinit var edCategory: EditText
    private lateinit var edItemText: EditText
    private lateinit var edType: EditText
    private lateinit var edPrice: EditText
    private lateinit var imageName: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnView: Button

    private lateinit var binding: FragmentHelpBinding
    private lateinit var recyclerView: RecyclerView


    private lateinit var clothDao: ClothDao
    private lateinit var clothAdapter: ClothAdapter
    private lateinit var userRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView // Предполагается, что вы добавили RecyclerView в макет фрагмента

        initView()
        initRecyclerView()

        clothDao = ClothDb.getDb(requireContext()).getDao()

        val navController = findNavController()
        clothAdapter = ClothAdapter(clothDao, viewLifecycleOwner.lifecycleScope, { bundle ->
            navController.navigate(R.id.prodCartFragment, bundle)
        })

        recyclerView.adapter = clothAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        btnAdd.setOnClickListener{ addService() }
        btnView.setOnClickListener{ viewCloth() }


        return binding.root
    }

    private fun viewCloth() {
        clothDao.getAllCloth().onEach { clothes ->
            clothAdapter.setCloth(clothes)
        }.launchIn(lifecycleScope)
    }


    private fun addService(){
        val name = edItemText.text.toString()
        val type = edCategory.text.toString()
        val size = edType.text.toString()
        val price = edPrice.text.toString()
        val image = imageName.text.toString()

        val text = null // Здесь вы можете добавить логику для установки значения text
        val imageRes = null // Здесь вы можете добавить логику для установки значения imageRes

        if (name.isEmpty() || type.isEmpty() || price.isEmpty() || size.isEmpty()){
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
        }
        else{
            val cloth = ClothItem(name = name, type = type, price = price, size = size, text = text, imageRes = image)
            lifecycleScope.launch(Dispatchers.IO) {
                clothDao.insertCloth(cloth)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Товар добавлен", Toast.LENGTH_SHORT).show()
                    clearEditText()
                }
            }
        }
    }

    private fun clearEditText() {
        edItemText.setText("")
        edCategory.setText("")
        edPrice.setText("")
        edType.setText("")
        imageName.setText("")
    }

    private fun initView() {
        edItemText = binding.itemText
        edCategory = binding.category
        edType = binding.type
        edPrice = binding.price
        btnAdd = binding.addService
        btnView = binding.viewService
        imageName = binding.imageRes
    }

    private fun initRecyclerView() {
        this.recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}