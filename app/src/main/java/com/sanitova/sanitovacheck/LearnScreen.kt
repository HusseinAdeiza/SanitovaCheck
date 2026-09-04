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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.sanitova.sanitovacheck.ui.theme.*

// --- Data models for educational content ---

data class WashArticle(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class WashVideo(
    val title: String,
    val source: String,
    val url: String
)

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isError: Boolean = false
)

// --- Static content (paraphrased from public WHO/UNICEF guidance) ---

val washArticles = listOf(
    WashArticle(
        id = "what_is_wash",
        title = "What is WASH?",
        summary = "Water, Sanitation, and Hygiene (WASH) is a cornerstone of public health. Safe drinking water, adequate sanitation, and proper hygiene prevent disease and save lives.",
        category = "Basics",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "water_safety",
        title = "Water Safety & Treatment",
        summary = "Learn how to assess water quality, common contamination sources, and practical treatment methods including boiling, chlorination, and filtration for field use.",
        category = "Water",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "sanitation",
        title = "Sanitation Systems",
        summary = "Understand latrine types, sewage management, and safe waste disposal. Key for preventing cholera, dysentery, and soil-transmitted helminths.",
        category = "Sanitation",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "handwashing",
        title = "Hand Hygiene",
        summary = "Proper handwashing with soap at critical times can reduce diarrheal disease by up to 50%. Learn the 5 key moments and how to promote behavior change.",
        category = "Hygiene",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "diseases",
        title = "Common WASH-Related Diseases",
        summary = "Cholera, typhoid, dysentery, schistosomiasis, and trachoma are all linked to poor WASH. Learn symptoms, transmission routes, and prevention strategies.",
        category = "Health",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "emergency",
        title = "Emergency WASH Response",
        summary = "In disasters and humanitarian crises, WASH is critical. Learn about the Sphere Handbook minimum standards, rapid assessments, and priority interventions.",
        category = "Emergency",
        icon = Icons.Filled.Book
    ),
    WashArticle(
        id = "clts",
        title = "Community-Led Total Sanitation",
        summary = "CLTS is an approach that empowers communities to eliminate open defecation through local action, not external subsidies. Learn triggering techniques and follow-up.",
        category = "Behavior Change",
        icon = Icons.Filled.Book
    )
)

val washVideos = listOf(
    WashVideo(
        title = "WHO: Water, Sanitation and Hygiene",
        source = "World Health Organization",
        url = "https://www.youtube.com/@WHO"
    ),
    WashVideo(
        title = "UNICEF WASH Programme",
        source = "UNICEF",
        url = "https://www.youtube.com/@unicef"
    ),
    WashVideo(
        title = "Global Handwashing Day",
        source = "Global Handwashing Partnership",
        url = "https://globalhandwashing.org/resources/"
    )
)

// --- Screen ---

@Composable
fun LearnScreen() {
    val context = LocalContext.current
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
            0 -> ArticlesTab()
            1 -> VideosTab { url ->
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            2 -> ChatTab()
        }
    }
}

@Composable
private fun ArticlesTab() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(washArticles) { article ->
            ArticleCard(article = article)
        }
    }
}

@Composable
private fun ArticleCard(article: WashArticle) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
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
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )
            if (article.summary.length > 100) {
                Spacer(Modifier.height(4.dp))
                Text(
                    if (expanded) "Show less" else "Read more",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}

@Composable
private fun VideosTab(onOpenUrl: (String) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(washVideos) { video ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenUrl(video.url) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(video.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            video.source,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text(
                "Videos open in your browser. All linked sources are official WHO/UNICEF public channels.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChatTab() {
    val scope = rememberCoroutineScope()
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Welcome message
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
                Icon(Icons.Filled.Send, contentDescription = "Send")
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
