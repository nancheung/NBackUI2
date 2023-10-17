package com.nancheung.xposed.nbackui.util

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import de.robv.android.xposed.XposedBridge

object Log {
    private var applicationContext: Context? = null

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    fun debug(log: String) {
        XposedBridge.log("NBack：$log")
    }


    /**
     * 弹出toast
     */
    fun toast(text: String,context: Context? = applicationContext) {
        context.let {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}