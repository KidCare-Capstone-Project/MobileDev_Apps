package com.callcenter.kidcare.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.LocalNavAnimatedVisibilityScope
import com.callcenter.kidcare.ui.components.KidCareSurface
import com.callcenter.kidcare.ui.home.about.AboutUs
import com.callcenter.kidcare.ui.home.account.myaccount
import com.callcenter.kidcare.ui.home.admin.AdminPanel
import com.callcenter.kidcare.ui.home.admin.EditUser
import com.callcenter.kidcare.ui.home.admin.Users
import com.callcenter.kidcare.ui.home.admin.orderanmasuk.IncomingOrdersScreen
import com.callcenter.kidcare.ui.home.admin.orderanmasuk.detail.OrderDetailScreen
import com.callcenter.kidcare.ui.home.admin.tambahartikel.AddArtikel
import com.callcenter.kidcare.ui.home.admin.tambahposter.AddPoster
import com.callcenter.kidcare.ui.home.admin.tambahproduk.AddProduct
import com.callcenter.kidcare.ui.home.admin.tambahresepmenu.AddResepMenu
import com.callcenter.kidcare.ui.home.admin.tambahresepmpasi.AddResepMpasi
import com.callcenter.kidcare.ui.home.article.ArticleDetailScreen
import com.callcenter.kidcare.ui.home.article.ArticleListScreen
import com.callcenter.kidcare.ui.home.article.ArticleRecommendation
import com.callcenter.kidcare.ui.home.article.BookmarkScreen
import com.callcenter.kidcare.ui.home.childprofile.AddActivityScreen
import com.callcenter.kidcare.ui.home.childprofile.ChildDetailScreen
import com.callcenter.kidcare.ui.home.childprofile.EditChildProfile
import com.callcenter.kidcare.ui.home.childprofile.diaryaktivitas.edit.EditActivityScreen
import com.callcenter.kidcare.ui.home.childprofile.diarymenu.add.AddMenuScreen
import com.callcenter.kidcare.ui.home.childprofile.diarymenu.detail.RecipeDetailScreen
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.ibuhamil.IbuHamilScreen
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.infoproduk.detail.ProductDetailScreen
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.infoproduk.info_produk
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.penangananstunting.penanganan_stunting
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.pencegahanstunting.pencegahan_stunting
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.predict.Predict
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.predictv2.PredictOnline
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.resepmpasi.BookmarkResepScreen
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.resepmpasi.detail.ResepDetailScreen
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.resepmpasi.resep_mpasi
import com.callcenter.kidcare.ui.home.pesanan.PaymentsMidtrans
import com.callcenter.kidcare.ui.home.pesanan.Pesanan
import com.callcenter.kidcare.ui.options.videoDetail.VideoDetailScreen
import com.callcenter.kidcare.ui.theme.KidCareTheme
import java.util.Locale

fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        fadeIn(animationSpec = spring())
    },
    exitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        fadeOut(animationSpec = spring())
    },
    popEnterTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? =
        enterTransition,
    popExitTransition: (
    @JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route,
        arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition
    ) {
        CompositionLocalProvider(
            LocalNavAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun NavGraphBuilder.addHomeGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    composable(HomeSections.FEED.route) { from ->
        Feed(modifier, navController)
    }
    composable(HomeSections.PESANAN.route) {
        Pesanan(navController = navController, modifier = Modifier)
    }
    composable(HomeSections.PROFILE.route) {
        Profile(navController)
    }
    composable("aboutus") {
        AboutUs(navController)
    }
    composable("account") {
        myaccount()
    }
    composable("admin") {
        AdminPanel(navController)
    }
    composable("users") {
        Users(navController)
    }
    composable("add_article") {
        AddArtikel(navController)
    }
    composable(
        "admin/editUser/{userUuid}",
        arguments = listOf(navArgument("userUuid") { type = NavType.StringType })
    ) { backStackEntry ->
        val userUuid = backStackEntry.arguments?.getString("userUuid") ?: ""
        EditUser(navController = navController, userUuid = userUuid)
    }
    composable(
        "child_detail/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: ""
        if (childId.isNotBlank()) {
            ChildDetailScreen(childId = childId, navController = navController)
        } else {
            Log.e("Navigation", "Invalid child ID")
        }
    }
    composable(
        "editChildProfile/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: ""
        EditChildProfile(childId = childId, navController = navController)
    }

    composable("articleRecommendation") {
        ArticleRecommendation(
            onArticleClick = { uuid ->
                navController.navigate("articleDetail/$uuid")
            },
            onSeeMoreClick = {
                navController.navigate("articleList")
            }
        )
    }

    composable("articleList") {
        ArticleListScreen(
            navController = navController,
            onArticleClick = { uuid ->
                navController.navigate("articleDetail/$uuid")
            },
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        "articleDetail/{uuid}",
        arguments = listOf(navArgument("uuid") { type = NavType.StringType })
    ) { backStackEntry ->
        val uuid = backStackEntry.arguments?.getString("uuid") ?: ""
        ArticleDetailScreen(
            uuid = uuid,
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        "videoDetail/{videoId}",
        arguments = listOf(navArgument("videoId") { type = NavType.StringType })
    ) { backStackEntry ->
        val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
        VideoDetailScreen(
            videoId = videoId,
            onBack = { navController.popBackStack() }
        )
    }

    composable("ibu_hamil") {
        IbuHamilScreen(navController)
    }
    composable("predict") {
        Predict(navController)
    }
    composable("predictv2") {
        PredictOnline(navController)
    }
    composable("pencegahan_stunting") {
        pencegahan_stunting(navController)
    }
    composable("penanganan_stunting") {
        penanganan_stunting(navController)
    }
    composable("info_produk") {
        info_produk(navController)
    }
    composable("add_poster") {
        AddPoster(navController)
    }
    composable("add_resep_menu") {
        AddResepMenu(navController)
    }
    composable("add_resep_mpasi") {
        AddResepMpasi(navController)
    }
    composable("bookmark") {
        BookmarkScreen(navController = navController)
    }
    composable("add_product") {
        AddProduct(navController)
    }
    composable(
        "productDetail/{productId}",
        arguments = listOf(navArgument("productId") { type = NavType.StringType })
    ) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId") ?: ""
        if (productId.isNotBlank()) {
            ProductDetailScreen(productId = productId, navController = navController)
        } else {
            // Handle jika productId kosong atau tidak valid
            // Bisa menampilkan error atau kembali ke halaman sebelumnya
        }
    }
    composable(
        "paymentsMidtrans/{orderId}",
        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        PaymentsMidtrans(orderId = orderId, navController = navController)
    }
    composable(
        "add_activity/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: ""
        if (childId.isNotBlank()) {
            AddActivityScreen(navController = navController, childId = childId)
        } else {
            Log.e("Navigation", "Invalid child ID provided to AddActivityScreen")
        }
    }

    composable("incoming_orders") {
        IncomingOrdersScreen(navController)
    }

    composable(
        "order_detail/{orderId}",
        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
    ) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        OrderDetailScreen(orderId = orderId, navController = navController)
    }

    composable("resepMpasiList") {
        resep_mpasi(
            navController = navController,
            onRecipeClick = { uuid ->
                navController.navigate("resepDetail/$uuid")
            },
            onBack = { navController.popBackStack() }
        )
    }
    composable("resepDetail/{uuid}") { backStackEntry ->
        val uuid = backStackEntry.arguments?.getString("uuid") ?: ""
        ResepDetailScreen(
            uuid = uuid,
            onBack = { navController.popBackStack() }
        )
    }
    composable("bookmarkResepMpasi") {
        BookmarkResepScreen(
            navController = navController
        )
    }

    composable(
        "edit_activity/{childId}/{activityTimestamp}",
        arguments = listOf(
            navArgument("childId") { type = NavType.StringType },
            navArgument("activityTimestamp") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: ""
        val activityTimestamp = backStackEntry.arguments?.getLong("activityTimestamp") ?: 0L
        EditActivityScreen(
            navController = navController,
            childId = childId,
            activityTimestamp = activityTimestamp
        )
    }

    composable(
        "add_menu/{childId}",
        arguments = listOf(navArgument("childId") { type = NavType.StringType })
    ) { backStackEntry ->
        val childId = backStackEntry.arguments?.getString("childId") ?: ""
        AddMenuScreen(childId = childId, navController = navController)
    }
    composable(
        "recipe_detail/{recipeId}",
        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
    ) { backStackEntry ->
        val recipeId = backStackEntry.arguments?.getString("recipeId")
        if (recipeId != null && recipeId.isNotBlank()) {
            RecipeDetailScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() }
            )
        } else {
            Log.e("Navigation", "Invalid or missing recipe ID")
            navController.popBackStack()
        }
    }
}

