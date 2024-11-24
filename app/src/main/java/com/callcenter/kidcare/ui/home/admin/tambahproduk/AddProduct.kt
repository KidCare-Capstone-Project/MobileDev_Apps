package com.callcenter.kidcare.ui.home.admin.tambahproduk

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.callcenter.kidcare.ui.theme.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProduct(navController: NavController) {
    val isLight = !KidCareTheme.colors.isDark
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Tambah Produk", "Tambah Brand")

    KidCareTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin - Tambah") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            @Suppress("DEPRECATION")
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
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(KidCareTheme.colors.uiBackground)
                        .padding(paddingValues)
                ) {
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
                        0 -> AddProductUtama()
                        1 -> AddBrand()
                    }
                }
            }
        )
    }
}

@Composable
fun AddProductUtama() {
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    // State variables
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var brandsList by remember { mutableStateOf<List<Brand>>(emptyList()) }

    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }

    // List to hold up to 5 optional images
    var optionalImages by remember { mutableStateOf<List<Uri?>>(listOf(null, null, null, null, null)) }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // Launchers for image picking
    val thumbnailPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            thumbnailUri = uri
        }
    )

    val imagePickerLaunchers = List(5) { index ->
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                optionalImages = optionalImages.toMutableList().apply { set(index, uri) }
            }
        )
    }

    val isDarkMode = isSystemInDarkTheme()
    val isLight = !KidCareTheme.colors.isDark

    val customTextSelectionColors = TextSelectionColors(
        handleColor = if (isDarkMode) MinimalPrimary else MinimalPrimary,
        backgroundColor = if (isDarkMode) MinimalPrimary else MinimalPrimary.copy(alpha = 0.4f)
    )

    // Fetch brands from Firestore
    LaunchedEffect(Unit) {
        try {
            val brandsSnapshot = firestore.collection("brands").get().await()
            brandsList = brandsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Brand::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            message = "Gagal mengambil daftar brand."
        }
    }

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
            // Judul Produk
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul Produk", color = if (isLight) MinimalTextLight else MinimalTextDark) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = if (isDarkMode) MinimalBackgroundDark else MinimalBackgroundLight,
                    focusedBorderColor = MinimalPrimary,
                    unfocusedBorderColor = MinimalSecondary,
                    cursorColor = MinimalPrimary,
                    textColor = if (isDarkMode) MinimalTextDark else MinimalTextLight,
                )
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                // Deskripsi Produk
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(
                            "Deskripsi Produk",
                            color = if (isLight) MinimalTextLight else MinimalTextDark
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp),
                    maxLines = 5,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = if (isDarkMode) MinimalBackgroundDark else MinimalBackgroundLight,
                        focusedBorderColor = MinimalPrimary,
                        unfocusedBorderColor = MinimalSecondary,
                        cursorColor = MinimalPrimary,
                        textColor = if (isDarkMode) MinimalTextDark else MinimalTextLight,
                    )
                )
            }

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                // Harga Produk
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Harga", color = if (isLight) MinimalTextLight else MinimalTextDark) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = if (isDarkMode) MinimalBackgroundDark else MinimalBackgroundLight,
                        focusedBorderColor = MinimalPrimary,
                        unfocusedBorderColor = MinimalSecondary,
                        cursorColor = MinimalPrimary,
                        textColor = if (isDarkMode) MinimalTextDark else MinimalTextLight,
                    )
                )
            }

            // Dropdown untuk memilih Brand
            BrandDropdown(
                brands = brandsList,
                selectedBrand = selectedBrand,
                onBrandSelected = { selectedBrand = it }
            )

            if (brandsList.isNotEmpty()) {
                Text(
                    text = "Jumlah brand tersedia: ${brandsList.size}",
                    color = Color.Green,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else if (message == "Gagal mengambil daftar brand.") {
                Text(
                    text = message ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thumbnail Produk
            Text(text = "Thumbnail", color = if (isLight) MinimalTextLight else MinimalTextDark, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            if (thumbnailUri != null) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = rememberImagePainter(thumbnailUri),
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { thumbnailUri = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Thumbnail", tint = Color.Red)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray)
                        .clickable { thumbnailPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Pilih Thumbnail", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gambar Opsional (1-5)
            Text(text = "Gambar Opsional", color = if (isLight) MinimalTextLight else MinimalTextDark, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Column {
                optionalImages.forEachIndexed { index, uri ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        if (uri != null) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray)
                            ) {
                                Image(
                                    painter = rememberImagePainter(uri),
                                    contentDescription = "Gambar Opsional ${index + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = {
                                        optionalImages = optionalImages.toMutableList().apply { set(index, null) }
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus Gambar", tint = Color.Red)
                                }
                            }
                        } else {
                            Button(
                                onClick = { imagePickerLaunchers[index].launch("image/*") },
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkMode) ButtonDarkColor else ButtonLightColor,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Tambah Gambar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Tambah Produk
            Button(
                onClick = {
                    if (validateProductInput(title, description, price, selectedBrand, thumbnailUri)) {
                        coroutineScope.launch {
                            isLoading = true
                            message = null
                            try {
                                // Upload Thumbnail
                                val thumbnailUrl = uploadImage(storage, thumbnailUri!!, "thumbnails")

                                // Upload Gambar Opsional
                                val optionalUrls = mutableListOf<String>()
                                for (uri in optionalImages) {
                                    if (uri != null) {
                                        val url = uploadImage(storage, uri, "products/images")
                                        optionalUrls.add(url)
                                    }
                                }

                                // Simpan Data Produk ke Firestore
                                val productData = mapOf(
                                    "title" to title,
                                    "description" to description,
                                    "price" to price.toDouble(),
                                    "brandId" to selectedBrand,
                                    "thumbnailUrl" to thumbnailUrl,
                                    "imageUrls" to optionalUrls,
                                    "createdAt" to System.currentTimeMillis()
                                )

                                firestore.collection("products").add(productData).await()

                                // Reset Form
                                title = ""
                                description = ""
                                price = ""
                                selectedBrand = null
                                thumbnailUri = null
                                optionalImages = listOf(null, null, null, null, null)
                                message = "Produk berhasil ditambahkan!"
                            } catch (e: Exception) {
                                e.printStackTrace()
                                message = "Terjadi kesalahan: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        message = "Mohon lengkapi semua field yang diperlukan."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) ButtonDarkColor else ButtonLightColor,
                    contentColor = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...", fontSize = 16.sp)
                } else {
                    Text("Tambah Produk", fontSize = 16.sp)
                }
            }

            // Pesan
            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = if (it.contains("berhasil", ignoreCase = true)) Color(0xFF4CAF50) else Color(0xFFF44336),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Data class untuk Brand
data class Brand(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = ""
)

// Fungsi untuk meng-upload gambar ke Firebase Storage
suspend fun uploadImage(storage: FirebaseStorage, uri: Uri, folder: String): String {
    val storageRef = storage.reference.child("$folder/${System.currentTimeMillis()}_${uri.lastPathSegment}")
    storageRef.putFile(uri).await()
    return storageRef.downloadUrl.await().toString()
}

// Validasi input produk
fun validateProductInput(
    title: String,
    description: String,
    price: String,
    selectedBrand: String?,
    thumbnailUri: Uri?
): Boolean {
    if (title.isBlank()) return false
    if (description.isBlank()) return false
    if (price.isBlank()) return false
    if (selectedBrand == null) return false
    if (thumbnailUri == null) return false
    return true
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandDropdown(
    brands: List<Brand>,
    selectedBrand: String?,
    onBrandSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedBrandName = brands.find { it.id == selectedBrand }?.name ?: ""

    val isLight = !KidCareTheme.colors.isDark
    val isDarkMode = isSystemInDarkTheme()

    val textColor = if (isLight) MinimalTextLight else MinimalTextDark
    val iconColor = if (isLight) MinimalTextLight else MinimalTextDark
    val menuBackgroundColor = KidCareTheme.colors.uiBackground
    val menuContentColor = if (isLight) MinimalTextLight else MinimalTextDark

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 0f else 270f,
        animationSpec = tween(durationMillis = 300)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (brands.isNotEmpty()) {
                expanded = !expanded
            }
        }
    ) {
        OutlinedTextField(
            value = selectedBrandName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Brand", color = textColor) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.rotate(rotationAngle)
                )
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MinimalPrimary,
                unfocusedBorderColor = MinimalSecondary,
                cursorColor = MinimalPrimary,
            ),
            textStyle = LocalTextStyle.current.copy(color = textColor),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(menuBackgroundColor)
                .border(
                    width = 1.dp,
                    color = if (isDarkMode) Color.White else Color.Black,
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            if (brands.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Tidak ada brand tersedia.", color = Color.Gray) },
                    onClick = {}
                )
            } else {
                brands.forEach { brand ->
                    DropdownMenuItem(
                        text = { Text(brand.name ?: "Unnamed Brand", color = menuContentColor) },
                        onClick = {
                            onBrandSelected(brand.id)
                            expanded = false
                        },
                        modifier = Modifier
                            .background(menuBackgroundColor)
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AddBrand() {
    val coroutineScope = rememberCoroutineScope()

    var brandName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImageUri = uri
        }
    )

    val isDarkMode = isSystemInDarkTheme()
    val isLight = !KidCareTheme.colors.isDark

    val customTextSelectionColors = TextSelectionColors(
        handleColor = if (isDarkMode) MinimalPrimary else MinimalPrimary,
        backgroundColor = if (isDarkMode) MinimalPrimary else MinimalPrimary.copy(alpha = 0.4f)
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val maxWidth = maxWidth
        val isLargeScreen = maxWidth > 600.dp

        val columnPadding = if (isLargeScreen) 32.dp else 16.dp
        val imageSize = if (isLargeScreen) 300.dp else 200.dp
        val textSize = if (isLargeScreen) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(columnPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tambah Brand",
                style = textSize,
                color = if (isLight) MinimalTextLight else MinimalTextDark,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = {
                        Text(
                            "Nama Brand",
                            color = if (isLight) MinimalTextLight else MinimalTextDark
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = if (isDarkMode) MinimalBackgroundDark else MinimalBackgroundLight,
                        focusedBorderColor = MinimalPrimary,
                        unfocusedBorderColor = MinimalSecondary,
                        cursorColor = MinimalPrimary,
                        textColor = if (isDarkMode) MinimalTextDark else MinimalTextLight,
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedImageUri != null) {
                Image(
                    painter = rememberImagePainter(selectedImageUri),
                    contentDescription = "Gambar Brand",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { selectedImageUri = null }) {
                    Text("Hapus Gambar", color = Color.Gray)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pilih Gambar",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (brandName.isNotBlank() && selectedImageUri != null) {
                        coroutineScope.launch {
                            isLoading = true
                            message = null
                            try {
                                val storageRef = storage.reference.child("brands/${System.currentTimeMillis()}_${selectedImageUri?.lastPathSegment}")
                                val uploadTask = storageRef.putFile(selectedImageUri!!)
                                uploadTask.await()

                                val downloadUrl = storageRef.downloadUrl.await().toString()

                                val brandData = mapOf(
                                    "name" to brandName,
                                    "imageUrl" to downloadUrl,
                                    "createdAt" to System.currentTimeMillis()
                                )
                                firestore.collection("brands")
                                    .add(brandData)
                                    .await()

                                brandName = ""
                                selectedImageUri = null
                                message = "Brand berhasil ditambahkan!"
                            } catch (e: Exception) {
                                e.printStackTrace()
                                message = "Terjadi kesalahan: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        message = "Mohon isi nama brand dan pilih gambar."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) ButtonDarkColor else ButtonLightColor,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...", fontSize = 16.sp)
                } else {
                    Text("Tambah Brand", fontSize = 16.sp)
                }
            }

            message?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = if (it.contains("berhasil", ignoreCase = true)) Color(0xFF4CAF50) else Color(0xFFF44336),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
