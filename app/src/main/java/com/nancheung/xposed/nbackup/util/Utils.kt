package com.nancheung.xposed.nbackup.util

import android.graphics.drawable.Drawable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.nancheung.xposed.nbackup.AppInfo

class Utils {
    companion object {
        val ROUNDED_SHAPE = RoundedCornerShape(16.dp)


        /**
         * 模拟AppInfo数据
         */
        fun mockAppInfo(drawable: Drawable?): List<AppInfo> {
            val icon = drawable?.toBitmap()?.asImageBitmap()
            return listOf<AppInfo>(
                AppInfo(
                    "FujiXWeekly",
                    "com.fujixweekly.FujiXWeekly",
                    icon,
                    1024,
                    "/data/user/0/com.fujixweekly.FujiXWeekly",
                    "/storage/emulated/0/Android/data/com.fujixweekly.FujiXWeekly",
                    "/data/app/~~OHRrsxXSz9nUTJuucvoGew==/com.jdd.motorfans-R1EqH7KgeRQTdVKO_RyPwA==/base.apk"
                ),
                AppInfo(
                    "摩托范",
                    "com.jdd.motorfans",
                    icon,
                    890,
                    "/data/user/0/com.jdd.motorfans",
                    "/storage/emulated/0/Android/data/com.jdd.motorfans",
                    "/data/app/~~OHRrsxXSz9nUTJuucvoGew==/com.jdd.motorfans-R1EqH7KgeRQTdVKO_RyPwA==/base.apk"
                ),
                AppInfo(
                    "附近设备扫描",
                    "com.niksoftware.Snapseed",
                    icon,
                    2024000,
                    "/data/user/0/com.samsung.android.easysetup",
                    null,
                    "/data/app/~~OHRrsxXSz9nUTJuucvoGew==/com.jdd.motorfans-R1EqH7KgeRQTdVKO_RyPwA==/base.apk"
                )
            )
        }
    }
}