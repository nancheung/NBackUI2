package com.nancheung.xposed.nbackup

import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap?,
    val packageSize: Long,
    val dataDir: String,
    val externalStorageDir: String?,
    val apkDir: String,
    )