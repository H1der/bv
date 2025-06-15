package dev.aaa1115910.bv.tv.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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
import dev.aaa1115910.bv.tv.component.HomeTopNavItem
import dev.aaa1115910.bv.tv.component.TopNav
import dev.aaa1115910.bv.tv.screens.main.home.PopularScreen
import dev.aaa1115910.bv.tv.screens.main.home.RecommendScreen
import dev.aaa1115910.bv.tv.screens.main.pgc.AnimeContent
import dev.aaa1115910.bv.tv.screens.main.pgc.DocumentaryContent
import dev.aaa1115910.bv.tv.screens.main.pgc.GuoChuangContent
import dev.aaa1115910.bv.tv.screens.main.pgc.MovieContent
import dev.aaa1115910.bv.tv.screens.main.pgc.TvContent
import dev.aaa1115910.bv.tv.screens.main.pgc.VarietyContent
import dev.aaa1115910.bv.tv.util.TvAnimationUtils
import dev.aaa1115910.bv.util.Prefs
import dev.aaa1115910.bv.util.fInfo
import dev.aaa1115910.bv.util.requestFocus
import dev.aaa1115910.bv.viewmodel.UserViewModel
import dev.aaa1115910.bv.viewmodel.home.PopularViewModel
import dev.aaa1115910.bv.viewmodel.home.RecommendViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcAnimeViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcDocumentaryViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcGuoChuangViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcMovieViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcTvViewModel
import dev.aaa1115910.bv.viewmodel.pgc.PgcVarietyViewModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navFocusRequester: FocusRequester,
    recommendViewModel: RecommendViewModel = koinViewModel(),
    popularViewModel: PopularViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel(),
    pgcAnimeViewModel: PgcAnimeViewModel = koinViewModel(),
    pgcGuoChuangViewModel: PgcGuoChuangViewModel = koinViewModel(),
    pgcMovieViewModel: PgcMovieViewModel = koinViewModel(),
    pgcDocumentaryViewModel: PgcDocumentaryViewModel = koinViewModel(),
    pgcTvViewModel: PgcTvViewModel = koinViewModel(),
    pgcVarietyViewModel: PgcVarietyViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logger = KotlinLogging.logger("HomeContent")

    val recommendState = rememberLazyListState()
    val popularState = rememberLazyListState()
    val animeState = rememberLazyListState()
    val guoChuangState = rememberLazyListState()
    val movieState = rememberLazyListState()
    val documentaryState = rememberLazyListState()
    val tvState = rememberLazyListState()
    val varietyState = rememberLazyListState()

    var selectedTab by remember { mutableStateOf(HomeTopNavItem.Recommend) }
    var focusOnContent by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val currentListOnTop by remember {
        derivedStateOf {
            with(
                when (selectedTab) {
                    HomeTopNavItem.Recommend -> recommendState
                    HomeTopNavItem.Popular -> popularState
                    HomeTopNavItem.Anime -> animeState
                    HomeTopNavItem.GuoChuang -> guoChuangState
                    HomeTopNavItem.Movie -> movieState
                    HomeTopNavItem.Documentary -> documentaryState
                    HomeTopNavItem.Tv -> tvState
                    HomeTopNavItem.Variety -> varietyState
                }
            ) {
                firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0
            }
        }
    }

    //启动时刷新数据
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            recommendViewModel.loadMore()
        }
        scope.launch(Dispatchers.IO) {
            popularViewModel.loadMore()
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
        } else {
            //logout
            userViewModel.clearUserInfo()
        }
    }

    LaunchedEffect(hasFocus) {
        if (hasFocus) {
            navFocusRequester.requestFocus()
        }
    }

    BackHandler(focusOnContent) {
        logger.fInfo { "onFocusBackToNav" }
        navFocusRequester.requestFocus(scope)
        // scroll to top
        scope.launch(Dispatchers.Main) {
            when (selectedTab) {
                HomeTopNavItem.Recommend -> if (Prefs.enableTvAnimations) recommendState.animateScrollToItem(0) else recommendState.scrollToItem(0)
                HomeTopNavItem.Popular -> if (Prefs.enableTvAnimations) popularState.animateScrollToItem(0) else popularState.scrollToItem(0)
                HomeTopNavItem.Anime -> if (Prefs.enableTvAnimations) animeState.animateScrollToItem(0) else animeState.scrollToItem(0)
                HomeTopNavItem.GuoChuang -> if (Prefs.enableTvAnimations) guoChuangState.animateScrollToItem(0) else guoChuangState.scrollToItem(0)
                HomeTopNavItem.Movie -> if (Prefs.enableTvAnimations) movieState.animateScrollToItem(0) else movieState.scrollToItem(0)
                HomeTopNavItem.Documentary -> if (Prefs.enableTvAnimations) documentaryState.animateScrollToItem(0) else documentaryState.scrollToItem(0)
                HomeTopNavItem.Tv -> if (Prefs.enableTvAnimations) tvState.animateScrollToItem(0) else tvState.scrollToItem(0)
                HomeTopNavItem.Variety -> if (Prefs.enableTvAnimations) varietyState.animateScrollToItem(0) else varietyState.scrollToItem(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .onFocusChanged { hasFocus = it.hasFocus },
        topBar = {
            TopNav(
                modifier = Modifier
                    .focusRequester(navFocusRequester)
                    .padding(end = 80.dp),
                items = HomeTopNavItem.entries,
                isLargePadding = !focusOnContent && currentListOnTop,
                onSelectedChanged = { nav ->
                    selectedTab = nav as HomeTopNavItem
                    when (nav) {
                        HomeTopNavItem.Recommend -> {}
                        HomeTopNavItem.Popular -> {}
                        HomeTopNavItem.Anime -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                        HomeTopNavItem.GuoChuang -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                        HomeTopNavItem.Movie -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                        HomeTopNavItem.Documentary -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                        HomeTopNavItem.Tv -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                        HomeTopNavItem.Variety -> {
                            // PGC ViewModels automatically load data in init, no need to manually trigger
                        }
                    }
                },
                onClick = { nav ->
                    when (nav) {
                        HomeTopNavItem.Recommend -> {
                            logger.fInfo { "clear recommend data" }
                            recommendViewModel.clearData()
                            logger.fInfo { "reload recommend data" }
                            scope.launch(Dispatchers.IO) { recommendViewModel.loadMore() }
                        }

                        HomeTopNavItem.Popular -> {
                            logger.fInfo { "clear popular data" }
                            popularViewModel.clearData()
                            logger.fInfo { "reload popular data" }
                            scope.launch(Dispatchers.IO) { popularViewModel.loadMore() }
                        }

                        HomeTopNavItem.Anime -> {
                            logger.fInfo { "reload anime data" }
                            scope.launch(Dispatchers.IO) {
                                pgcAnimeViewModel.reloadAll()
                            }
                        }

                        HomeTopNavItem.GuoChuang -> {
                            logger.fInfo { "reload guochuang data" }
                            scope.launch(Dispatchers.IO) {
                                pgcGuoChuangViewModel.reloadAll()
                            }
                        }

                        HomeTopNavItem.Movie -> {
                            logger.fInfo { "reload movie data" }
                            scope.launch(Dispatchers.IO) {
                                pgcMovieViewModel.reloadAll()
                            }
                        }

                        HomeTopNavItem.Documentary -> {
                            logger.fInfo { "reload documentary data" }
                            scope.launch(Dispatchers.IO) {
                                pgcDocumentaryViewModel.reloadAll()
                            }
                        }

                        HomeTopNavItem.Tv -> {
                            logger.fInfo { "reload tv data" }
                            scope.launch(Dispatchers.IO) {
                                pgcTvViewModel.reloadAll()
                            }
                        }

                        HomeTopNavItem.Variety -> {
                            logger.fInfo { "reload variety data" }
                            scope.launch(Dispatchers.IO) {
                                pgcVarietyViewModel.reloadAll()
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .onFocusChanged { focusOnContent = it.hasFocus }
        ) {
            AnimatedContent(
                targetState = selectedTab,
                label = "home animated content",
                transitionSpec = TvAnimationUtils.optimizedHorizontalSlideTransition(
                    coefficient = 10,
                    duration = 200
                )
            ) { screen ->
                when (screen) {
                    HomeTopNavItem.Recommend -> RecommendScreen(lazyListState = recommendState)
                    HomeTopNavItem.Popular -> PopularScreen(lazyListState = popularState)
                    HomeTopNavItem.Anime -> AnimeContent(lazyListState = animeState, pgcViewModel = pgcAnimeViewModel)
                    HomeTopNavItem.GuoChuang -> GuoChuangContent(lazyListState = guoChuangState, pgcViewModel = pgcGuoChuangViewModel)
                    HomeTopNavItem.Movie -> MovieContent(lazyListState = movieState, pgcViewModel = pgcMovieViewModel)
                    HomeTopNavItem.Documentary -> DocumentaryContent(lazyListState = documentaryState, pgcViewModel = pgcDocumentaryViewModel)
                    HomeTopNavItem.Tv -> TvContent(lazyListState = tvState, pgcViewModel = pgcTvViewModel)
                    HomeTopNavItem.Variety -> VarietyContent(lazyListState = varietyState, pgcViewModel = pgcVarietyViewModel)
                }
            }
        }
    }
}
