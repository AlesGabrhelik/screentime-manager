package com.example.screentime_manager

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK


class ForegroundAppCheckWorker(context: Context, params: WorkerParameters) : Worker(context, params){

    override fun doWork(): Result {

        val database = AppDatabase.getDatabase(applicationContext)

        // Access your DAO and retrieve the list of apps
        GlobalScope.launch {
            val appList = database.selectedAppDao().getAllSelectedApps()
            val packageNamesList: List<String> = appList.map { it.packageName}
            if (isAnyAppInForeground(applicationContext, packageNamesList)) {
                showHomeScreen(applicationContext)

            }

            }
        return Result.success()

        }
        // Perform your background task with the appList
        // ...
        fun showHomeScreen(context: Context) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = FLAG_ACTIVITY_NEW_TASK
            context.startActivity(homeIntent)
        }

    }
