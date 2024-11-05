package com.callcenter.kidcare.ui.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.scale
import com.callcenter.kidcare.ui.funcauth.FunLoginGoogle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Profile(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
    }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    var displayName by remember { mutableStateOf("Unknown") }
    var email by remember { mutableStateOf("No Email") }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    currentUser?.let { user: FirebaseUser ->
        displayName = user.displayName ?: "No Name"
        email = user.email ?: "No Email"
        photoUrl = user.photoUrl?.toString()
    }

    KidCareTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(KidCareTheme.colors.uiBackground) // Use theme color
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0B5C6A), Color(0xFF1D9D90)) // Custom colors for CardView
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberImagePainter(
                            data = photoUrl,
                            builder = {
                                transformations(CircleCropTransformation())
                            }
                        ),
                        contentDescription = "Profile photo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default profile icon",
                        tint = KidCareTheme.colors.textPrimary,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(KidCareTheme.colors.textPrimary.copy(alpha = 0.2f))
                            .padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFFE5E5E5)
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFB0BEC5)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ProfileItem(
                    title = "Tentang Kami",
                    icon = Icons.Default.Info,
                    color = Color(0xFF1976D2)
                ) {
                    Toast.makeText(context, "Tentang Kami", Toast.LENGTH_SHORT).show()
                }

                AnimatedVisibility(visible = true) {
                    ProfileItem(
                        title = "Keluar",
                        icon = Icons.Default.ExitToApp,
                        color = Color(0xFFD32F2F)
                    ) {
                        handleLogout(context)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileItem(
    title: String,
    icon: ImageVector,
    color: Color = KidCareTheme.colors.textInteractive,
    onClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    KidCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    isPressed = true
                    onClick?.invoke()

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(100)
                        isPressed = false
                    }
                }
            )
            .padding(8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .scale(scale),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.05f))
                    .padding(8.dp)
                    .animateContentSize()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = KidCareTheme.colors.textPrimary // Use theme color
            )
        }
    }
}

fun handleLogout(context: Context) {
    FirebaseAuth.getInstance().signOut()

    if (context is ComponentActivity) {
        val intent = Intent(context, FunLoginGoogle::class.java)
        context.startActivity(intent)
        context.finish()
    }
}

@Preview("default")
@Preview("large font", fontScale = 2f)
@Composable
fun ProfilePreview() {
    KidCareTheme {
        Profile()
    }
}

