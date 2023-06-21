package com.example.fshop.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.fshop.R
import com.example.fshop.databinding.FragmentSuccessBinding


class SuccessFragment : Fragment() {
    private lateinit var binding: FragmentSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuccessBinding.inflate(inflater, container, false)


        Glide.with(this)
            .asGif()
            .load(R.drawable.konfeti)
            .into(binding.gifImageView)

        val controller = findNavController()
        binding.backBtn.setOnClickListener { controller.navigate(R.id.cartFragment) }

        return binding.root

    }
}

