package com.qwert2603.iau_helper

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

class UpdateHelperFlexible(
    private val activity: AppCompatActivity,
    onUpdateDownloaded: () -> Unit,
    checkUpdateInterval: Long = 1000L * 60 * 60 * 42, // 42 hours.
    private val logger: ((String) -> Unit)? = null
) {

    companion object {
        private const val REQUEST_UPDATE = 18
    }

    private val className = "UpdateHelperFlexible"

    private val appUpdateManager = AppUpdateManagerFactory.create(activity)

    private fun log(s: String) {
        logger?.invoke(s)
    }

    private val updateListener = InstallStateUpdatedListener { state ->
        log("$className updateListener $state")
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            log("$className DOWNLOADED onUpdateDownloaded")
            onUpdateDownloaded()
        }
    }

    private var lastAskToUpdateMillis by prefsLong(
        prefs = PreferenceManager.getDefaultSharedPreferences(activity),
        key = "lastAskToUpdateMillis"
    )

    init {
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
                        "$className onResume appUpdateInfo " +
                                "${appUpdateInfo.availableVersionCode()} ${appUpdateInfo.packageName()} " +
                                "${appUpdateInfo.installStatus()} ${appUpdateInfo.updateAvailability()}"
                    )

                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        onUpdateDownloaded()
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

    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun checkUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            log(
                "$className checkUpdate appUpdateInfo " +
                        "${appUpdateInfo.availableVersionCode()} ${appUpdateInfo.packageName()} " +
                        "${appUpdateInfo.installStatus()} ${appUpdateInfo.updateAvailability()}"
            )

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                log("$className startUpdateFlowForResult")
                startUpdateFlow(appUpdateInfo)
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, REQUEST_UPDATE)
    }
}