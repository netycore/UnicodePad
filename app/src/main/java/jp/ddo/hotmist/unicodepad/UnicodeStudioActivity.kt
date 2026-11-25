package jp.ddo.hotmist.unicodepad

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class StudioBlock(val start: Int, val title: String)
private data class StudioCharacter(val codePoint: Int) {
    val glyph: String get() = String(Character.toChars(codePoint))
    val code: String get() = "U+%04X".format(codePoint)
    val name: String get() = if (Build.VERSION.SDK_INT >= 24) {
        android.icu.lang.UCharacter.getName(codePoint) ?: "Unassigned character"
    } else {
        Character.UnicodeBlock.of(codePoint)?.toString()?.replace('_', ' ') ?: "Unicode character"
    }
}

class UnicodeStudioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UnicodeStudioApp() }
    }
}

@Composable
private fun UnicodeStudioApp() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("unicode_studio", Context.MODE_PRIVATE) }
    val blocks = remember { loadStudioBlocks(context) }
    var selectedBlock by remember { mutableStateOf(blocks.firstOrNull() ?: StudioBlock(0, "Basic Latin")) }
    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableIntStateOf(0) }
    var blockMenu by remember { mutableStateOf(false) }
    var selectedCharacter by remember { mutableStateOf<StudioCharacter?>(null) }
    var pageOffset by remember { mutableIntStateOf(0) }
    val favorites = remember {
        mutableStateListOf<String>().apply {
            addAll(preferences.getStringSet("favorites", emptySet()) ?: emptySet())
        }
    }
    val recents = remember {
        mutableStateListOf<String>().apply {
            addAll(preferences.getString("recent", "").orEmpty().split(",").filter { it.isNotBlank() })
        }
    }
    val characters = remember(selectedBlock, query, activeTab, pageOffset, favorites.size, recents.size) {
        when (activeTab) {
            1 -> favorites.mapNotNull { it.toIntOrNull() }.map { StudioCharacter(it) }
            2 -> recents.mapNotNull { it.toIntOrNull() }.map { StudioCharacter(it) }
            else -> buildStudioCharacters(selectedBlock, blocks, query, pageOffset)
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFB8A7FF),
        secondary = Color(0xFF80D8FF),
        background = Color(0xFF0B0D14),
        surface = Color(0xFF151925),
        onPrimary = Color(0xFF171329),
        onBackground = Color(0xFFF3F1FA),
        onSurface = Color(0xFFF3F1FA)
    )) {
        Scaffold(containerColor = Color(0xFF0B0D14)) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).background(Color(0xFF0B0D14))) {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF29204E), Color(0xFF142C42), Color(0xFF0B0D14))))
                        .padding(start = 20.dp, end = 16.dp, top = 22.dp, bottom = 20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("UNICODE STUDIO", color = Color(0xFFB8A7FF), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                Text("Character Explorer", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(shape = CircleShape, color = Color.White.copy(alpha = .10f)) {
                                Icon(Icons.Default.Tune, contentDescription = "Explorer tools", tint = Color.White, modifier = Modifier.padding(12.dp).size(21.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Discover, inspect and copy characters from every Unicode block.", color = Color(0xFFD0CDE0), fontSize = 13.sp)
                        Spacer(Modifier.height(18.dp))
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it; pageOffset = 0 },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            placeholder = { Text("Search name, symbol or U+ code") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            shape = RoundedCornerShape(18.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
                        )
                    }
                }

                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Explore", "Favorites", "Recent").forEachIndexed { index, label ->
                        FilterChip(
                            selected = activeTab == index,
                            onClick = { activeTab = index; query = ""; pageOffset = 0 },
                            label = { Text(label) },
                            leadingIcon = if (index == 1) {
                                { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else if (index == 2) {
                                { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                if (activeTab == 0) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("UNICODE BLOCK", color = Color(0xFF9995AC), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                            Box {
                                Text(selectedBlock.title + "  ▾", Modifier.clickable { blockMenu = true }.padding(vertical = 7.dp), color = Color(0xFFE5DFFF), fontWeight = FontWeight.SemiBold)
                                DropdownMenu(expanded = blockMenu, onDismissRequest = { blockMenu = false }) {
                                    blocks.forEach { block ->
                                        DropdownMenuItem(
                                            text = { Text(block.start.toString(16).uppercase().padStart(4, '0') + "  " + block.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                            onClick = { selectedBlock = block; pageOffset = 0; blockMenu = false }
                                        )
                                    }
                                }
                            }
                        }
                        Text(characters.size.toString() + " characters", color = Color(0xFF9B97AA), fontSize = 11.sp)
                    }
                }

                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        when (activeTab) { 1 -> "Saved characters"; 2 -> "Recently viewed"; else -> "Character map" },
                        color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text("Tap a character for details", color = Color(0xFF9290A0), fontSize = 10.sp)
                }

                if (characters.isEmpty()) {
                    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text(if (activeTab == 1) "No favorites yet" else if (activeTab == 2) "Your recent characters will appear here" else "No matching characters in this block", color = Color(0xFF9B97AA))
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(characters, key = { it.codePoint }) { character ->
                            CharacterTile(character, favorites.contains(character.codePoint.toString())) {
                                selectedCharacter = character
                                recents.remove(character.codePoint.toString())
                                recents.add(0, character.codePoint.toString())
                                while (recents.size > 100) recents.removeAt(recents.lastIndex)
                                preferences.edit().putString("recent", recents.joinToString(",")).apply()
                            }
                        }
                    }
                }

                if (activeTab == 0 && query.isBlank()) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Showing " + (pageOffset + 1) + "–" + (pageOffset + characters.size), color = Color(0xFF8F8B9E), fontSize = 11.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { pageOffset = (pageOffset - 240).coerceAtLeast(0) }, enabled = pageOffset > 0) { Text("Previous") }
                        Button(onClick = { pageOffset += 240 }, enabled = characters.size == 240) { Text("Next") }
                    }
                }
            }
        }
    }

    selectedCharacter?.let { character ->
        val isFavorite = favorites.contains(character.codePoint.toString())
        AlertDialog(
            onDismissRequest = { selectedCharacter = null },
            containerColor = Color(0xFF191D2B),
            title = { Text(character.glyph, fontSize = 54.sp, color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(character.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(character.code, color = Color(0xFFB8A7FF))
                    Text("Decimal: " + character.codePoint, color = Color(0xFFAAA6B9), fontSize = 12.sp)
                    Text("Block: " + (Character.UnicodeBlock.of(character.codePoint)?.toString()?.replace('_', ' ') ?: "Unknown"), color = Color(0xFFAAA6B9), fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Unicode character", character.glyph))
                    Toast.makeText(context, "Copied " + character.code, Toast.LENGTH_SHORT).show()
                    selectedCharacter = null
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Copy")
                }
            },
            dismissButton = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = {
                        val key = character.codePoint.toString()
                        if (isFavorite) favorites.remove(key) else favorites.add(key)
                        preferences.edit().putStringSet("favorites", favorites.toSet()).apply()
                    }) {
                        Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (isFavorite) "Saved" else "Favorite")
                    }
                    TextButton(onClick = { selectedCharacter = null }) { Text("Close") }
                }
            }
        )
    }
}

