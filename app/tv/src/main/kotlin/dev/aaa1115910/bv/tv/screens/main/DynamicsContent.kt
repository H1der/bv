package dev.aaa1115910.bv.tv.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import dev.aaa1115910.bv.tv.screens.main.home.DynamicsScreen
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.DynamicViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DynamicsContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    dynamicViewModel: DynamicViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("DynamicsContent")

    val dynamicState = rememberLazyListState()

    var focusOnContent by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val currentListOnTop by remember {
        derivedStateOf {
            dynamicState.firstVisibleItemIndex == 0 && dynamicState.firstVisibleItemScrollOffset == 0
        }
    }

    //启动时刷新数据
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            dynamicViewModel.loadMoreVideo()
        }
        scope.launch(Dispatchers.IO) {
            userViewModel.updateUserInfo()
        }
    }

    //监听登录变化
    LaunchedEffect(userViewModel.isLogin) {
        if (userViewModel.isLogin) {
            //login
            userViewModel.updateUserInfo()
            if (dynamicViewModel.dynamicVideoList.isEmpty()) {
                scope.launch(Dispatchers.IO) { dynamicViewModel.loadMoreVideo() }
            }
        } else {
            //logout
            userViewModel.clearUserInfo()
            dynamicViewModel.clearVideoData()
        }
    }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            // 延迟请求焦点，确保UI已经完全渲染
            scope.launch {
                navFocusRequester.requestFocus()
            }
        }
    }

    BackHandler(focusOnContent && !currentListOnTop) {
        logger.fInfo { "onFocusBackToNav - scroll to top" }
        // scroll to top
        scope.launch(Dispatchers.Main) {
            if (Prefs.enableTvAnimations) dynamicState.animateScrollToItem(0) else dynamicState.scrollToItem(0)
        }
    }

    Scaffold(
        modifier = modifier
            .onFocusChanged { hasFocus = it.hasFocus },
        topBar = {
            Box(
                modifier = Modifier.padding(start = 48.dp, top = 24.dp, bottom = 8.dp, end = 48.dp)
            ) {
                Text(
                    text = "动态",
                    style = androidx.tv.material3.MaterialTheme.typography.displaySmall
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .focusRequester(navFocusRequester)
                .onFocusChanged { focusOnContent = it.hasFocus }
        ) {
            DynamicsScreen(lazyListState = dynamicState)
        }
    }
}
