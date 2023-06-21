package com.example.fshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext



class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)


        binding.loginBtn.setOnClickListener {
            val email = binding.mail.text.toString().trim()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        binding.btnRegLog.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val userDao = UserDb.getDb(this@LoginActivity).getDao()
            val user = userDao.getUser(email, password)
            if (user != null) {
                user.is_auth = 1
                userDao.updateUser(user)
                withContext(Dispatchers.Main) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.password.setText("")
                    Toast.makeText(this@LoginActivity, "Неправильный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        return
    }
}

