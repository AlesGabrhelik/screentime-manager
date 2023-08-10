package com.example.screentime_manager

import android.app.ActivityManager
import android.content.Context

fun isAnyAppInForeground(context: Context, packageNames: List<String>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses

    appProcesses?.forEach { processInfo ->
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
            packageNames.contains(processInfo.processName)) {
            return true
        }
    }

    return false
}
