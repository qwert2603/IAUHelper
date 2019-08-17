package com.qwert2603.iau_test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.qwert2603.iau_helper.UpdateHelperFlexible
import kotlinx.android.synthetic.main.activity_main.*

class FlexibleActivity : AppCompatActivity() {

    private lateinit var updateHelperFlexible: UpdateHelperFlexible

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        @SuppressLint("SetTextI18n")
        version_TextView.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        updateHelperFlexible = UpdateHelperFlexible(
            activity = this,
            onUpdateDownloaded = this::popupSnackbarForCompleteUpdate,
            logger = { Log.d("iau_helper", it) }
        )
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.root_FrameLayout),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { updateHelperFlexible.completeUpdate() }
            show()
        }
    }
}