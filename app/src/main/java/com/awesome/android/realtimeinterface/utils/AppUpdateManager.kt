package com.awesome.android.realtimeinterface.utils

import android.content.Context
import android.util.Log
import com.awesome.android.realtimeinterface.R
import com.awesome.android.realtimeinterface.data.UIConfig
import com.awesome.android.realtimeinterface.data.local.PreferenceUtil


interface AppUpdateListener {
    fun onDialogShown()
    fun onDialogDismissed(latestUIConfig: UIConfig?)
}

object AppUpdateManager {

    fun checkForUpdate(context: Context, uIConfig: UIConfig, listener: AppUpdateListener?) {
//        val currentUIVersionCode = PreferenceUtil.UI_VERSION
        val latestUIVersion = uIConfig.ui_version

        Log.d("TAG", "Inside checkForUpdate $uIConfig")

//        if (currentUIVersionCode < latestUIVersion) {
            PreferenceUtil.UI_VERSION = latestUIVersion
            showUpdateDialog(context, uIConfig, listener)
//        }
    }

    private fun showUpdateDialog(
        context: Context,
        latestUIConfig: UIConfig?,
        listener: AppUpdateListener?
    ) {
        listener?.onDialogShown()
        val builder = android.app.AlertDialog.Builder(context)
            .setTitle("Update UI").setMessage(
                "Found some exciting changes here, " +
                        "Please Refresh this screen?"
            )
            .setCancelable(false)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("Update") { _, _ ->
                listener?.onDialogDismissed(latestUIConfig)
            }
            .show()

        builder.setCanceledOnTouchOutside(false)
    }
}