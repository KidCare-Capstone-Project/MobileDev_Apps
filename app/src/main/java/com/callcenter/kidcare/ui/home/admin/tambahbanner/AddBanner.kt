package com.callcenter.kidcare.ui.home.admin.tambahbanner

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.theme.DarkBlue
import com.callcenter.kidcare.ui.theme.DarkText
import com.callcenter.kidcare.ui.theme.KidCareTheme
import com.callcenter.kidcare.ui.theme.LightBlue
import com.callcenter.kidcare.ui.theme.MinimalBackgroundDark
import com.callcenter.kidcare.ui.theme.MinimalBackgroundLight
import com.callcenter.kidcare.ui.theme.MinimalTextDark
import com.callcenter.kidcare.ui.theme.MinimalTextLight
import com.callcenter.kidcare.ui.theme.Ocean4
import com.callcenter.kidcare.ui.theme.Ocean7
import com.callcenter.kidcare.ui.theme.Ocean9
import com.callcenter.kidcare.ui.theme.WhiteColor
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBanner(navController: NavController) {

    val isLight = !KidCareTheme.colors.isDark

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(KidCareTheme.colors.uiBackground),
        color = KidCareTheme.colors.uiBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Tambah Banner") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isLight) DarkBlue else LightBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isLight) MinimalBackgroundLight else MinimalBackgroundDark,
                    titleContentColor = if (isLight) MinimalTextLight else MinimalTextDark,
                    navigationIconContentColor = if (isLight) DarkText else WhiteColor,
                    actionIconContentColor = if (isLight) DarkText else WhiteColor
                )
            )

            var selectedTabIndex by remember { mutableStateOf(0) }
            val tabs = listOf("Ibu Hamil", "Pencegahan Stunting", "Penanganan Stunting")

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isLight) MinimalBackgroundLight else MinimalBackgroundDark),
                contentColor = if (isLight) MinimalTextLight else MinimalTextDark,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = if (isLight) DarkBlue else LightBlue
                    )
                },
                containerColor = if (isLight) MinimalBackgroundLight else MinimalBackgroundDark,
                divider = {
                    Divider(
                        color = if (isLight) DarkBlue else LightBlue,
                        thickness = 1.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> IbuHamil()
                1 -> PencegahanStunting(isLight)
                2 -> PenangananStunting(isLight)
            }
        }
    }
}

@Composable
fun IbuHamil() {
    val isLight = !KidCareTheme.colors.isDark
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadStatus by remember { mutableStateOf<String?>(null) }
    var uploadProgress by remember { mutableStateOf<Float?>(null) }

    val storage = FirebaseStorage.getInstance()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLight) Ocean7 else Ocean4,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Pilih Gambar",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Pilih Gambar")
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = "Preview Gambar",
                    modifier = Modifier
                        .padding(8.dp)
                        .width(566.dp)
                        .height(478.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )

                Text("Gambar dipilih: ${uri.lastPathSegment}", modifier = Modifier.padding(8.dp))

                Button(
                    onClick = {
                        val storageRef = storage.reference.child("banner/ibu_hamil/${UUID.randomUUID()}")
                        val uploadTask = storageRef.putFile(uri)

                        uploadTask.addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                uploadStatus = "Upload berhasil: $downloadUri"
                            }
                        }.addOnFailureListener { e ->
                            uploadStatus = "Upload gagal: ${e.message}"
                        }

                        uploadTask.addOnProgressListener { taskSnapshot ->
                            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                            uploadProgress = progress
                        }
                    },
                    modifier = Modifier.padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLight) Ocean7 else Ocean4,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Unggah Gambar",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Unggah Gambar")
                }

                // Progress indicator
                uploadProgress?.let { progress ->
                    LinearProgressIndicator(
                        progress = progress / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = if (isLight) Color.Green else Ocean9,
                        trackColor = if (isLight) Color.LightGray else Color.DarkGray
                    )
                    Text("${progress.toInt()}%", modifier = Modifier.padding(8.dp))
                }
            }

            uploadStatus?.let { status ->
                KidCareCard(
                    modifier = Modifier.padding(8.dp),
                    contentColor = Color.Black
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(16.dp),
                        color = if (status.startsWith("Upload berhasil")) Color.Green else Color.Red,  // Text color
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PencegahanStunting(isLight: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

        }
    }
}

@Composable
fun PenangananStunting(isLight: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {

        }
    }
}

fun pickImageFromGallery(context: Context, onImagePicked: (Uri) -> Unit) {
    val intent = Intent(Intent.ACTION_PICK).apply {
        type = "image/*"
    }
    val activity = context as Activity
    activity.startActivityForResult(intent, 1001)
}

fun uploadImageToFirebase(uri: Uri, storageReference: StorageReference, context: Context) {
    val fileName = "ibu_hamil/${System.currentTimeMillis()}.jpg"
    val fileReference = storageReference.child(fileName)

    fileReference.putFile(uri)
        .addOnSuccessListener {
            Toast.makeText(context, "Gambar berhasil diunggah!", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Gagal mengunggah gambar!", Toast.LENGTH_SHORT).show()
        }
}
