package com.nerdachse.controlnerd

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.nerdachse.controlnerd.databinding.ActivityMainBinding
import com.nerdachse.controlnerd.obswebsocket.ObsWebsocket

class ObsState {
    var isPaused: Boolean = false
    var currentProgramScene: String = ""
    var currentPreviewScene: String = ""
}

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var ws: ObsWebsocket
    var state: ObsState = ObsState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab?.setOnClickListener { view ->
            Snackbar.make(view, "I love you", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_action, R.id.nav_settings
                ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_action,
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        // 10.0.2.2 is the default loopback address of android for local development
        val ipAddress = sharedPreferences.getString("obs_websocket_ip", "10.0.2.2").orEmpty()
        // 4455 is the default port of obs_websocket
        val port = sharedPreferences.getString("obs_websocket_port", "6337").orEmpty()
        val password = sharedPreferences.getString("obs_websocket_password", "").orEmpty()

        ws = ObsWebsocket(ipAddress = ipAddress, port = port, state = state, password = password)
        ws.connect()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        ws.disconnect()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}

fun View.getMainActivity(): MainActivity? {
    var context: Context = this.context
    while (context is ContextWrapper) {
        if (context is MainActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}
