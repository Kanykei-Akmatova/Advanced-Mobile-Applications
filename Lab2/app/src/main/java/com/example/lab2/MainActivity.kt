package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab2.ui.theme.ChatAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatAppTheme {
                val navController = rememberNavController()
                val viewModel: ChatViewModel = viewModel()

                NavHost(navController = navController, startDestination = "chat") {
                    composable("chat") {
                        ChatScreen(
                            viewModel = viewModel,
                            onMessageClick = { messageId ->
                                navController.navigate("detail/$messageId")
                            }
                        )
                    }
                    composable("detail/{messageId}") { backStackEntry ->
                        val messageId = backStackEntry.arguments?.getString("messageId")?.toIntOrNull()
                        val messages by viewModel.messages.collectAsState()
                        val message = messages.find { it.id == messageId }
                        if (message != null) {
                            DetailScreen(message = message, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

// ─── Chat Screen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onMessageClick: (Int) -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat App", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 12.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Message input field
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedContainerColor = Color(0xFFE8F0FE)
                        ),
                        maxLines = 4,
                        singleLine = false
                    )

                    // Send & Receive buttons side by side
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.sendMessage(inputText, MessageType.SEND)
                                inputText = ""
                            },
                            enabled = inputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2),
                                disabledContainerColor = Color(0xFFB0BEC5)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Send", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.sendMessage(inputText, MessageType.RECEIVE)
                                inputText = ""
                            },
                            enabled = inputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF388E3C),
                                disabledContainerColor = Color(0xFFB0BEC5)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text("Receive", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No messages yet.\nType something and press Send or Receive.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(32.dp)
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        onClick = { onMessageClick(message.id) }
                    )
                }
            }
        }
    }
}

// ─── Message Bubble ────────────────────────────────────────────────────────────

@Composable
fun MessageBubble(message: Message, onClick: () -> Unit) {
    val isSend = message.type == MessageType.SEND

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isSend) Arrangement.Start else Arrangement.End
    ) {
        Column(
            horizontalAlignment = if (isSend) Alignment.Start else Alignment.End,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = if (isSend) "Sent" else "Received",
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = if (isSend) 4.dp else 16.dp,
                            topEnd = if (isSend) 16.dp else 4.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .background(
                        if (isSend) Color(0xFFBBDEFB)   // light blue for sent
                        else Color(0xFFC8E6C9)            // light green for received
                    )
                    .clickable { onClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color = Color(0xFF212121)
                )
            }
        }
    }
}

// ─── Detail Screen ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(message: Message, navController: NavHostController) {
    val isSend = message.type == MessageType.SEND
    val typeLabel = if (isSend) "Sent" else "Received"
    val accentColor = if (isSend) Color(0xFF1976D2) else Color(0xFF388E3C)
    val bgColor = if (isSend) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Message Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = accentColor,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Message card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Message",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message.text,
                        fontSize = 18.sp,
                        color = Color(0xFF212121),
                        lineHeight = 26.sp
                    )
                }
            }

            // Info card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5F5F5),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(label = "Direction", value = typeLabel)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(label = "Message ID", value = "#${message.id}")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}