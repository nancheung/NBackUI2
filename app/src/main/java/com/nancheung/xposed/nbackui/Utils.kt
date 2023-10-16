package com.nancheung.xposed.nbackui

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap

class Utils {
    companion object {
        val ROUNDED_SHAPE = RoundedCornerShape(16.dp)


        /**
         * 模拟AppInfo数据
         */
        fun mockAppInfo(drawable: Drawable): List<AppInfo> {
            val icon = drawable.toBitmap().asImageBitmap()
            return listOf<AppInfo>(
                AppInfo("FujiXWeekly", "com.fujixweekly.FujiXWeekly", icon, 1024, "", ""),
                AppInfo("摩托范", "com.jdd.motorfans", icon, 890, "", ""),
                AppInfo("Snapseed", "com.niksoftware.Snapseed", icon, 2024000, "", "")
            )
        }

        /**
         * 弹出toast
         */
        fun toast(context: Context, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}