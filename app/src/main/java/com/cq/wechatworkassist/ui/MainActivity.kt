package com.cq.wechatworkassist.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.cq.wechatworkassist.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private var mAppBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            if (FloatWindowManager.isShow()) {
                FloatWindowManager.removeBallView(this)
            } else {
                if (AccessibilityUtil.checkSetting(this, AccessibilityService::class.java)) {
                    if (PermissionManager.checkFloatPermission(this)) {
                        FloatWindowManager.addBallView(this)
                    } else {
                        showFloatDialog()
                    }
                }
            }

        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
        )
            .setDrawerLayout(drawer)
            .build()
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
//        NavigationUI.setupWithNavController(toolbar, navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            run {
//            supportInvalidateOptionsMenu()
//                invalidateOptionsMenu()
            }
        }

    }


    fun requestFloatPermission(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent2 = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent2, 1)
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:" + context?.packageName)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (PermissionManager.checkFloatPermission(this)) {
                Toast.makeText(this, "悬浮窗权限申请成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "悬浮窗权限申请失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun showFloatDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限设置")
            .setMessage("找到【" + getString(R.string.aby_label) + "】并授权【悬浮窗】权限")
            .setPositiveButton(
                R.string.common_ok
            ) { _, _ -> requestFloatPermission(this) }
            .setCancelable(false)
            .show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
    }
}