package com.qwert2603.iau_helper

import android.os.Build
import androidx.appcompat.app.AppCompatActivity

interface UpdateHelperFlexible {
    fun start()
    fun completeUpdate()

    companion object {
        const val REQUEST_UPDATE = 18
        const val DEFAULT_CHECK_UPDATE_INTERVAL = 1000L * 60 * 60 * 72 // 72 hours.

        fun create(
            activity: AppCompatActivity,
            onUpdateDownloaded: (UpdateHelperFlexible) -> Unit,
            logger: ((String) -> Unit)? = null,
            checkUpdateInterval: Long = DEFAULT_CHECK_UPDATE_INTERVAL
        ): UpdateHelperFlexible =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UpdateHelperFlexibleImpl(activity, onUpdateDownloaded, logger, checkUpdateInterval)
            } else {
                logger?.invoke("Build.VERSION.SDK_INT = ${Build.VERSION.SDK_INT}. Not working at this API level.")
                UpdateHelperFlexibleStub
            }
    }
}