@Composable
private fun CharacterTile(character: StudioCharacter, favorite: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(76.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF171C29))
    ) {
        Box(Modifier.fillMaxSize().padding(5.dp)) {
            Text(character.glyph, modifier = Modifier.align(Alignment.Center), color = Color(0xFFF4F1FF), fontSize = 25.sp)
            if (favorite) Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color(0xFFFF7DAB), modifier = Modifier.align(Alignment.TopEnd).size(11.dp))
            Text(character.code, modifier = Modifier.align(Alignment.BottomCenter), color = Color(0xFF8E8A9F), fontSize = 8.sp)
        }
    }
}

private fun loadStudioBlocks(context: Context): List<StudioBlock> {
    return R.string::class.java.fields
        .filter { it.name.startsWith("script_") }
        .mapNotNull { field ->
            runCatching {
                val start = field.name.removePrefix("script_").toInt(16)
                StudioBlock(start, context.getString(field.getInt(null)).substringAfter(' ').trim())
            }.getOrNull()
        }
        .distinctBy { it.start }
        .sortedBy { it.start }
}

private fun buildStudioCharacters(selected: StudioBlock, blocks: List<StudioBlock>, query: String, offset: Int): List<StudioCharacter> {
    val nextStart = blocks.firstOrNull { it.start > selected.start }?.start ?: 0x110000
    val end = nextStart - 1
    val normalized = query.trim().uppercase()
    val codeQuery = normalized.removePrefix("U+").removePrefix("0X").toIntOrNull(16)
    if (codeQuery != null && codeQuery in selected.start..end && codeQuery <= 0x10FFFF && codeQuery !in 0xD800..0xDFFF) {
        return listOf(StudioCharacter(codeQuery))
    }
    val result = ArrayList<StudioCharacter>()
    if (normalized.isNotEmpty()) {
        var cp = selected.start
        while (cp <= end && result.size < 240) {
            if (cp !in 0xD800..0xDFFF && Character.getType(cp) != Character.UNASSIGNED) {
                val item = StudioCharacter(cp)
                if (item.name.contains(normalized, ignoreCase = true) || item.glyph.contains(query)) result.add(item)
            }
            cp++
        }
        return result
    }
    var cp = (selected.start + offset).coerceAtMost(end)
    while (cp <= end && result.size < 240) {
        if (cp !in 0xD800..0xDFFF && Character.getType(cp) != Character.UNASSIGNED) result.add(StudioCharacter(cp))
        cp++
    }
    return result
}
