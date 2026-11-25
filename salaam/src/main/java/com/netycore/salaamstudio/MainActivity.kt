package com.netycore.george

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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

private val Navy = Color(0xFF152631)
private val Teal = Color(0xFF087F78)
private val Page = Color(0xFFF3F7F7)
private val Subtle = Color(0xFF71838A)
private val White = Color.White

private data class Style(val name: String, val group: String, val sample: String)

private fun convert(input: String, upper: Int, lower: Int): String = buildString {
    var i = 0
    while (i < input.length) {
        val cp = input.codePointAt(i)
        val mapped = when (cp) {
            in 65..90 -> upper + cp - 65
            in 97..122 -> lower + cp - 97
            else -> cp
        }
        append(String(Character.toChars(mapped)))
        i += Character.charCount(cp)
    }
}

private fun circled(input: String): String = buildString {
    var i = 0
    while (i < input.length) {
        val cp = input.codePointAt(i)
        val mapped = when (cp) {
            in 65..90 -> 0x24B6 + cp - 65
            in 97..122 -> 0x24D0 + cp - 97
            in 49..57 -> 0x2460 + cp - 49
            48 -> 0x24EA
            else -> cp
        }
        append(String(Character.toChars(mapped)))
        i += Character.charCount(cp)
    }
}

private fun fullWidth(input: String): String = buildString {
    input.forEach { c -> append(if (c in '!'..'~') (c.code + 0xFEE0).toChar() else c) }
}

private fun makeStyles(value: String): List<Style> = listOf(
    Style("Bold", "Popular", convert(value, 0x1D400, 0x1D41A)),
    Style("Italic", "Popular", convert(value, 0x1D434, 0x1D44E)),
    Style("Bold Italic", "Popular", convert(value, 0x1D468, 0x1D482)),
    Style("Sans Serif", "Popular", convert(value, 0x1D5A0, 0x1D5BA)),
    Style("Sans Bold", "Bold", convert(value, 0x1D5D4, 0x1D5EE)),
    Style("Sans Italic", "Fancy", convert(value, 0x1D608, 0x1D622)),
    Style("Double Struck", "Fancy", convert(value, 0x1D538, 0x1D552)),
    Style("Monospace", "Fancy", convert(value, 0x1D670, 0x1D68A)),
    Style("Script", "Fancy", convert(value, 0x1D49C, 0x1D4B6)),
    Style("Bold Script", "Fancy", convert(value, 0x1D4D0, 0x1D4EA)),
    Style("Fraktur", "Fancy", convert(value, 0x1D504, 0x1D51E)),
    Style("Bold Fraktur", "Fancy", convert(value, 0x1D56C, 0x1D586)),
    Style("Circled", "Symbols", circled(value)),
    Style("Full Width", "Symbols", fullWidth(value)),
    Style("Squared", "Symbols", value.map { if (it in 'A'..'Z') String(Character.toChars(0x1F130 + it.code - 65)) else it.toString() }.joinToString("")),
    Style("Small Caps", "Fancy", value.map { if (it in 'a'..'z') "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ"[it.code - 97].toString() else it.toString() }.joinToString(""))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GeorgeApp() }
    }
}

