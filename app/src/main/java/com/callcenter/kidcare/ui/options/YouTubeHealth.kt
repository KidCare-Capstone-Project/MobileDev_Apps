package com.callcenter.kidcare.ui.options

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.callcenter.kidcare.R
import com.callcenter.kidcare.data.ApiClient
import com.callcenter.kidcare.data.VideoDetailsResponse
import com.callcenter.kidcare.data.VideoItem
import com.callcenter.kidcare.data.VideoResponse
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.components.YouTubeAppBar
import com.callcenter.kidcare.ui.options.videoDetail.VideoDetailScreen
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.callcenter.kidcare.ui.theme.KidCareTheme.colors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun YouTubeHealth(onClose: () -> Unit) {
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val apiKey = context.getString(R.string.youtube_api_key)
    var searchQuery by remember { mutableStateOf("") }
    var showSearchInput by remember { mutableStateOf(false) }
    var selectedVideoId by remember { mutableStateOf<String?>(null) }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let {
                connectivityManager.getNetworkCapabilities(it)
            }
            return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo?.isConnected == true
        }
    }

    val onSearchClick: () -> Unit = {
        if (searchQuery.isNotEmpty()) {
            isLoading = true
            fetchVideos(searchQuery, apiKey) { fetchedVideos, error ->
                if (error != null) {
                    errorMessage = error
                } else {
                    videos = fetchedVideos
                }
                isLoading = false
            }
        }
        showSearchInput = !showSearchInput
    }

    LaunchedEffect(Unit) {
        if (!isInternetAvailable(context)) {
            isLoading = false
            errorMessage = "No Internet Connection"
            return@LaunchedEffect
        }
        fetchVideos("Informasi Stunting Pada Anak DI Indonesia Terbaru 2024", apiKey) { fetchedVideos, error ->
            if (error != null) {
                errorMessage = error
            } else {
                videos = fetchedVideos
            }
            isLoading = false
        }
    }

    BackHandler {
        onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.uiBackground)
    ) {
        if (selectedVideoId == null) {
            YouTubeAppBar(
                onClose = {
                    onClose()
                },
                onSearchClick = onSearchClick
            )
        }

        if (showSearchInput) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search", color = colors.brand) },
                placeholder = {
                    Text("Enter search term", color = colors.brandSecondary.copy(alpha = 0.5f))
                },
                trailingIcon = {
                    IconButton(onClick = { onSearchClick() }) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = colors.brand
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = colors.brand,
                    unfocusedTextColor = colors.brandSecondary,
                    focusedContainerColor = colors.uiBackground,
                    unfocusedContainerColor = colors.uiBorder,
                    focusedIndicatorColor = Color(0xFF0E5E6C),
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        if (selectedVideoId != null) {
            val selectedVideo = videos.find { it.id.videoId == selectedVideoId }
            selectedVideo?.let {
                VideoDetailScreen(
                    videoItem = it,
                    onBack = { selectedVideoId = null }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF0E5E6C))
                    }
                } else if (errorMessage != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = errorMessage ?: "An unknown error occurred",
                            color = Color.Red,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn {
                        items(videos) { video ->
                            VideoItemRow(video) { videoId ->
                                selectedVideoId = videoId
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun fetchVideos(
    query: String,
    apiKey: String,
    onResult: (List<VideoItem>, String?) -> Unit
) {
    ApiClient.api.searchVideos(query = query, apiKey = apiKey).enqueue(object : Callback<VideoResponse> {
        override fun onResponse(call: Call<VideoResponse>, response: Response<VideoResponse>) {
            if (response.isSuccessful) {
                val videoItems = response.body()?.items ?: emptyList()
                onResult(videoItems, null)

                videoItems.forEach { video ->
                    ApiClient.api.getVideoDetails(id = video.id.videoId, apiKey = apiKey).enqueue(object : Callback<VideoDetailsResponse> {
                        override fun onResponse(call: Call<VideoDetailsResponse>, response: Response<VideoDetailsResponse>) {
                            if (response.isSuccessful) {
                                val details = response.body()?.items?.firstOrNull()
                                details?.let {
                                    val updatedVideo = video.copy(statistics = it.statistics, snippet = it.snippet)
                                    onResult(videoItems.map { if (it.id == video.id) updatedVideo else it }, null)
                                }
                            }
                        }

                        override fun onFailure(call: Call<VideoDetailsResponse>, t: Throwable) {
                            Log.e("VideoDetails", "Failed to fetch video details: ${t.message}")
                        }
                    })
                }
            } else {
                onResult(emptyList(), "API Error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
            onResult(emptyList(), "Failed to fetch videos: ${t.message}")
        }
    })
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun VideoItemRow(video: VideoItem, onClick: (String) -> Unit) {
    val colors = colors
    val typography = MaterialTheme.typography

    KidCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable { onClick(video.id.videoId) },
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = video.snippet.thumbnails.high.url,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.empty_state_search)
                            error(R.drawable.empty_state_search)
                        }
                    ),
                    contentDescription = video.snippet.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = video.snippet.title,
                    color = colors.textPrimary,
                    style = typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.snippet.description,
                    color = colors.textSecondary,
                    style = typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = "Likes",
                            tint = colors.brand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${video.statistics?.likeCount ?: "0"}",
                            color = colors.textPrimary,
                            style = typography.bodySmall
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.Outlined.RemoveRedEye,
                            contentDescription = "Views",
                            tint = colors.brand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${video.statistics?.viewCount ?: "0"}",
                            color = colors.textPrimary,
                            style = typography.bodySmall
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Preview(showBackground = true)
@Composable
fun PreviewYouTubeHealth() {
    KidCareTheme {
        YouTubeHealth(onClose = { })
    }
}
