package com.nancheung.xposed.nbackui.util

import android.content.Context
import de.robv.android.xposed.XSharedPreferences

object SharedPreferencesUtil {

    private var applicationContext: Context? = null

    private var packageName = "com.nancheung.xposed.nbackui"
    private var prefName = "${packageName}_prefs"

    fun init(context: Context) {
        SharedPreferencesUtil.applicationContext = context.applicationContext
    }

    public fun put( key: String, value: String?): Boolean {
        if (value == null) {
            return false
        }

        return applicationContext!!.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit()
            .putString(key, value).commit()
    }

    public fun get(key: String): String? {
        val prefs = applicationContext!!.getSharedPreferences(prefName, Context.MODE_PRIVATE)
        return prefs.getString(key, "")
    }
}