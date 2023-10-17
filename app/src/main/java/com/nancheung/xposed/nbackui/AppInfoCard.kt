package com.nancheung.xposed.nbackui


import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme
import com.nancheung.xposed.nbackui.util.Log
import com.nancheung.xposed.nbackui.util.Utils
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun AppInfoCard(
    appInfo: AppInfo,
    onclick: () -> Unit
) {
    val context = LocalContext.current

    // 是否展开按钮
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 8.dp, end = 8.dp)
        .clip(Utils.ROUNDED_SHAPE)
        .clickable { onclick() }) {
        Row(
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            appInfo.icon?.let {
                Image(
                    bitmap = appInfo.icon,
                    contentDescription = "${appInfo.name} icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(Utils.ROUNDED_SHAPE)
                        .border(1.5.dp, MaterialTheme.colorScheme.tertiary, Utils.ROUNDED_SHAPE)
                )
            }
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
                    onClick = { Log.toast("备份中……") },
                    modifier = Modifier
                        .weight(10f)
                        .padding(all = 5.dp)
                ) {
                    Text(text = "备份")
                }
                Button(
                    onClick = { Log.toast("恢复中……") },
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
fun AppCardPreview() {
    val current = LocalContext.current
    val appInfos = Utils.mockAppInfo(current.getDrawable(R.mipmap.ic_launcher)!!)

    NBackUITheme {
        Surface {
            LazyColumn() {
                items(appInfos) { appInfo -> AppInfoCard(appInfo) {} }
            }
        }
    }
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
