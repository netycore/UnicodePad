package com.netycore.salaamstudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF182B35)
private val Teal = Color(0xFF087E78)
private val Canvas = Color(0xFFF5F8F7)
private val Muted = Color(0xFF75858A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SalaamStudioApp() }
    }
}

@Composable
private fun SalaamStudioApp() {
    var text by remember { mutableStateOf("Your Name") }
    var selected by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current
    val styles = remember(text) {
        listOf(
            "𝑺𝒕𝒚𝒍𝒊𝒔𝒉" to "𝑺𝒕𝒚𝒍𝒊𝒔𝒉",
            "𝐁𝐨𝐥𝐝" to text.map { if (it in 'A'..'Z') (0x1D400 + it.code - 65).toChar() else it }.joinToString(""),
            "𝘐𝘵𝘢𝘭𝘪𝘤" to "𝘐𝘵𝘢𝘭𝘪𝘤  $text",
            "Double" to "𝕕𝕠𝕦𝕓𝕝𝕖  $text",
            "Script" to "𝓢𝓬𝓻𝓲𝓹𝓽  $text",
            "Mono" to "𝚖𝚘𝚗𝚘  $text",
            "Circle" to "ⓒⓘⓡⓒⓛⓔ  $text",
            "Full width" to text.map { if (it in '!'..'~') (it.code + 0xFEE0).toChar() else it }.joinToString("")
        )
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Teal, background = Canvas, surface = Color.White)) {
        Scaffold(containerColor = Canvas, bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Text("Aa", fontWeight = FontWeight.Bold) }, label = { Text("Editor") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Rounded.FavoriteBorder, null) }, label = { Text("Saved") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Rounded.Tune, null) }, label = { Text("Settings") })
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
                Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("SALAAM STUDIO", color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                        Text("Make words beautiful", color = Ink, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE0F1EE)), contentAlignment = Alignment.Center) {
                        Text("S", color = Teal, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("YOUR TEXT", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(12.dp))
                        BasicTextField(value = text, onValueChange = { text = it }, singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Ink, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.fillMaxWidth(), decorationBox = { inner ->
                                if (text.isEmpty()) Text("Type your name here…", color = Muted, fontSize = 20.sp)
                                inner()
                            })
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Type anything and explore styles", color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            TextButton(onClick = { text = "" }) { Text("Clear") }
                        }
                    }
                }
                Spacer(Modifier.height(22.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Explore styles", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.Search, null, tint = Muted)
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Popular", "Fancy", "Bold", "Symbols").forEach { category ->
                        FilterChip(selected = selected == category, onClick = { selected = category }, label = { Text(category) },
                            shape = RoundedCornerShape(50))
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search a style") }, leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    shape = RoundedCornerShape(16.dp), singleLine = true)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
                    items(styles.filter { query.isBlank() || it.first.contains(query, true) || it.second.contains(query, true) }) { item ->
                        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 10.dp, top = 14.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.first, color = Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(5.dp))
                                    Text(item.second, color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Medium)
                                }
                                FilledTonalIconButton(onClick = { clipboard.setText(AnnotatedString(item.second)) }) {
                                    Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy", tint = Teal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
