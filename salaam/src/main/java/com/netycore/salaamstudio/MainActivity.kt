package com.netycore.george

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF202B55)
private val Blue = Color(0xFF3949AB)
private val Accent = Color(0xFFE83EBD)
private val Canvas = Color(0xFFF0F2F4)
private val Cell = Color(0xFFFFFFFF)
private val Muted = Color(0xFF788092)

private data class Board(val name: String, val chars: List<String>)

private val boards = listOf(
    Board("ALPHABET", ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz").map { it.toString() } +
        listOf("Ⓐ","Ⓑ","Ⓒ","ⓐ","ⓑ","ⓒ","𝐀","𝐁","𝑨","𝒜","𝔄","𝔸","Ａ","ᴬ","ᴀ")),
    Board("NUMBERS", listOf("0","1","2","3","4","5","6","7","8","9","⓪","①","②","③","④","⑤","⑥","⑦","⑧","⑨","⑴","⑵","⑶","⑷","⑸","⅟","½","⅓","⅔","¼","¾","⅛","⅜","⅝","⅞")),
    Board("MATH-OPERATORS", listOf("√","∛","∜","∑","∏","∫","∬","∮","∆","∇","±","∓","×","÷","·","⋅","∙","∘","∗","∧","∨","¬","∩","∪","⊕","⊗","⊙","≠","≤","≥","≈","≡","∞","∝","∴","∵","∈","∉","∋","∅","∀","∃")),
    Board("GEOMETRY", listOf("△","▽","◁","▷","◀","▶","▲","▼","◆","◇","■","□","▪","▫","●","○","◉","◎","◌","◍","◐","◑","◒","◓","◔","◕","⬟","⬢","⬡","⬠")),
    Board("SHAPES", listOf("★","☆","✦","✧","✩","✪","✫","✬","✭","✮","✯","✰","♡","♥","❤","❥","❣","❖","※","⁂","✿","❀","❁","❃","❋","☀","☁","☂","☯","☮","⚑","⚙")),
    Board("ARROWS", listOf("←","↑","→","↓","↔","↕","↖","↗","↘","↙","⇐","⇑","⇒","⇓","⇔","⇕","➜","➤","➔","⟵","⟶","⟷","⤴","⤵")),
    Board("ARABIC", listOf("ء","آ","أ","ؤ","إ","ئ","ا","ب","ة","ت","ث","ج","ح","خ","د","ذ","ر","ز","س","ش","ص","ض","ط","ظ","ع","غ","ف","ق","ك","ل","م","ن","ه","و","ي","لا","ﷲ","ﷺ")),
    Board("JAPANESE", listOf("あ","い","う","え","お","か","き","く","け","こ","さ","し","す","せ","そ","た","ち","つ","て","と","な","に","ぬ","ね","の","は","ひ","ふ","へ","ほ","ま","み","む","め","も","や","ゆ","よ","ら","り","る","れ","ろ","わ","を","ん","ア","イ","ウ","エ","オ","カ","キ","ク","ケ","コ")),
    Board("CHINESE", listOf("一","二","三","四","五","六","七","八","九","十","人","大","小","中","山","水","火","木","金","土","日","月","年","天","地","文","字","語","学","愛","心","好","明","龍","風")),
    Board("EMOJI", listOf("😀","😁","😂","🤣","😊","😍","🥰","😘","😎","🤔","😭","😴","🔥","✨","⭐","❤️","💙","💜","💚","🖤","👍","👏","🙏","💯","🎉","🌹","☀️","🌙","⚡","🍀")),
    Board("CLIPBOARD", emptyList())
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GeorgeApp() }
    }
}

