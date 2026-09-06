package com.sanitova.sanitovacheck

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.sanitova.sanitovacheck.ui.theme.*

@Composable
fun LearnScreen(onOpenArticle: (String) -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Articles", "Videos", "Ask AI")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(24.dp)
    ) {
        Text("Learn about WASH", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            "Build your knowledge and ask questions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> ArticlesTab(onOpenArticle = onOpenArticle)
            1 -> VideosTab()
            2 -> ChatTab()
        }
    }
}

@Composable
private fun ArticlesTab(onOpenArticle: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(washArticles) { article ->
            ArticleCard(article = article, onClick = { onOpenArticle(article.id) })
        }
    }
}

@Composable
private fun ArticleCard(article: WashArticle, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    article.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    article.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(article.title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Read full article",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Generate a stable gradient brush for a video card based on its title.
 * This ensures each video always gets the same colors, creating visual variety.
 */
@Composable
private fun videoGradient(title: String): Brush {
    val colors = listOf(
        Pair(Color(0xFF0077B6), Color(0xFF00B4D8)),   // Ocean blue
        Pair(Color(0xFF023E8A), Color(0xFF0096C7)),   // Deep water
        Pair(Color(0xFF48CAE4), Color(0xFF90E0EF)),   // Light aqua
        Pair(Color(0xFF0A9396), Color(0xFF005F73)),   // Teal
        Pair(Color(0xFF028090), Color(0xFF00A896)),   // Sea green
        Pair(Color(0xFF277DA1), Color(0xFF4D908E)),   // Muted blue
        Pair(Color(0xFF1D3557), Color(0xFF457B9D)),   // Navy
        Pair(Color(0xFF006D77), Color(0xFF83C5BE)),   // Sage water
        Pair(Color(0xFF264653), Color(0xFF2A9D8F)),   // Dark teal
        Pair(Color(0xFF1A659E), Color(0xFFFF6B35)),   // Blue to orange (contrast)
    )
    val index = title.hashCode().mod(colors.size).let { if (it < 0) it + colors.size else it }
    val (start, end) = colors[index]
    return Brush.linearGradient(
        colors = listOf(start, end),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

/**
 * Extract YouTube video ID from various URL formats.
 */
private fun extractYouTubeVideoId(url: String): String? {
    val patterns = listOf(
        "[?&]v=([a-zA-Z0-9_-]{11})".toRegex(),
        "embed/([a-zA-Z0-9_-]{11})".toRegex(),
        "youtu\\.be/([a-zA-Z0-9_-]{11})".toRegex(),
        "shorts/([a-zA-Z0-9_-]{11})".toRegex()
    )
    for (pattern in patterns) {
        pattern.find(url)?.groupValues?.get(1)?.let { return it }
    }
    return null
}

@Composable
private fun VideosTab() {
    val context = LocalContext.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(washVideos) { video ->
            val videoId = remember(video.embedUrl) { extractYouTubeVideoId(video.embedUrl) }
            val thumbnailUrl = videoId?.let { "https://img.youtube.com/vi/$it/hqdefault.jpg" }
            val gradient = videoGradient(video.title)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Video preview area: gradient background + optional thumbnail overlay + play button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(gradient)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.watchUrl))
                                context.startActivity(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Try to load thumbnail as an enhancement (if it fails, gradient still shows)
                        if (thumbnailUrl != null) {
                            AsyncImage(
                                model = thumbnailUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gradient overlay so text/play button is always visible
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.1f),
                                                Color.Black.copy(alpha = 0.5f)
                                            )
                                        )
                                    )
                            )
                        }

                        // Play button (always visible)
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.White.copy(alpha = 0.9f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PlayCircle,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Source badge (bottom-left)
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                video.source,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }

                    // Text content below the video area
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            video.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            video.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.watchUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Filled.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Watch on YouTube")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatTab() {
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(
                ChatMessage(
                    text = "Hello! I'm your WASH education assistant. Ask me anything about water safety, sanitation, hygiene, disease prevention, or WASH standards.",
                    isUser = false
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Thinking...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask about WASH...") },
                modifier = Modifier.weight(1f),
                maxLines = 3
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val text = inputText.trim()
                    if (text.isEmpty() || isLoading) return@IconButton

                    messages = messages + ChatMessage(text = text, isUser = true)
                    inputText = ""
                    isLoading = true

                    scope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                WashApiClient.service.chat(ChatRequest(message = text))
                            }
                            val reply = if (response.isSuccessful && response.body() != null) {
                                response.body()!!.reply
                            } else {
                                "Sorry, I couldn't get a response. Please try again."
                            }
                            messages = messages + ChatMessage(text = reply, isUser = false)
                        } catch (e: UnknownHostException) {
                            messages = messages + ChatMessage(
                                text = "No internet connection. Check your network and try again.",
                                isUser = false,
                                isError = true
                            )
                        } catch (e: SocketTimeoutException) {
                            messages = messages + ChatMessage(
                                text = "The request timed out. Please try again.",
                                isUser = false,
                                isError = true
                            )
                        } catch (e: Exception) {
                            messages = messages + ChatMessage(
                                text = "Something went wrong. Please try again.",
                                isUser = false,
                                isError = true
                            )
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = inputText.trim().isNotEmpty() && !isLoading
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val bgColor = when {
        message.isError -> RiskHighBg
        isUser -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        message.isError -> RiskHigh
        isUser -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (!isUser) {
                Icon(
                    Icons.Filled.SmartToy,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                modifier = Modifier.widthIn(max = 260.dp)
            )
            if (isUser) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Filled.Person,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)
