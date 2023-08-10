package com.example.screentime_manager

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
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

        val appNames = apps.map { it.loadLabel(packageManager).toString() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appNames)
        appListView.adapter = adapter

        val appAdapter = SelectedAppAdapter(selectedApps, this)
        appRecyclerView.adapter = appAdapter
        appRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the Room database and DAO
        val database = AppDatabase.getDatabase(applicationContext)
        selectedAppDao = database.selectedAppDao()

        // Load selected apps from the database and update RecyclerView
        GlobalScope.launch {
            val selectedAppsFromDb = selectedAppDao.getAllSelectedApps()
            selectedApps.addAll(selectedAppsFromDb.map { it.packageName })
            runOnUiThread {
                appAdapter.notifyDataSetChanged()
            }

            appListView.setOnItemClickListener { _, _, position, _ ->
                val selectedAppInfo = apps[position]
                val packageName = selectedAppInfo.packageName

                // Update the main activity with the selected app information
                // For example, update a TextView or start a new activity

                // Save the selected item to the list
                selectedApps.add(packageName)

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
