package com.example.screentime_manager

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appListView: ListView
    private lateinit var appRecyclerView: RecyclerView
    private lateinit var chooseAppButton: FloatingActionButton
    private val selectedApps = mutableListOf<String>() // Initialize an empty list
    private lateinit var selectedAppDao: SelectedAppDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appListView = findViewById(R.id.appListView)
        appRecyclerView = findViewById(R.id.appRecyclerView)
        chooseAppButton = findViewById(R.id.chooseAppButton)

        val packageManager: PackageManager = packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val userInstalledApps = apps.filter { app ->
            packageManager.getLaunchIntentForPackage(app.packageName) != null// &&
                   // (app.flags and ApplicationInfo.FLAG_SYSTEM == 0) &&
                    //(app.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP == 0)
        }
        val appNames = userInstalledApps.map { it.loadLabel(packageManager).toString() }
        Log.d("MyTag", appNames.toString())
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appNames)
        appListView.adapter = adapter

        val appAdapter = SelectedAppAdapter(selectedApps, this)
        appRecyclerView.adapter = appAdapter
        appRecyclerView.layoutManager = LinearLayoutManager(this)

        // Init database
        val database = AppDatabase.getDatabase(applicationContext)
        selectedAppDao = database.selectedAppDao()

        // Load selected apps
        GlobalScope.launch {
            val selectedAppsFromDb = selectedAppDao.getAllSelectedApps()
            selectedApps.addAll(selectedAppsFromDb.map { it.packageName })
            runOnUiThread {
                appAdapter.notifyDataSetChanged()
                appRecyclerView.visibility = ListView.VISIBLE
            }

            appListView.setOnItemClickListener { _, _, position, _ ->
                val selectedAppInfo = userInstalledApps[position]
                val packageName = selectedAppInfo.packageName

                // Save the selected item to the list
                if (packageName !in selectedApps) {
                    selectedApps.add(packageName)
                    saveSelectedApp(packageName)
                }


                // Hide the ListView and show the button again
                appListView.visibility = ListView.GONE
                chooseAppButton.visibility = FloatingActionButton.VISIBLE
                appRecyclerView.visibility = View.VISIBLE
            }

            chooseAppButton.setOnClickListener {
                appListView.visibility = ListView.VISIBLE
                chooseAppButton.visibility = FloatingActionButton.GONE
            }
        }

    }
    fun saveSelectedApp(packageName: String) {
        GlobalScope.launch {
            selectedAppDao.insert(SelectedApp(packageName))
        }
    }

    fun deleteSelectedApp(packageName: String) {
        GlobalScope.launch {
            selectedAppDao.deleteSelectedApp(packageName)
        }
    }


}
