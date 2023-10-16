package com.nancheung.xposed.nbackui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(appInfo: AppInfo, onHide: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onHide,
        sheetState = sheetState
    ) {
        SheetContent(appInfo) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onHide.invoke()
                }
            }
        }
    }
}

@Composable
fun SheetContent(appInfo: AppInfo, onHide: () -> Unit) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(20.dp)) {
        val cardPadding = Modifier.padding(top = 10.dp, bottom = 10.dp)
        Card(modifier = cardPadding) {
            AppInfoCard(appInfo) {}
        }

        Card(modifier = cardPadding) {
            val textPadding = Modifier.padding(top = 2.dp, bottom = 2.dp)
            Row(modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
                Column {
                    Text(text = "安装包路径", modifier = textPadding)
                    Text(text = "data目录", modifier = textPadding)
                    appInfo.externalStorageDir?.let {
                        Text(text = "外部存储目录", modifier = textPadding)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    val onClipboard: (String) -> () -> Unit = { text: String ->
                        {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Copied Text", text))

                            Utils.toast(context, "已复制")
                        }
                    }

                    Text(
                        text = appInfo.apkDir,
                        modifier = textPadding.clickable(onClick = onClipboard(appInfo.apkDir))
                    )
                    Text(
                        text = appInfo.dataDir,
                        modifier = textPadding.clickable(onClick = onClipboard(appInfo.dataDir))
                    )
                    appInfo.externalStorageDir?.let {
                        Text(
                            text = appInfo.externalStorageDir,
                            modifier = textPadding.clickable(onClick = onClipboard(appInfo.externalStorageDir))
                        )
                    }
                }
            }
        }
        Button(modifier = cardPadding.fillMaxWidth(), onClick = onHide) {
            Text("备份")
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
fun AppInfoBottomSheetPreview() {
    val current = LocalContext.current
    val appInfos = Utils.mockAppInfo(current.getDrawable(R.mipmap.ic_launcher)!!)
    val appInfo = appInfos[0]
    val appInfo2 = appInfos[2]

    NBackUITheme {
        Surface {
            Column {
                SheetContent(appInfo) {}
                SheetContent(appInfo2) {}
            }
        }
    }
}
