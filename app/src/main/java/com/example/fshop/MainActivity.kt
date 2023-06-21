package com.example.fshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fshop.databases.UserDao
import com.example.fshop.databases.UserDb
import com.example.fshop.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var conf: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //BOTTOM NAVIGATION
        val botNavView: BottomNavigationView = binding.botNavView
        navController = findNavController(R.id.fragmentContainerView)
        userDao = UserDb.getDb(this).getDao()

        navController.addOnDestinationChangedListener {_, destination,_ ->
            when (destination.id) {
                R.id.homeFragment -> botNavView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.cartFragment -> botNavView.menu.findItem(R.id.cartFragment).isChecked = true
                R.id.profileFragment -> botNavView.menu.findItem(R.id.profileFragment).isChecked = true
                R.id.helpFragment -> botNavView.menu.findItem(R.id.helpFragment).isChecked = true
            }
        }

        lifecycleScope.launch {
            val currentUser = userDao.getUserByAdminStatus(1)

            conf = AppBarConfiguration(
                setOf(
                    if (currentUser != null) R.id.profileFragment else R.id.settingsFragment,
                    R.id.homeFragment,
                    R.id.cartFragment,
                    R.id.profileFragment,
                )
            )

            withContext(Dispatchers.Main) {
                setupActionBarWithNavController(navController, conf)
                botNavView.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.homeFragment, R.id.cartFragment, R.id.profileFragment -> {
                            navController.popBackStack(R.id.homeFragment, false)
                            navController.popBackStack(R.id.cartFragment, false)
                            navController.navigate(item.itemId)
                            true
                        }

                        R.id.helpFragment -> {
                            lifecycleScope.launch {
                                val authUser = userDao.getUserByAuthStatus(1)
                                Log.d("MainActivity", "Authenticated user: $authUser")
                                if (authUser != null && authUser.is_admin == 1) {
                                    Log.d("MainActivity", "Navigating to helpFragment")
                                    navController.navigate(R.id.helpFragment)
                                } else {
                                    Log.d("MainActivity", "Navigating to settingsFragment")
                                    navController.navigate(R.id.settingsFragment)
                                }
                            }
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if(navController.currentDestination?.id == R.id.homeFragment) {
            return
        } else {
            super.onBackPressed()
        }
    }
}
