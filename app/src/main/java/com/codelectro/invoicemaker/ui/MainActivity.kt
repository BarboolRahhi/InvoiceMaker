package com.codelectro.invoicemaker.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.codelectro.invoicemaker.R
import com.codelectro.invoicemaker.util.DrawerLocker
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DrawerLocker {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.billFragment))

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
        NavigationUI.setupWithNavController(navigation_view, navController)
        //setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, drawer_layout)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                return if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    true
                } else {
                    false
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawer_layout.setDrawerLockMode(lockMode)
    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.billFragment -> {
//                val navOptions = NavOptions.Builder()
//                    .setPopUpTo(R.id.nav_graph, true)
//                    .build()
//
//                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(
//                    R.id.billFragment,
//                    null,
//                    navOptions
//                )
//            }
//            R.id.productFragment -> {
//                if (isValidDestination(R.id.productFragment)) {
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                        .navigate(R.id.productFragment)
//                }
//            }
//
//            R.id.profileFragment -> {
//                if (isValidDestination(R.id.profileFragment)) {
//                    Navigation.findNavController(this, R.id.nav_host_fragment)
//                        .navigate(R.id.profileFragment)
//                }
//            }
//        }
//        drawer_layout.closeDrawer(GravityCompat.START)
//        return true
//    }
//
//
//    private fun isValidDestination(destination: Int): Boolean {
//        return destination != Objects.requireNonNull(
//            Navigation.findNavController(
//                this,
//                R.id.nav_host_fragment
//            ).currentDestination
//        )?.id
//    }

}