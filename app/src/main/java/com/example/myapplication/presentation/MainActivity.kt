package com.example.myapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
    )

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // permissions
        askPermissionsIfNotGranted()

        // nav
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController
        navController.graph = navController.navInflater.inflate(R.navigation.nav_graph)

        // menu
        toolbar = findViewById(R.id.materialToolbar)
        setSupportActionBar(toolbar)
        val builder = AppBarConfiguration.Builder(navController.graph)

        val appBarConf = builder.build()
        toolbar.setupWithNavController(navController, appBarConf)

        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        // savedInstanceState
        if (savedInstanceState != null) {
            val currentFragment =
                savedInstanceState.getInt("current_fragment", R.id.filesListFragment)
            if (currentFragment == R.id.fileInfoFragment) {
                return
            }
            navController.navigate(currentFragment)
            val selectedItemId = savedInstanceState.getInt("selectedItemId", 0)
            bottomNav.selectedItemId = selectedItemId
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    private fun askPermissionsIfNotGranted() {
        Log.d(TAG, "ask_permissions_if_not_granted")
        // Register callback to handle user response
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted: Map<String, Boolean> ->
                if (!isGranted.values.contains(false)) {
                    Log.d(TAG, "Permission is granted (callback)")
                } else {
                    Log.d(TAG, "some permissions not granted")
                    Toast.makeText(
                        applicationContext,
                        R.string.permissions_denied_text,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }

        when {
            permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
            -> {
                Log.d(TAG, "all permissions granted")
            }
            else -> {
                // callback will get the result
                Log.d(TAG, "requestPermission")
                requestPermissionLauncher.launch(
                    permissions
                )
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHost.navController
        val currentFragment = navController.currentDestination?.id
        if (currentFragment != null) {
            outState.putInt("current_fragment", currentFragment)
        }
        outState.putInt("selectedItemId", bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    companion object {
        val TAG = "** MainActivity"
    }
}

