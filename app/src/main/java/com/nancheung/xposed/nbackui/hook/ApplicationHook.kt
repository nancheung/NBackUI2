package com.nancheung.xposed.nbackui.hook

import android.app.Activity
import android.app.AndroidAppHelper
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.nancheung.xposed.nbackui.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class ApplicationHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        Log.debug("开始加载${lpparam.packageName}")

        XposedHelpers.findAndHookMethod(
            "android.app.Activity", lpparam.classLoader, "onCreate", Bundle::class.java,
            MethodHook(lpparam)
        )
    }
}

class MethodHook(private val lpparam: XC_LoadPackage.LoadPackageParam) : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        // 获取上下文
        val context = (param.thisObject as Activity).applicationContext

        var targetPackageName: String? = ""
        // 在这里使用contentResolver进行操作
        val contentResolver = context.contentResolver
        val uri = Uri.parse("content://com.nancheung.xposed.nback.AppContentProvider/getPackageName")
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            // 获取手动配置的包名
            targetPackageName = cursor.getString(0)
            cursor.close()
        }
        if (targetPackageName!=lpparam.packageName) {
            Log.debug("取消加载${lpparam.packageName}，应当为${targetPackageName}")
            return
        }

        // 获取app的数据目录
        var toastStr: String
        var dataDir: String? = null

        try {
            // 获取app的数据目录
            dataDir = getDataDir(lpparam.packageName)
            toastStr = "数据目录：$dataDir"
        } catch (e: PackageManager.NameNotFoundException) {
            toastStr = "找不到包名：" + e.localizedMessage
        } catch (e: Exception) {
            toastStr = "获取数据目录失败：" + e.localizedMessage
        }

        Log.toast(toastStr, context)

        if (dataDir == null) {
            return
        }

        // 内部数据打包文件将保存到外部存储目录
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir == null) {
            Log.toast("没有外部存储目录", context)
            return
        }

        val zipFile = File(externalFilesDir, "data.zip")
        if (zipFile.exists()) {
            Log.toast("zip已存在", context)
            zipFile.delete()
        }

        // 打包文件
        zip(dataDir, zipFile)

        Log.toast("zip备份至：" + externalFilesDir.absolutePath, context)
    }
}

@Throws(IOException::class)
private fun zip(dataDir: String, zipFile: File) {
    ZipOutputStream(FileOutputStream(zipFile)).use { zipOutputStream ->
        zipDirectory(
            "",
            File(dataDir),
            zipOutputStream
        )
    }
}

@Throws(IOException::class)
private fun zipDirectory(path: String, directory: File, zipOutputStream: ZipOutputStream) {
    val files = directory.listFiles()
    val buffer = ByteArray(4096)
    for (file in files) {
        if (file.isDirectory) {
            zipDirectory(path + file.name + "/", file, zipOutputStream)
            continue
        }
        FileInputStream(file).use { fileInputStream ->
            val zipEntry = ZipEntry(path + file.name)
            zipOutputStream.putNextEntry(zipEntry)
            var length: Int
            while (fileInputStream.read(buffer).also { length = it } > 0) {
                zipOutputStream.write(buffer, 0, length)
            }
        }
    }
}

/**
 * 获取app的数据目录
 *
 * @param packageName app包名
 * @return 数据目录路径
 * @throws PackageManager.NameNotFoundException 找不到包名
 */
@Throws(PackageManager.NameNotFoundException::class)
private fun getDataDir(packageName: String): String {
    val context = AndroidAppHelper.currentApplication()
        .createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY)
    return context.applicationInfo.dataDir
}