package com.qwert2603.iau_test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.qwert2603.iau_helper.UpdateHelperFlexible
import kotlinx.android.synthetic.main.activity_main.*

class FlexibleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        @SuppressLint("SetTextI18n")
        version_TextView.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

        UpdateHelperFlexible(
            activity = this,
            onUpdateDownloaded = { updateHelperFlexible ->
                Snackbar.make(
                    this.findViewById(R.id.root_FrameLayout),
                    R.string.iau_helper_update_is_downloaded,
                    Snackbar.LENGTH_INDEFINITE
                ).apply {
                    this.setAction(R.string.iau_helper_install) { updateHelperFlexible.completeUpdate() }
                    this.show()
                }
            },
            logger = { Log.d("iau_helper", it) }
        )
    }

}