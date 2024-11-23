package com.callcenter.kidcare.ui.home.mainfeaturesgrid.predict

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.callcenter.kidcare.data.ChildProfile
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.home.mainfeaturesgrid.predict.viewmodel.PredictViewModel
import com.callcenter.kidcare.ui.navigation.MainDestinations
import com.callcenter.kidcare.ui.theme.ButtonDarkColor
import com.callcenter.kidcare.ui.theme.ButtonLightColor
import com.callcenter.kidcare.ui.theme.DarkText
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.callcenter.kidcare.ui.theme.MinimalAccent
import com.callcenter.kidcare.ui.theme.MinimalTextDark
import com.callcenter.kidcare.ui.theme.MinimalTextLight
import com.callcenter.kidcare.ui.theme.Neutral4
import com.callcenter.kidcare.ui.theme.Ocean4
import com.callcenter.kidcare.ui.theme.Ocean7
import com.callcenter.kidcare.ui.theme.WhiteColor
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun Predict(navController: NavController) {
    val viewModel: PredictViewModel = viewModel()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val context = LocalContext.current
    var children by remember { mutableStateOf<List<ChildProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    // Definisikan isDarkMode
    val isDarkMode = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.loadModel(context)
        viewModel.fetchChildrenData(uid) { fetchedChildren ->
            children = fetchedChildren
            isLoading = false
            if (children.isEmpty()) {
                showDialog = true
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = KidCareTheme.colors.uiBackground
    ) {
        when {
            isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = if (isSystemInDarkTheme()) Ocean4 else Ocean7,
                            strokeWidth = 4.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Memuat data...",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            children.isEmpty() -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Anda belum memiliki profil anak.",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.navigate(MainDestinations.ADD_CHILD_PROFILE_ROUTE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDarkMode) ButtonDarkColor else ButtonLightColor,
                                contentColor = if (isDarkMode) WhiteColor else WhiteColor
                            )
                        ) {
                            Text(text = "Tambah Profil Anak")
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showDialog,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 300)
                    ) + slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 300)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    ) + slideOutVertically(
                        targetOffsetY = { it / 2 },
                        animationSpec = tween(durationMillis = 300)
                    )
                ) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = "Tambah Profil Anak") },
                        text = { Text(text = "Anda belum memiliki profil anak. Silakan tambahkan profil anak terlebih dahulu.") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDialog = false
                                    navController.navigate(MainDestinations.ADD_CHILD_PROFILE_ROUTE)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF00796B)
                                )
                            ) {
                                Text("Tambah")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFFD32F2F)
                                )
                            ) {
                                Text("Nanti")
                            }
                        },
                        containerColor = if (isDarkMode) Neutral4.copy(alpha = 0.85f) else MinimalAccent.copy(alpha = 0.85f),
                        titleContentColor = if (isDarkMode) WhiteColor else DarkText,
                        textContentColor = if (isDarkMode) WhiteColor else DarkText
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = rememberLazyListState()
                ) {
                    items(
                        items = children,
                        key = { child ->
                            if (child.id.isNotEmpty()) {
                                child.id
                            } else {
                                "${child.name}-${child.getAgeInMonths()}-${children.indexOf(child)}"
                            }
                        }
                    ) { child ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { 40 }) + fadeOut(),
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(durationMillis = 300)
                            )
                        ) {
                            val predictions = viewModel.predict(child)
                            ChildCard(child = child, predictions = predictions)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildCard(child: ChildProfile, predictions: FloatArray?) {

    val isLight = !KidCareTheme.colors.isDark

    KidCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = child.name,
                style = MaterialTheme.typography.titleLarge,
                color = if (isLight) MinimalTextLight else MinimalTextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usia: ${child.getAgeInMonths()} bulan",
                style = MaterialTheme.typography.bodyMedium
            )
            if (predictions != null) {
                if (predictions.size == 2) {
                    val stuntedPercentage = predictions[1] * 100
                    val notStuntedPercentage = predictions[0] * 100
                    Text(
                        text = "Prediksi Stunting: ${String.format("%.2f", stuntedPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                    Text(
                        text = "Prediksi Tidak Stunting: ${String.format("%.2f", notStuntedPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Green
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PredictionChart(predictions = predictions)
                } else if (predictions.size == 1) {
                    val stuntedProbability = predictions[0]
                    val stuntedPercentage = stuntedProbability * 100
                    val notStuntedPercentage = (1 - stuntedProbability) * 100
                    Text(
                        text = "Prediksi Stunting: ${String.format("%.2f", stuntedPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                    Text(
                        text = "Prediksi Tidak Stunting: ${String.format("%.2f", notStuntedPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Green
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PredictionChart(predictions = floatArrayOf(notStuntedPercentage, stuntedPercentage))
                } else {
                    Text(
                        text = "Prediksi tidak tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                Text(
                    text = "Prediksi tidak tersedia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
