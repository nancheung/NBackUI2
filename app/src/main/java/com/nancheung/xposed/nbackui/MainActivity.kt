package com.nancheung.xposed.nbackui

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.drawable.toBitmap
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme
import com.nancheung.xposed.nbackui.util.Log
import com.nancheung.xposed.nbackui.util.SharedPreferencesUtil
import com.nancheung.xposed.nbackui.util.Utils
import java.io.File
import kotlin.streams.toList


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.init(this)
        SharedPreferencesUtil.init(this)

        setContent {
            NBackUITheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppScaffold(getInstalledApps(packageManager), this)
                }
            }
        }
    }

    fun moveTaskToBack() {
        moveTaskToBack(true)
    }
}

@Composable
fun AppScaffold(
    appInfos: List<AppInfo>,
    activity: MainActivity?
) {
    val context = LocalContext.current

    // 展示的APP列表
    var showAppInfos by remember { mutableStateOf(appInfos) }
    // 是否正在搜索，控制相关组件的展示
    var isSearching by remember { mutableStateOf(false) }
    // 搜索框文本
    var searchText by remember { mutableStateOf("") }
    // 选中的app
    var selectAppInfo: AppInfo by remember { mutableStateOf(Utils.mockAppInfo(null)[0]) }

    // 底部弹窗
    var isShowBottomSheet by remember { mutableStateOf(false) }

    // 返回键
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // 自定义返回事件
    DisposableEffect(isSearching) {
        val callback = onBackPressedDispatcher?.addCallback {
            // 搜索时，取消搜索
            if (isSearching) {
                isSearching = false
                searchText = ""
                showAppInfos = appInfos
            } else {
                // 不搜索时，app置于后台
                activity?.moveTaskToBack()
            }
        }
        onDispose { callback?.remove() }
    }

    Scaffold(
        topBar = {
            AppsPageTopBar(
                isSearching, searchText,
                {
                    isSearching = true
                    searchText = ""

                }, {
                    isSearching = false
                    showAppInfos = appInfos
                }, {
                    searchText = it
                    showAppInfos = searchApp(appInfos, searchText)
                }
            )
        },
        content = { padding ->
            LazyColumn(contentPadding = padding) {
                items(showAppInfos) { appInfo ->
                    AppInfoCard(appInfo) {
                        isShowBottomSheet = true
                        selectAppInfo = appInfo
                    }
                }
            }
            if (isShowBottomSheet) {
                AppInfoBottomSheet(selectAppInfo) {
                    isShowBottomSheet = false
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { Log.toast("APP") },
                    icon = {
                        Icon(Icons.Filled.Home, contentDescription = "Apps")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { Log.toast("更多") },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "收藏") }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun AppMainPreview() {
    NBackUITheme {
        Surface {
            AppScaffold(
                Utils.mockAppInfo(LocalContext.current.getDrawable(R.mipmap.ic_launcher)!!), null
            )
        }
    }
}

private fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    return packageManager.getInstalledApplications(0).parallelStream().map { appInfo ->
        val appName = packageManager.getApplicationLabel(appInfo).toString()
        val packageName = appInfo.packageName
        val icon = packageManager.getApplicationIcon(appInfo).toBitmap().asImageBitmap()
        val sourceDir = appInfo.sourceDir
        val packageSize = File(sourceDir).length()
        // 获取内部存储目录地址
        val dataDir = File(appInfo.dataDir).absolutePath

        // 获取外部存储目录地址
        val externalStorageDirectory = File(
            Environment.getExternalStorageDirectory().toString() + "/Android/data/" + packageName
        ).absolutePath

        AppInfo(
            appName,
            packageName,
            icon,
            packageSize,
            dataDir,
            externalStorageDirectory,
            sourceDir
        )
    }.toList()
}

/**
 * 按应用名称或包名搜索
 */
private fun searchApp(appInfos: List<AppInfo>, searchText: String): List<AppInfo> {
    return appInfos.filter { appInfo ->
        appInfo.name.contains(searchText) || appInfo.packageName.contains(searchText)
    }
}