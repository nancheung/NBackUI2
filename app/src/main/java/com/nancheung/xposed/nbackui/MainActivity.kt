package com.nancheung.xposed.nbackui

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.textFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.streams.toList


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NBackUITheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppScaffold(getInstalledApps(packageManager),this)
                }
            }
        }
    }

    fun moveTaskToBack() {
        moveTaskToBack(true)
    }
}

/**
 * 圆角样式
 */
private val roundedShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AppScaffold(appInfos: List<AppInfo>,
                activity: MainActivity?) {
    val context = LocalContext.current

    // 展示的APP列表
    var showAppInfos by remember { mutableStateOf(appInfos) }
    // 是否正在搜索，控制相关组件的展示
    var isSearching by remember { mutableStateOf(false) }
    // 搜索框文本
    var searchText by remember { mutableStateOf("") }

    // 搜索框焦点
    val focusRequester = remember { FocusRequester() }
    // 软键盘
    val softKeyboard = LocalSoftwareKeyboardController.current
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
            TopAppBar(
                title = { Text("Apps") },
                navigationIcon = {
                    AnimatedVisibility(isSearching) {
                        IconButton(onClick = {
                            isSearching = false
                            searchText = ""
                            showAppInfos = appInfos
                        }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "取消")
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(!isSearching) {
                        IconButton(onClick = {
                            isSearching = true
                            searchText = ""
                        }) {
                            Icon(Icons.Filled.Search, contentDescription = "搜索")
                        }
                    }
                    AnimatedVisibility(isSearching) {
                        TextField(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                                showAppInfos = searchApp(appInfos, searchText)
                            },
                            placeholder = { Text("搜索APP应用名或包名") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    softKeyboard?.hide()
                                }
                            ),
                            modifier = Modifier
                                .clip(roundedShape)
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    focusRequester.requestFocus()
                                    softKeyboard?.show()
                                }
                        )
                    }
                }
            )
        },
        content = { padding ->
            LazyColumn(contentPadding = padding) {
                items(showAppInfos) { appInfo -> AppInfoCard(appInfo) }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { toast(context, "APP") },
                    icon = {
                        Icon(Icons.Filled.Home, contentDescription = "Apps")
                    }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { toast(context, "更多") },
                    icon = { Icon(Icons.Filled.Favorite , contentDescription = "收藏") }
                )
            }
        }
    )
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap?,
    val packageSize: Long,
    val dataDir: String,
    val externalStorageDir: String,
)

@Composable
fun AppInfoCard(appInfo: AppInfo) {
    val context = LocalContext.current

    // 是否展开按钮
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp)
        .clip(roundedShape)
        .clickable { isExpanded = !isExpanded }) {
        Row(
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                bitmap = appInfo.icon!!,
                contentDescription = "${appInfo.name} icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(roundedShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.tertiary, roundedShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = appInfo.name, style = MaterialTheme.typography.titleMedium)
                Text(text = appInfo.packageName, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = formatSize(appInfo.packageSize),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        AnimatedVisibility(isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { toast(context, "备份中……") },
                    modifier = Modifier
                        .weight(10f)
                        .padding(all = 5.dp)
                ) {
                    Text(text = "备份")
                }
                Button(
                    onClick = { toast(context, "恢复中……") },
                    modifier = Modifier
                        .weight(10f)
                        .padding(all = 5.dp)
                ) {
                    Text(text = "恢复")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun AppCardListPreview() {
    NBackUITheme {
        Surface {
            AppScaffold(
                sampleData(LocalContext.current.getDrawable(R.mipmap.ic_launcher)!!),null
            )
        }
    }
}

private fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    return packageManager.getInstalledApplications(0).parallelStream().map { appInfo ->
        val appName = packageManager.getApplicationLabel(appInfo).toString()
        val packageName = appInfo.packageName
        val icon = packageManager.getApplicationIcon(appInfo).toBitmap().asImageBitmap()
        val packageSize = File(appInfo.sourceDir).length()
        // 获取内部存储目录地址
        val dataDir = File(appInfo.dataDir).absolutePath

        // 获取外部存储目录地址
        val externalStorageDirectory = File(
            Environment.getExternalStorageDirectory().toString() + "/Android/data/" + packageName
        ).absolutePath

        AppInfo(appName, packageName, icon, packageSize, dataDir, externalStorageDirectory)
    }.toList()
}

/**
 * 预览示例数据
 */
private fun sampleData(drawable: Drawable): List<AppInfo> {
    val icon = drawable.toBitmap().asImageBitmap()
    return listOf<AppInfo>(
        AppInfo("FujiXWeekly", "com.fujixweekly.FujiXWeekly", icon, 1024, "", ""),
        AppInfo("摩托范", "com.jdd.motorfans", icon, 890, "", ""),
        AppInfo("Snapseed", "com.niksoftware.Snapseed", icon, 2024000, "", "")
    )
}

/**
 * 格式化文件大小为可读格式
 */
private fun formatSize(size: Long): String {
    if (size <= 0) {
        return "0 B"
    }
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.##").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}

/**
 * 按应用名称或包名搜索
 */
private fun searchApp(appInfos: List<AppInfo>, searchText: String): List<AppInfo> {
    return appInfos.filter { appInfo ->
        appInfo.name.contains(searchText) || appInfo.packageName.contains(searchText)
    }
}

private fun toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}