package com.nancheung.xposed.nbackui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.waterfallPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme
import kotlin.streams.toList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NBackUITheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppCardList(getInstalledApps(packageManager))
                }
            }
        }
    }
}

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap?
)

@Composable
fun AppInfoCard(appInfo: AppInfo) {
    // 记录是否展开
    var isExpanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier
        .padding(all = 5.dp)
        .waterfallPadding()
        .clickable { isExpanded = !isExpanded }) {
        val iconShape = RoundedCornerShape(16.dp)

        Image(
            bitmap = appInfo.icon!!,
            contentDescription = "${appInfo.name} icon",
            modifier = Modifier
                .size(40.dp)
                .clip(iconShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, iconShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = appInfo.name, style = MaterialTheme.typography.titleMedium)
            AnimatedVisibility(isExpanded) {
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}

@Composable
fun AppCardList(appInfos: List<AppInfo>) {
    LazyColumn {
        items(appInfos) { appInfo -> AppInfoCard(appInfo) }
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
            AppCardList(SampleData(LocalContext.current.getDrawable(R.mipmap.ic_launcher)!!))
        }
    }
}

fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    return packageManager.getInstalledApplications(0).parallelStream().map { appInfo ->
        val appName = packageManager.getApplicationLabel(appInfo).toString()
        val packageName = appInfo.packageName
        val icon = packageManager.getApplicationIcon(appInfo).toBitmap().asImageBitmap()
        AppInfo(appName, packageName, icon)
    }.toList()
}

fun SampleData(drawable: Drawable): List<AppInfo> {
    val icon = drawable.toBitmap().asImageBitmap()
    return listOf<AppInfo>(
        AppInfo("FujiXWeekly", "com.fujixweekly.FujiXWeekly", icon),
        AppInfo("摩托范", "com.jdd.motorfans", icon),
        AppInfo("Snapseed", "com.niksoftware.Snapseed", icon)
    )
}