@Composable
private fun GeorgeApp() {
    var text by remember { mutableStateOf("") }
    var selectedBoard by remember { mutableIntStateOf(0) }
    var fontSize by remember { mutableIntStateOf(22) }
    var findText by remember { mutableStateOf("") }
    var replaceText by remember { mutableStateOf("") }
    var showFind by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var selecting by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }
    val clipboard = LocalClipboardManager.current
    val recent = remember { mutableStateListOf<String>() }
    val custom = remember { mutableStateListOf<String>() }
    val activeBoard = boards[selectedBoard]
    val chars = when (activeBoard.name) {
        "CLIPBOARD" -> recent.toList().asReversed()
        else -> activeBoard.chars
    } + if (activeBoard.name == "CUSTOM") custom.toList() else emptyList()

    fun insert(value: String) {
        history.add(text)
        text += value
        recent.remove(value)
        recent.add(value)
    }
    fun undo() { if (history.isNotEmpty()) { text = history.removeAt(history.lastIndex) } }

    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Canvas, surface = Cell)) {
        Scaffold(
            containerColor = Canvas,
            topBar = {
                Row(Modifier.fillMaxWidth().background(Blue).padding(horizontal = 18.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("GEORGE", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                        Text("UNICODE CHARACTER BOARD", color = Color.White.copy(alpha = .72f), fontSize = 9.sp, letterSpacing = 1.5.sp)
                    }
                    Text("Text size", color = Color.White.copy(alpha = .8f), fontSize = 10.sp)
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = { fontSize = if (fontSize >= 36) 12 else fontSize + 2 }) {
                        Text("$fontSize  ▾", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = { showInfo = true }) { Icon(Icons.Rounded.MoreVert, null, tint = Color.White) }
                }
            },
            bottomBar = {
                Row(Modifier.fillMaxWidth().background(Blue).navigationBarsPadding().padding(horizontal = 5.dp, vertical = 7.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    BottomAction(Icons.Rounded.ContentCopy, "Copy") { clipboard.setText(AnnotatedString(text)) }
                    BottomAction(Icons.Rounded.ContentCut, "Cut") { clipboard.setText(AnnotatedString(text)); text = "" }
                    BottomAction(Icons.Rounded.ContentPaste, "Paste") { clipboard.getText()?.let { insert(it.text) } }
                    BottomAction(Icons.Rounded.SpaceBar, "Space") { insert(" ") }
                    BottomAction(Icons.Rounded.SubdirectoryArrowLeft, "Enter") { insert("\n") }
                    BottomAction(Icons.Rounded.Backspace, "Delete") { if (text.isNotEmpty()) { history.add(text); text = text.dropLast(1) } }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                // Editor surface
                Card(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp), shape = RoundedCornerShape(13.dp), colors = CardDefaults.cardColors(containerColor = Cell), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(11.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("YOUR TEXT", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.weight(1f))
                            Text("${text.length} characters", color = Muted, fontSize = 10.sp)
                            IconButton(onClick = { clipboard.setText(AnnotatedString(text)) }, modifier = Modifier.size(34.dp)) { Icon(Icons.Rounded.Share, null, tint = Blue) }
                            IconButton(onClick = { text = "" }, modifier = Modifier.size(34.dp)) { Icon(Icons.Rounded.DeleteOutline, null, tint = Blue) }
                        }
                        Box(Modifier.fillMaxWidth().heightIn(min = 112.dp, max = 180.dp).verticalScroll(rememberScrollState()).padding(vertical = 5.dp)) {
                            if (text.isEmpty()) Text("Tap characters below to build your text…", color = Muted.copy(alpha = .7f), fontSize = 17.sp)
                            BasicTextField(value = text, onValueChange = { history.add(text); text = it }, modifier = Modifier.fillMaxWidth(), textStyle = androidx.compose.ui.text.TextStyle(color = Ink, fontSize = fontSize.sp, lineHeight = (fontSize + 9).sp))
                        }
                        Divider(color = Canvas)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            EditorAction(Icons.Rounded.Undo, "Undo", { undo() })
                            EditorAction(Icons.Rounded.Redo, "Redo", { })
                            EditorAction(Icons.Rounded.Keyboard, "Keyboard", { })
                            EditorAction(Icons.Rounded.Search, "Find", { showFind = !showFind })
                            EditorAction(Icons.Rounded.Info, "Info", { showInfo = true })
                            EditorAction(Icons.Rounded.SelectAll, "Select", { selecting = !selecting })
                        }
                        if (showFind) {
                            OutlinedTextField(findText, { findText = it }, Modifier.fillMaxWidth(), label = { Text("Find") }, singleLine = true)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(replaceText, { replaceText = it }, Modifier.weight(1f), label = { Text("Replace with") }, singleLine = true)
                                TextButton(onClick = { if (findText.isNotEmpty()) text = text.replaceFirst(findText, replaceText) }) { Text("Replace") }
                                TextButton(onClick = { if (findText.isNotEmpty()) text = text.replace(findText, replaceText) }) { Text("All") }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (selecting) "SELECTION MODE" else "CHARACTER CATEGORIES", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = { selectedBoard = boards.indexOfFirst { it.name == "CLIPBOARD" }.coerceAtLeast(0) }) { Text("RECENT", fontSize = 10.sp) }
                    TextButton(onClick = { selectedBoard = boards.indexOfFirst { it.name == "EMOJI" } }) { Text("EMOJI", fontSize = 10.sp) }
                }
                ScrollableTabRow(selectedTabIndex = selectedBoard, edgePadding = 8.dp, containerColor = Blue, contentColor = Color.White, indicator = { positions ->
                    if (selectedBoard < positions.size) TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(positions[selectedBoard]), color = Accent, height = 3.dp)
                }) {
                    boards.forEachIndexed { index, board ->
                        Tab(selected = selectedBoard == index, onClick = { selectedBoard = index }, text = { Text(board.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1) })
                    }
                }
                if (activeBoard.name == "CLIPBOARD" && chars.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Recently used characters appear here", color = Muted) }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 7.dp, end = 7.dp, top = 7.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(chars.size) { index ->
                            val ch = chars[index]
                            Surface(onClick = { insert(ch) }, color = Cell, shape = RoundedCornerShape(4.dp), modifier = Modifier.aspectRatio(1.05f), shadowElevation = 1.dp) {
                                Box(contentAlignment = Alignment.Center) { Text(ch, color = Ink, fontSize = 22.sp, maxLines = 1) }
                            }
                        }
                    }
                }
            }
        }
        if (showInfo) {
            AlertDialog(onDismissRequest = { showInfo = false }, confirmButton = { TextButton(onClick = { showInfo = false }) { Text("Close") } }, title = { Text("George Character Info") }, text = { Text("Unicode character board and text editor. Tap any table cell to insert it into your editor.\n\nCharacters: ${text.codePointCount(0, text.length)}\nUTF-16 units: ${text.length}") })
        }
    }
}

@Composable
private fun BottomAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, action: () -> Unit) {
    TextButton(onClick = action, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(19.dp))
            Text(label, color = Color.White, fontSize = 9.sp)
        }
    }
}

@Composable
private fun EditorAction(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, action: () -> Unit) {
    TextButton(onClick = action, contentPadding = PaddingValues(horizontal = 3.dp, vertical = 2.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = Blue, modifier = Modifier.size(19.dp))
            Text(label, color = Muted, fontSize = 8.sp)
        }
    }
}
