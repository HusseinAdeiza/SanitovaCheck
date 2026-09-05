package com.sanitova.sanitovacheck

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun VideosTab() {
    val context = LocalContext.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(washVideos) { video ->
            var hasError by remember { mutableStateOf(false) }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.PlayCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(video.title, style = MaterialTheme.typography.titleMedium)
                            Text(
                                video.source,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        video.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    if (!hasError) {
                        // Embedded YouTube player via WebView
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black)
                        ) {
                            AndroidView(
                                factory = { ctx ->
                                    WebView(ctx).apply {
                                        settings.javaScriptEnabled = true
                                        settings.domStorageEnabled = true
                                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                                        settings.mediaPlaybackRequiresUserGesture = false
                                        // Desktop user-agent to prevent mobile app redirects
                                        settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
                                        webChromeClient = WebChromeClient()
                                        webViewClient = object : WebViewClient() {
                                            override fun onReceivedError(
                                                view: WebView?, request: android.webkit.WebResourceRequest?, error: android.webkit.WebResourceError?
                                            ) {
                                                super.onReceivedError(view, request, error)
                                                hasError = true
                                            }

                                            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                                val url = request?.url?.toString() ?: return false
                                                // Block intent:// and market:// redirects
                                                if (url.startsWith("intent://") || url.startsWith("market://") || url.startsWith("vnd.youtube://")) {
                                                    return true
                                                }
                                                return false
                                            }
                                        }
                                        val html = """
                                            <!DOCTYPE html>
                                            <html><head>
                                            <meta name="viewport" content="width=device-width, initial-scale=1">
                                            <style>
                                                body{margin:0;padding:0;overflow:hidden;background:#000;}
                                                .container{position:relative;width:100%;height:100vh;}
                                                iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0;}
                                            </style>
                                            </head><body>
                                            <div class="container">
                                            <iframe src="${video.embedUrl}?rel=0&modestbranding=1&playsinline=1&enablejsapi=1" 
                                            frameborder="0" allowfullscreen 
                                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"></iframe>
                                            </div>
                                            </body></html>
                                        """.trimIndent()
                                        loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "UTF-8", null)
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Fallback: always show Watch on YouTube button
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.watchUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(if (hasError) "Watch on YouTube" else "Open in YouTube app")
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