@Composable
private fun GeorgeApp() {
    var value by remember { mutableStateOf("George") }
    var category by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var page by remember { mutableIntStateOf(0) }
    val favorites = remember { mutableStateListOf<String>() }
    val clipboard = LocalClipboardManager.current
    val styles = remember(value) { makeStyles(value) }
    val categories = listOf("All", "Popular", "Bold", "Fancy", "Symbols")

    MaterialTheme(colorScheme = lightColorScheme(primary = Teal, background = Page, surface = White)) {
        Scaffold(
            containerColor = Page,
            bottomBar = {
                NavigationBar(containerColor = White, tonalElevation = 8.dp) {
                    NavigationBarItem(selected = page == 0, onClick = { page = 0 }, icon = { Icon(Icons.Rounded.TextFields, null) }, label = { Text("Studio") })
                    NavigationBarItem(selected = page == 1, onClick = { page = 1 }, icon = { Icon(Icons.Rounded.GridView, null) }, label = { Text("Characters") })
                    NavigationBarItem(selected = page == 2, onClick = { page = 2 }, icon = { Icon(Icons.Rounded.Favorite, null) }, label = { Text("Saved") })
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("GEORGE  •  UNICODE STUDIO", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                        Text(when (page) { 1 -> "Character map"; 2 -> "Your collection"; else -> "Make text unique" }, color = Navy, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(16.dp)).background(Teal), contentAlignment = Alignment.Center) {
                        Text("G", color = White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    }
                }

                when (page) {
                    0 -> {
                        Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                                Column(Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("TEXT EDITOR", color = Subtle, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp, modifier = Modifier.weight(1f))
                                        Text("${value.length} chars", color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(14.dp))
                                    BasicTextField(
                                        value = value,
                                        onValueChange = { value = it },
                                        textStyle = androidx.compose.ui.text.TextStyle(color = Navy, fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.fillMaxWidth(),
                                        decorationBox = { inner ->
                                            if (value.isEmpty()) Text("Type your text here...", color = Subtle, fontSize = 20.sp)
                                            inner()
                                        }
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        TextButton(onClick = { value = "" }) { Icon(Icons.Rounded.Backspace, null); Spacer(Modifier.width(5.dp)); Text("Clear") }
                                        Spacer(Modifier.weight(1f))
                                        FilledTonalButton(onClick = { clipboard.setText(AnnotatedString(value)) }, shape = RoundedCornerShape(12.dp)) {
                                            Icon(Icons.Rounded.ContentCopy, null, Modifier.size(16.dp)); Spacer(Modifier.width(6.dp)); Text("Copy text")
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Explore styles", color = Navy, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("${styles.size} styles", color = Subtle, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                categories.forEach { item ->
                                    FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) }, shape = RoundedCornerShape(50))
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Find a text style") }, leadingIcon = { Icon(Icons.Rounded.Search, null) },
                                trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { query = "" }) { Icon(Icons.Rounded.Close, null) } },
                                shape = RoundedCornerShape(16.dp), singleLine = true
                            )
                        }
                        val visible = styles.filter {
                            (category == "All" || it.group == category) &&
                                (query.isBlank() || it.name.contains(query, true) || it.sample.contains(query, true))
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(9.dp),
                            contentPadding = PaddingValues(top = 12.dp, bottom = 18.dp)
                        ) {
                            items(visible) { style ->
                                StyleCard(style, favorites.contains(style.name), {
                                    clipboard.setText(AnnotatedString(style.sample))
                                }, {
                                    if (favorites.contains(style.name)) favorites.remove(style.name) else favorites.add(style.name)
                                })
                            }
                        }
                    }
                    1 -> {
                        val chars = (0x1F300..0x1F5FF).map { String(Character.toChars(it)) } +
                            listOf("★", "☆", "♡", "♥", "♛", "♕", "♔", "⚡", "☯", "☮", "✓", "✦", "✧", "❖", "※", "∞", "ツ", "彡", "々", "〆")
                        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                            Text("Tap any symbol to copy it", color = Subtle, fontSize = 13.sp)
                            Spacer(Modifier.height(14.dp))
                            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(6),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 18.dp)
                            ) {
                                items(chars.size) { index ->
                                    Surface(onClick = { clipboard.setText(AnnotatedString(chars[index])) }, shape = RoundedCornerShape(13.dp), color = White) {
                                        Box(Modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
                                            Text(chars[index], fontSize = 23.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        val saved = styles.filter { favorites.contains(it.name) }
                        if (saved.isEmpty()) {
                            Box(Modifier.fillMaxSize().padding(30.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Rounded.FavoriteBorder, null, tint = Teal, modifier = Modifier.size(42.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("No saved styles yet", color = Navy, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("Tap the heart on any style to save it here.", color = Subtle, fontSize = 13.sp)
                                }
                            }
                        } else {
                            LazyColumn(Modifier.fillMaxSize().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                                items(saved) { style -> StyleCard(style, true, { clipboard.setText(AnnotatedString(style.sample)) }, { favorites.remove(style.name) }) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleCard(style: Style, favorite: Boolean, onCopy: () -> Unit, onFavorite: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = White)) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(style.name.uppercase(), color = Subtle, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(Modifier.height(5.dp))
                Text(style.sample.ifEmpty { "Preview appears here" }, color = Navy, fontSize = 19.sp, fontWeight = FontWeight.Medium, maxLines = 2)
            }
            IconButton(onClick = onFavorite) {
                Icon(if (favorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, tint = if (favorite) Color(0xFFE05A71) else Subtle)
            }
            FilledTonalIconButton(onClick = onCopy) {
                Icon(Icons.Rounded.ContentCopy, contentDescription = "Copy style", tint = Teal)
            }
        }
    }
}
