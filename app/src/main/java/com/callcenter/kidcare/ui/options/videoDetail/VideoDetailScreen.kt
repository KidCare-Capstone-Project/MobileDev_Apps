package com.callcenter.kidcare.ui.options.videoDetail

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import coil.compose.rememberImagePainter
import com.callcenter.kidcare.R
import com.callcenter.kidcare.data.*
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.components.KidCareDivider
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun VideoDetailScreen(videoItem: VideoItem?, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }

    var isFullscreen by remember { mutableStateOf(false) }

    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var channelAvatarUrl by remember { mutableStateOf<String?>(null) }
    var isLoadingComments by remember { mutableStateOf(true) }
    var isLoadingChannel by remember { mutableStateOf(true) }
    var errorComments by remember { mutableStateOf<String?>(null) }
    var errorChannel by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(videoItem) {
        videoItem?.let { video ->
            fetchComments(video.id.videoId, context.getString(R.string.youtube_api_key)) { fetchedComments: List<Comment>, error: String? ->
                if (error != null) {
                    errorComments = error
                } else {
                    comments = fetchedComments
                }
                isLoadingComments = false
            }

            fetchChannelDetails(video.snippet.channelId, context.getString(R.string.youtube_api_key)) { avatarUrl, error ->
                if (avatarUrl != null) {
                    channelAvatarUrl = avatarUrl
                } else {
                    errorChannel = error
                }
                isLoadingChannel = false
            }
        }
    }

    LaunchedEffect(isFullscreen) {
        activity?.let { act ->
            if (isFullscreen) {
                WindowCompat.setDecorFitsSystemWindows(act.window, false)
                act.window.insetsController?.let { controller ->
                    controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior =
                        android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                WindowCompat.setDecorFitsSystemWindows(act.window, true)
                act.window.insetsController?.show(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
            }
        }
    }

    // BackHandler untuk saat tidak dalam mode fullscreen
    BackHandler(enabled = !isFullscreen) {
        onBack()
    }

    // BackHandler tambahan untuk saat dalam mode fullscreen
    BackHandler(enabled = isFullscreen) {
        isFullscreen = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        videoItem?.let { video ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                    ) {
                        val youTubePlayerView = remember { YouTubePlayerView(context) }

                        DisposableEffect(youTubePlayerView) {
                            onDispose {
                                youTubePlayerView.release()
                            }
                        }

                        AndroidView(factory = {
                            youTubePlayerView
                        }) { playerView ->
                            playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(video.id.videoId, 0f)
                                }
                            })
                        }

                        IconButton(
                            onClick = { isFullscreen = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White
                            )
                        }
                    }

                    KidCareCard(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                if (isLoadingChannel) {
                                    // Placeholder saat memuat
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "User Avatar",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                            .padding(4.dp),
                                        tint = Color.Gray
                                    )
                                } else if (errorChannel != null) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "User Avatar",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray.copy(alpha = 0.2f))
                                            .padding(4.dp),
                                        tint = Color.Gray
                                    )
                                } else {

                                    Image(
                                        painter = rememberImagePainter(
                                            data = channelAvatarUrl,
                                            builder = {
                                                crossfade(true)
                                                placeholder(R.drawable.ic_account_circle)
                                                error(R.drawable.ic_account_circle)
                                            }
                                        ),
                                        contentDescription = "User Avatar",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = video.snippet.channelTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Text(
                                text = video.snippet.title,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = video.snippet.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .fillMaxWidth(),
                                maxLines = 99999999,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ThumbUp,
                                        contentDescription = "Likes",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${video.statistics?.likeCount ?: 0} Likes")
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Views",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${video.statistics?.viewCount ?: 0} Views")
                                }
                            }
                        }
                    }

                    Text(
                        text = "Komentar",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    if (isLoadingComments) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    } else if (errorComments != null) {
                        Text(
                            text = errorComments ?: "Error fetching comments",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            comments.take(10).forEach { comment ->
                                CommentItem(
                                    userName = comment.snippet.authorDisplayName,
                                    comment = comment.snippet.textDisplay,
                                    profileImageUrl = comment.snippet.authorProfileImageUrl
                                )
                                KidCareDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }

//                    Text(
//                        text = "Video Terkait",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
//                        modifier = Modifier.padding(vertical = 8.dp)
//                    )
//                    if (isLoadingRelated) {
//                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                    } else if (errorRelated != null) {
//                        Text(
//                            text = errorRelated ?: "Error fetching related videos",
//                            color = Color.Red,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    } else {
//                        LazyRow(
//                            horizontalArrangement = Arrangement.spacedBy(8.dp),
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            items(relatedVideos.take(10)) { relatedVideo -> // Limit to 10 videos
//                                RelatedVideoItem(
//                                    video = relatedVideo,
//                                    onVideoClick = { /* Handle klik video terkait */ }
//                                )
//                            }
//                        }
//                    }
                }
            }

            AnimatedVisibility(
                visible = isFullscreen,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.zIndex(2f)
            ) {
                FullscreenPlayer(
                    videoId = video.id.videoId,
                    onCloseFullscreen = { isFullscreen = false }
                )
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

private fun fetchComments(
    videoId: String,
    apiKey: String,
    onResult: (List<Comment>, String?) -> Unit
) {
    ApiClient.api.getComments(
        videoId = videoId,
        apiKey = apiKey,
        maxResults = 10
    ).enqueue(object : Callback<CommentResponse> {
        override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
            if (response.isSuccessful) {
                val commentItems = response.body()?.items?.map { it.snippet.topLevelComment } ?: emptyList()
                onResult(commentItems, null)
            } else {
                onResult(emptyList(), "API Error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
            onResult(emptyList(), "Failed to fetch comments: ${t.message}")
        }
    })
}

private fun fetchChannelDetails(
    channelId: String,
    apiKey: String,
    onResult: (String?, String?) -> Unit
) {
    ApiClient.api.getChannelDetails(
        channelId = channelId,
        apiKey = apiKey
    ).enqueue(object : Callback<ChannelResponse> {
        override fun onResponse(call: Call<ChannelResponse>, response: Response<ChannelResponse>) {
            if (response.isSuccessful) {
                val channelItems = response.body()?.items
                if (!channelItems.isNullOrEmpty()) {
                    val avatarUrl = channelItems[0].snippet.thumbnails.high.url
                    onResult(avatarUrl, null)
                } else {
                    onResult(null, "Channel tidak ditemukan")
                }
            } else {
                onResult(null, "API Error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<ChannelResponse>, t: Throwable) {
            onResult(null, "Gagal mengambil detail channel: ${t.message}")
        }
    })
}

@Composable
fun CommentItem(userName: String, comment: String, profileImageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Image(
            painter = rememberImagePainter(
                data = profileImageUrl,
                builder = {
                    crossfade(true)
                    placeholder(R.drawable.ic_account_circle)
                    error(R.drawable.ic_account_circle)
                }
            ),
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 9999,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun FullscreenPlayer(
    videoId: String,
    onCloseFullscreen: () -> Unit
) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .zIndex(0f)
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        AndroidView(factory = {
            YouTubePlayerView(context).apply {
                // Optional: Kustomisasi player view jika diperlukan
            }
        }, modifier = Modifier
            .width(screenWidth)
            .height(screenHeight)
        ) { playerView ->
            playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoId, 0f)
                    youTubePlayer.play()
                }
            })
        }

        IconButton(
            onClick = onCloseFullscreen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Fullscreen",
                tint = Color.White
            )
        }
    }
}
