@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.callcenter.kidcare.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.callcenter.kidcare.R
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.components.KidCareSurface
import com.callcenter.kidcare.ui.home.welcome.WelcomeDialog
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun Feed(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var showDialog by remember { mutableStateOf(sharedPreferences.getBoolean("show_welcome_dialog", true)) }

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userName = firebaseUser?.displayName ?: "User"

    if (showDialog) {
        WelcomeDialog(userName = userName, onDismiss = {
            showDialog = false
            sharedPreferences.edit().putBoolean("show_welcome_dialog", false).apply()
        })
    }

    FeedContent(modifier)
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
private fun FeedContent(
    modifier: Modifier = Modifier
) {
    KidCareSurface(modifier = modifier.fillMaxSize()) {
        SharedTransitionLayout {
            Column {
                Spacer(modifier = Modifier.height(14.dp))

                ImageCarousel()

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ButtonGrid()
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImageCarousel() {
    val images = listOf(
        R.drawable.carousel_1_welcome_new_1,
        R.drawable.carousel_2_welcome_new_1,
        R.drawable.carousel_3_welcome
    )

    val pagerState = remember { PagerState() }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % images.size)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
    ) {
        val aspectRatio = 16f / 9f

        Column {
            HorizontalPager(
                count = images.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio),
                state = pagerState
            ) { page ->
                val pageOffset = pagerState.currentPageOffset(page)

                KidCareCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 18.dp)
                        .graphicsLayer(
                            scaleX = 1f - (0.1f * abs(pageOffset)),
                            scaleY = 1f - (0.1f * abs(pageOffset)),
                            translationX = pageOffset * 100f
                        )
                ) {
                    Image(
                        painter = painterResource(id = images[page]),
                        contentDescription = "Carousel Image $page",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            PagerIndicator(currentPage = pagerState.currentPage, totalPages = images.size)
        }
    }
}

private fun PagerState.currentPageOffset(page: Int): Float {
    return (currentPage - page) + currentPageOffset
}

@Composable
fun PagerIndicator(
    currentPage: Int,
    totalPages: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 0 until totalPages) {
                val isSelected = i == currentPage
                val indicatorWidth by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 12.dp,
                    animationSpec = tween(durationMillis = 300)
                )

                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(indicatorWidth)
                        .background(
                            color = if (isSelected) Color(0xFF4285F4) else Color.Gray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}
@Composable
private fun ButtonGrid() {

    val buttons: List<Pair<String, ImageVector>> = listOf(
        Pair("dummy 1", Icons.Filled.LocalHospital),
        Pair("dummy 2", Icons.Filled.Chat),
        Pair("dummy 3", Icons.Filled.CardGiftcard),
        Pair("dummy 4", Icons.Filled.Info)
    )

    fun handleButtonClick(buttonText: String) {
        when (buttonText) {
            "Booking Vaksin" -> {
                println("dummy 1 clicked")
            }
            "Chat Dokter" -> {
                println("dummy 2 clicked")
            }
            "Voucher Layanan" -> {
                println("dummy 3 clicked")
            }
            "Info Produk" -> {
                println("dummy 4 clicked")
            }
        }
    }

    val iconColor = KidCareTheme.colors.iconPrimary

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(buttons.size) { index ->
            val (text, icon) = buttons[index]
            KidCareCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { handleButtonClick(text) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview("default")
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun HomePreview() {
    KidCareTheme {
        Feed()
    }
}
