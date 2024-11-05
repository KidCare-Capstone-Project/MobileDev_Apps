package com.callcenter.kidcare.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

class TermsAndConditionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TermsAndConditionsScreen(onBackPressed = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(onBackPressed: () -> Unit) {
    var agreed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "KidCare",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (agreed) {
                                onBackPressed()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Silakan setujui syarat dan ketentuan terlebih dahulu.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .animateContentSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Syarat dan Ketentuan KidCare",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "Selamat datang di KidCare, aplikasi inovatif yang dirancang untuk membantu mencegah stunting pada anak melalui teknologi machine learning dan cloud computing.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 26.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(bottom = 24.dp),
                        textAlign = TextAlign.Justify
                    )

                    Divider(
                        color = MaterialTheme.colorScheme.primary,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Text(
                        text = "Dengan menggunakan aplikasi KidCare, Anda menyetujui syarat dan ketentuan berikut:",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TermsAndConditionsText()

                    Spacer(modifier = Modifier.height(32.dp))

                    AcceptButton(onAgree = { agreed = true })
                }
            }
        }
    )
}

@Composable
fun TermsAndConditionsText() {
    val terms = listOf(
        "Dengan menggunakan aplikasi KidCare, Anda setuju untuk mematuhi semua aturan yang berlaku.",
        "Informasi yang Anda berikan harus akurat dan selalu diperbarui sesuai dengan perkembangan.",
        "Anda bertanggung jawab atas aktivitas yang dilakukan dalam aplikasi ini.",
        "Penggunaan aplikasi ini tunduk pada kebijakan privasi yang berlaku.",
        "Semua konten dan data dalam aplikasi ini adalah hak cipta dari pengembang KidCare.",
        "Kami berhak untuk mengubah atau menghentikan layanan kapan saja tanpa pemberitahuan.",
        "Pelanggaran terhadap syarat dan ketentuan dapat mengakibatkan pembatasan akses."
    )

    terms.forEachIndexed { index, term ->
        Text(
            text = "${index + 1}. $term",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun AcceptButton(onAgree: () -> Unit) {
    var agreed by remember { mutableStateOf(false) }

    Button(
        onClick = {
            agreed = true
            onAgree()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = if (agreed) "Terima Kasih!" else "Saya Setuju",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
    }

    if (agreed) {
        Text(
            text = "Terima kasih telah menerima syarat dan ketentuan. Anda siap untuk menggunakan KidCare!",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TermsAndConditionsPreview() {
    TermsAndConditionsScreen(onBackPressed = {})
}
