package com.awesome.android.realtimeinterface

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.awesome.android.realtimeinterface.data.UIConfig
import com.awesome.android.realtimeinterface.data.local.PreferenceUtil
import com.awesome.android.realtimeinterface.utils.AppUpdateListener
import com.awesome.android.realtimeinterface.utils.AppUpdateManager
import com.awesome.android.realtimeinterface.utils.Constants
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferenceUtil.initPreference(this)
        // Initialize Firebase Remote Config
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            delay(5000)
            readFirebaseConfig()
        }
    }

    private fun readFirebaseConfig() {
        val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(
                if (packageName.contains(
                        "debug", true
                    )
                ) 0 else 3600
            ).build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val appUpdateRemote =
                    remoteConfig.getValue(Constants.KEY_SYNC_REMOTE_CONFIG).asString()
                val appUpdateLocal = Gson().fromJson(appUpdateRemote, UIConfig::class.java)

                if (appUpdateLocal != null) {
                    AppUpdateManager.checkForUpdate(this,
                        appUpdateLocal,
                        object : AppUpdateListener {
                            override fun onDialogShown() {
                                // Dialog shown, handle any necessary actions
                                // For example, show progress/loading indicator
                            }

                            override fun onDialogDismissed(latestUIConfig: UIConfig?) {
                                applyRemoteConfigToUI(latestUIConfig)
                            }
                        })
                }
            } else {
                Log.d("TAG", "Inside else")
            }
        }
    }

    private fun applyRemoteConfigToUI(latestUIConfig: UIConfig?) {
        // Apply the fetched values to UI elements
        Log.d("TAG", "Inside applyRemoteConfigToUI $latestUIConfig")

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor(latestUIConfig?.color_1)))
        findViewById<TextView>(R.id.topLayout).setBackgroundColor(Color.parseColor(latestUIConfig?.color_1))
        findViewById<ShapeableImageView>(R.id.circleImageView).setBackgroundColor(
            Color.parseColor(
                latestUIConfig?.color_2
            )
        )
        findViewById<MaterialCardView>(R.id.cardView).setCardBackgroundColor(
            Color.parseColor(
                latestUIConfig?.color_3
            )
        )
        findViewById<TextView>(R.id.descTextView).setTextColor(
            Color.parseColor(
                latestUIConfig?.color_3
            )
        )
        findViewById<ConstraintLayout>(R.id.rootLayout).setBackgroundColor(
            Color.parseColor(
                latestUIConfig?.color_4
            )
        )
        findViewById<TextView>(R.id.titleTextView).setTextColor(
            Color.parseColor(
                latestUIConfig?.color_5
            )
        )
        findViewById<TextView>(R.id.subTitleTextView).setTextColor(
            Color.parseColor(
                latestUIConfig?.color_5
            )
        )
        findViewById<TextView>(R.id.topLayout).setTextColor(Color.parseColor(latestUIConfig?.color_5))
    }
}