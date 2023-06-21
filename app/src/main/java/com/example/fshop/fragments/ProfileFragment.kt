package com.example.fshop.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fshop.LoginActivity
import com.example.fshop.R
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databases.UserItem
import com.example.fshop.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

            return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.cardViewOrders.setOnClickListener {
            navController.navigate(R.id.ordersFragment)
        }

        binding.cardViewFav.setOnClickListener {
            navController.navigate(R.id.favFragment)
        }

        getUserData()
        val builder = AlertDialog.Builder(requireContext())

        val btnLogout = binding.btnLogout
        btnLogout.setOnClickListener {
            builder.setMessage("Вы уверены, что хотите выйти?")
            builder.setCancelable(true)
            builder.setPositiveButton("Да"){dialog,_ ->
                logoutUser()
                dialog.dismiss()
            }
            builder.setNegativeButton("Нет"){dialog, _->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()
        }

        val btnEdit = binding.btnEdit
        btnEdit.setOnClickListener {

            builder.setMessage("Вы уверены, что хотите изменить свое имя?")
            builder.setCancelable(true)
            builder.setPositiveButton("Да"){dialog,_ ->
                updateUserData()
                dialog.dismiss()
            }
            builder.setNegativeButton("Нет"){dialog, _->
                dialog.dismiss()
            }
            val alert = builder.create()
            alert.show()


        }

    }
    private fun updateUserData() {
        val userDao = UserDb.getDb(requireContext()).getDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val user = getCurrentUser(userDao)
            if (user != null) {
                val firstname = binding.NameTv.text.toString()
//                val phone = binding.PhoneTv.text.toString()
//                val email = binding.EmailTv.text.toString()

                user.firstname = firstname
//                user.phone = phone
//                user.email = email

                updateUser(userDao, user)
            }
        }
    }

    private fun logoutUser() {
        GlobalScope.launch {
            updateUserAuthStatus(false)
            withContext(Dispatchers.Main) {
                navigateToLogin()
            }
        }
    }

    private suspend fun updateUserAuthStatus(isAuth: Boolean) {
        val user = getCurrentUser()
        user?.is_auth = if (isAuth) 1 else 0
        user?.let { UserDb.getDb(requireContext()).getDao().updateUser(it) }
    }

    private suspend fun getCurrentUser(): UserItem? {
        val userDao = UserDb.getDb(requireContext()).getDao()
        return getCurrentUser(userDao)
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun getUserData() {
        val userDao = UserDb.getDb(requireContext()).getDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val user = getCurrentUser(userDao)
            if (user != null) {
                binding.NameTv.text = user.firstname.toEditable()
//                binding.PhoneTv.text = user.phone.toEditable()
//                binding.EmailTv.text = user.email.toEditable()
            } else {
                // Handle the case when user is not authenticated
            }
        }
    }

    private suspend fun getCurrentUser(userDao: UserDao): UserItem? {
        return userDao.getUserByAuthStatus(1)
    }
    private fun String.toEditable(): Editable {
        return Editable.Factory.getInstance().newEditable(this)
    }

    private suspend fun updateUser(userDao: UserDao, user: UserItem) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }
}
