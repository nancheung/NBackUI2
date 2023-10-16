package com.nancheung.xposed.nbackui


import android.content.res.Configuration
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.nancheung.xposed.nbackui.ui.theme.NBackUITheme

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppsPageTopBar(
    isSearching: Boolean,
    searchText: String,
    onStartSearch: () -> Unit,
    onCancelSearch: () -> Unit,
    onValueChange: (String) -> Unit
) {
    // 软键盘
    val softKeyboard = LocalSoftwareKeyboardController.current
    // 返回键
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    // 搜索框焦点
    val focusRequester = remember { FocusRequester() }

    TopAppBar(
        title = { Text("Apps") },
        navigationIcon = {
            AnimatedVisibility(isSearching) {
                IconButton(onClick = onCancelSearch) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "取消")
                }
            }
        },
        actions = {
            AnimatedVisibility(!isSearching) {
                IconButton(onClick = onStartSearch) {
                    Icon(Icons.Filled.Search, contentDescription = "搜索")
                }
            }
            AnimatedVisibility(isSearching) {
                TextField(
                    value = searchText,
                    onValueChange = onValueChange,
                    placeholder = { Text("搜索APP应用名或包名") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            softKeyboard?.hide()
                        }
                    ),
                    modifier = Modifier
                        .clip(Utils.ROUNDED_SHAPE)
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            // 显示搜索框时的焦点变动
                            if (isSearching) {
                                focusRequester?.requestFocus()
                                softKeyboard?.show()
                            }
                        }
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
fun AppsPageTopBarPreview() {
    NBackUITheme {
        Surface {
            Column {
                AppsPageTopBar(false, "", {}, {}, {})
                AppsPageTopBar(true, "", {}, {}, {})
            }
        }
    }
}