enum class HomeSections(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.home_feed, Icons.Outlined.Home, "home/feed"),
    PESANAN(R.string.home_pesanan, Icons.Outlined.ShoppingCart, "home/pesanan"),
    PROFILE(R.string.home_profile, Icons.Outlined.AccountCircle, "home/profile")
}

@Composable
fun KidCareBottomBar(
    tabs: Array<HomeSections>,
    currentRoute: String,
    navigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xff00a1c7),
    contentColor: Color = Color.White
) {
    val routes = remember { tabs.map { it.route } }
    val currentSection = tabs.first { it.route == currentRoute }

    KidCareSurface(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        color = color,
        contentColor = contentColor
    ) {
        val springSpec = spring<Float>()
        KidCareBottomNavLayout(
            selectedIndex = currentSection.ordinal,
            itemCount = routes.size,
            indicator = { KidCareBottomNavIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding()
        ) {
            val configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            tabs.forEach { section ->
                val selected = section == currentSection
                val tint by animateColorAsState(
                    if (selected) {
                        Color(0xFFFFFFFF)
                    } else {
                        Color(0xFFB0BEC5)
                    },
                    label = "tint"
                )

                val text = stringResource(section.title).uppercase(currentLocale)

                KidCareBottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = section.icon,
                            tint = tint,
                            contentDescription = text,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = tint,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(section.route) },
                    animSpec = springSpec,
                    modifier = BottomNavigationItemPadding
                        .clip(BottomNavIndicatorShape)
                )
            }
        }
    }
}

@Composable
private fun KidCareBottomNavLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            selectionFraction.animateTo(target, animSpec)
        }
    }

    val indicatorIndex = remember { Animatable(0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }

    Layout(
        modifier = modifier.height(BottomNavHeight),
        content = {
            content()
            Box(Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1))

        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->

                val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
private fun KidCareBottomNavigationItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        if (selected) 1f else 0f, animSpec,
        label = "animation progress"
    )
    KidCareBottomNavItemLayout(
        icon = icon,
        text = text,
        animationProgress = animationProgress,
        modifier = modifier
            .selectable(selected = selected, onClick = onSelected)
            .wrapContentSize()
    )
}

@Composable
private fun KidCareBottomNavItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .layoutId("icon")
                    .padding(horizontal = TextIconSpacing),
                content = icon
            )
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .layoutId("text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    },
                content = text
            )
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
private fun KidCareBottomNavIndicator(
    strokeWidth: Dp = 1.dp,
    color: Color = Color(0xFFFFFFFF),
    shape: Shape = BottomNavIndicatorShape
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(BottomNavigationItemPadding)
            .border(strokeWidth, color, shape)
    )
}

private val TextIconSpacing = 2.dp
private val BottomNavHeight = 56.dp
private val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
private val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
private val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
private fun KidCareBottomNavPreview() {
    KidCareTheme {
        KidCareBottomBar(
            tabs = HomeSections.entries.toTypedArray(),
            currentRoute = "home/feed",
            navigateToRoute = { }
        )
    }
}
