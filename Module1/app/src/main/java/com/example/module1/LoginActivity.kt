package com.example.module1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginapp.ui.theme.LoginAppTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAppTheme {
                LoginScreen(
                    onLoginClick = { username ->
                        // Save username to the repository
                        UserRepository.username = username
                        // Navigate to WelcomeActivity
                        val intent = Intent(this, WelcomeActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginClick: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF00714B), Color(0xFF00A06B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Welcome!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00714B)
                )

                Text(
                    text = "Sign in to continue",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username TextField
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    label = { Text("Username") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username Icon",
                            tint = Color(0xFF00714B)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00714B),
                        focusedLabelColor = Color(0xFF00714B)
                    )
                )

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = Color(0xFF00714B)
                        )
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00714B),
                        focusedLabelColor = Color(0xFF00714B)
                    )
                )

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Login Button
                Button(
                    onClick = {
                        when {
                            username.isBlank() -> errorMessage = "Please enter your username."
                            password.isBlank() -> errorMessage = "Please enter your password."
                            else -> onLoginClick(username.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00714B))
                ) {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
