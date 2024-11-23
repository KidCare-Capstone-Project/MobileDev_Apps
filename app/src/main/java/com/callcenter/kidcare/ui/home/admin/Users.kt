package com.callcenter.kidcare.ui.home.admin

//noinspection UsingMaterialAndMaterial3Libraries
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.callcenter.kidcare.data.UserFirebase
import com.callcenter.kidcare.ui.components.KidCareCard
import com.callcenter.kidcare.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Users(navController: NavController) {
    var userList by remember { mutableStateOf<List<UserFirebase>>(emptyList()) }
    var openDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<UserFirebase?>(null) }

    LaunchedEffect(Unit) {
        fetchUsers { users ->
            userList = users
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(KidCareTheme.colors.uiBackground),
        color = KidCareTheme.colors.uiBackground
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "User List",
                style = MaterialTheme.typography.h5,
                color = KidCareTheme.colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(userList) { user ->
                    UserItem(user, navController) {
                        userToDelete = user
                        openDialog = true
                    }
                }
            }

            if (openDialog && userToDelete != null) {
                showDeleteConfirmationDialog(userToDelete!!, { openDialog = false }, navController)
            }
        }
    }
}

@Composable
fun UserItem(user: UserFirebase, navController: NavController, onDeleteClick: () -> Unit) {
    KidCareCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val userUuid = user.uuid
                if (userUuid.isNotEmpty()) {
                    navController.navigate("admin/editUser/$userUuid")
                }
            },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user.profilePic != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profilePic)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Profile Picture",
                    modifier = Modifier
                        .size(58.dp)
                        .padding(end = 16.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default User Icon",
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 16.dp),
                    tint = KidCareTheme.colors.textSecondary
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.h6,
                    color = KidCareTheme.colors.textPrimary
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.body1,
                    color = KidCareTheme.colors.textPrimary
                )
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.body2,
                    color = KidCareTheme.colors.textPrimary
                )
            }

            // Add Delete Button
            IconButton(
                onClick = {
                    onDeleteClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete User",
                    tint = Color.Red
                )
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun showDeleteConfirmationDialog(
    user: UserFirebase,
    onDismiss: () -> Unit,
    navController: NavController
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Confirm Deletion")
        },
        text = {
            Text("Are you sure you want to delete this user?")
        },
        confirmButton = {
            Button(
                onClick = {
                    deleteUserFromAuth(user.uuid, navController.context)
                    onDismiss()
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

fun deleteUserFromAuth(uid: String, context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()

    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("users").document(uid).delete()
        .addOnSuccessListener {
            Toast.makeText(context, "User deleted from Firestore", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to delete user from Firestore", Toast.LENGTH_SHORT).show()
        }

    val user = auth.currentUser
    if (user != null && user.uid == uid) {
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "User deleted from Firebase Auth", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to delete user from Firebase Auth", Toast.LENGTH_SHORT).show()
                }
            }
    } else {
        Toast.makeText(context, "Cannot delete another user via client", Toast.LENGTH_SHORT).show()
    }
}
