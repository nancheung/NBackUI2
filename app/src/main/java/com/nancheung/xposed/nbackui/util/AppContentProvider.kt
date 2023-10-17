package com.nancheung.xposed.nbackui.util

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri


class AppContentProvider : ContentProvider() {
    companion object {
        //用于匹配URI，并返回对应的操作编码
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        val AUTHORITIES = "com.nancheung.xposed.nback.AppContentProvider"

        // 定义 URI 编码
        private val GETPACKAGENAME = 1

        init {
            sUriMatcher.addURI(AUTHORITIES, "getPackageName", GETPACKAGENAME)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val code = sUriMatcher.match(uri)
        if (code == AppContentProvider.GETPACKAGENAME) {
            val cursor = MatrixCursor(arrayOf("result"))
            cursor.addRow(arrayOf(getPackageName()))
            return cursor
        }
        return null
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    fun getPackageName(): String {
        return SharedPreferencesUtil.get("userSelectedPackageName") ?: ""
    }
}