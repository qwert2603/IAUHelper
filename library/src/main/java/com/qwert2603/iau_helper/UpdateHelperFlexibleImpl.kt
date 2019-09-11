package com.qwert2603.iau_helper

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.preference.PreferenceManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import java.lang.ref.WeakReference

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class UpdateHelperFlexibleImpl(
    private val activity: AppCompatActivity,
    private val onUpdateDownloaded: (UpdateHelperFlexible) -> Unit,
    private val logger: ((String) -> Unit)?,
    private val checkUpdateInterval: Long
) : UpdateHelperFlexible {

    private val appUpdateManager = AppUpdateManagerFactory.create(activity.applicationContext)

    private fun log(s: String) {
        logger?.invoke(s)
    }

    private val updateListener = InstallStateUpdatedListener { state ->
        log("updateListener $state")
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            log("DOWNLOADED onUpdateDownloaded")
            onUpdateDownloaded(this)
        }
    }

    private var lastAskToUpdateMillis by prefsLong(
        prefs = PreferenceManager.getDefaultSharedPreferences(activity.applicationContext),
        key = "lastAskToUpdateMillis"
    )

    override fun start() {
        val activityWeak = WeakReference(activity)

        @Suppress("UNUSED")
        activity.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                appUpdateManager.registerListener(updateListener)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                appUpdateManager.unregisterListener(updateListener)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                    log(
                        "onResume appUpdateInfo " +
                                "${appUpdateInfo.availableVersionCode()} ${appUpdateInfo.packageName()} " +
                                "${appUpdateInfo.installStatus()} ${appUpdateInfo.updateAvailability()}"
                    )

                    if (activityWeak.get()?.isDestroyed == false && appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        onUpdateDownloaded(this@UpdateHelperFlexibleImpl)
                    }
                }
            }
        })

        val nowMillis = System.currentTimeMillis()
        if (lastAskToUpdateMillis < nowMillis - checkUpdateInterval) {
            lastAskToUpdateMillis = nowMillis
            checkUpdate()
        }
    }

    override fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            log(
                "checkUpdate appUpdateInfo " +
                        "${appUpdateInfo.availableVersionCode()} ${appUpdateInfo.packageName()} " +
                        "${appUpdateInfo.installStatus()} ${appUpdateInfo.updateAvailability()}"
            )

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                log("startUpdateFlowForResult")
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            activity,
            UpdateHelperFlexible.REQUEST_UPDATE
        )
    }
}