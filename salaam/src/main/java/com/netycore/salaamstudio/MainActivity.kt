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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
private data class UnicodeBlock(val start: Int, val end: Int, val name: String)
private val unicodeBlocks = listOf(
    UnicodeBlock(0x0, 0x7F, "Basic Latin"),
    UnicodeBlock(0x80, 0xFF, "Latin-1 Supplement"),
    UnicodeBlock(0x100, 0x17F, "Latin Extended-A"),
    UnicodeBlock(0x180, 0x24F, "Latin Extended-B"),
    UnicodeBlock(0x250, 0x2AF, "IPA Extensions"),
    UnicodeBlock(0x2B0, 0x2FF, "Spacing Modifier Letters"),
    UnicodeBlock(0x300, 0x36F, "Combining Diacritical Marks"),
    UnicodeBlock(0x370, 0x3FF, "Greek and Coptic"),
    UnicodeBlock(0x400, 0x4FF, "Cyrillic"),
    UnicodeBlock(0x500, 0x52F, "Cyrillic Supplement"),
    UnicodeBlock(0x530, 0x58F, "Armenian"),
    UnicodeBlock(0x590, 0x5FF, "Hebrew"),
    UnicodeBlock(0x600, 0x6FF, "Arabic"),
    UnicodeBlock(0x700, 0x74F, "Syriac"),
    UnicodeBlock(0x750, 0x77F, "Arabic Supplement"),
    UnicodeBlock(0x780, 0x7BF, "Thaana"),
    UnicodeBlock(0x7C0, 0x7FF, "NKo"),
    UnicodeBlock(0x800, 0x83F, "Samaritan"),
    UnicodeBlock(0x840, 0x85F, "Mandaic"),
    UnicodeBlock(0x860, 0x86F, "Syriac Supplement"),
    UnicodeBlock(0x870, 0x89F, "Arabic Extended-B"),
    UnicodeBlock(0x8A0, 0x8FF, "Arabic Extended-A"),
    UnicodeBlock(0x900, 0x97F, "Devanagari"),
    UnicodeBlock(0x980, 0x9FF, "Bengali"),
    UnicodeBlock(0xA00, 0xA7F, "Gurmukhi"),
    UnicodeBlock(0xA80, 0xAFF, "Gujarati"),
    UnicodeBlock(0xB00, 0xB7F, "Oriya"),
    UnicodeBlock(0xB80, 0xBFF, "Tamil"),
    UnicodeBlock(0xC00, 0xC7F, "Telugu"),
    UnicodeBlock(0xC80, 0xCFF, "Kannada"),
    UnicodeBlock(0xD00, 0xD7F, "Malayalam"),
    UnicodeBlock(0xD80, 0xDFF, "Sinhala"),
    UnicodeBlock(0xE00, 0xE7F, "Thai"),
    UnicodeBlock(0xE80, 0xEFF, "Lao"),
    UnicodeBlock(0xF00, 0xFFF, "Tibetan"),
    UnicodeBlock(0x1000, 0x109F, "Myanmar"),
    UnicodeBlock(0x10A0, 0x10FF, "Georgian"),
    UnicodeBlock(0x1100, 0x11FF, "Hangul Jamo"),
    UnicodeBlock(0x1200, 0x137F, "Ethiopic"),
    UnicodeBlock(0x1380, 0x139F, "Ethiopic Supplement"),
    UnicodeBlock(0x13A0, 0x13FF, "Cherokee"),
    UnicodeBlock(0x1400, 0x167F, "Unified Canadian Aboriginal Syllabics"),
    UnicodeBlock(0x1680, 0x169F, "Ogham"),
    UnicodeBlock(0x16A0, 0x16FF, "Runic"),
    UnicodeBlock(0x1700, 0x171F, "Tagalog"),
    UnicodeBlock(0x1720, 0x173F, "Hanunoo"),
    UnicodeBlock(0x1740, 0x175F, "Buhid"),
    UnicodeBlock(0x1760, 0x177F, "Tagbanwa"),
    UnicodeBlock(0x1780, 0x17FF, "Khmer"),
    UnicodeBlock(0x1800, 0x18AF, "Mongolian"),
    UnicodeBlock(0x18B0, 0x18FF, "Unified Canadian Aboriginal Syllabics Extended"),
    UnicodeBlock(0x1900, 0x194F, "Limbu"),
    UnicodeBlock(0x1950, 0x197F, "Tai Le"),
    UnicodeBlock(0x1980, 0x19DF, "New Tai Lue"),
    UnicodeBlock(0x19E0, 0x19FF, "Khmer Symbols"),
    UnicodeBlock(0x1A00, 0x1A1F, "Buginese"),
    UnicodeBlock(0x1A20, 0x1AAF, "Tai Tham"),
    UnicodeBlock(0x1AB0, 0x1AFF, "Combining Diacritical Marks Extended"),
    UnicodeBlock(0x1B00, 0x1B7F, "Balinese"),
    UnicodeBlock(0x1B80, 0x1BBF, "Sundanese"),
    UnicodeBlock(0x1BC0, 0x1BFF, "Batak"),
    UnicodeBlock(0x1C00, 0x1C4F, "Lepcha"),
    UnicodeBlock(0x1C50, 0x1C7F, "Ol Chiki"),
    UnicodeBlock(0x1C80, 0x1C8F, "Cyrillic Extended-C"),
    UnicodeBlock(0x1C90, 0x1CBF, "Georgian Extended"),
    UnicodeBlock(0x1CC0, 0x1CCF, "Sundanese Supplement"),
    UnicodeBlock(0x1CD0, 0x1CFF, "Vedic Extensions"),
    UnicodeBlock(0x1D00, 0x1D7F, "Phonetic Extensions"),
    UnicodeBlock(0x1D80, 0x1DBF, "Phonetic Extensions Supplement"),
    UnicodeBlock(0x1DC0, 0x1DFF, "Combining Diacritical Marks Supplement"),
    UnicodeBlock(0x1E00, 0x1EFF, "Latin Extended Additional"),
    UnicodeBlock(0x1F00, 0x1FFF, "Greek Extended"),
    UnicodeBlock(0x2000, 0x206F, "General Punctuation"),
    UnicodeBlock(0x2070, 0x209F, "Superscripts and Subscripts"),
    UnicodeBlock(0x20A0, 0x20CF, "Currency Symbols"),
    UnicodeBlock(0x20D0, 0x20FF, "Combining Diacritical Marks for Symbols"),
    UnicodeBlock(0x2100, 0x214F, "Letterlike Symbols"),
    UnicodeBlock(0x2150, 0x218F, "Number Forms"),
    UnicodeBlock(0x2190, 0x21FF, "Arrows"),
    UnicodeBlock(0x2200, 0x22FF, "Mathematical Operators"),
    UnicodeBlock(0x2300, 0x23FF, "Miscellaneous Technical"),
    UnicodeBlock(0x2400, 0x243F, "Control Pictures"),
    UnicodeBlock(0x2440, 0x245F, "Optical Character Recognition"),
    UnicodeBlock(0x2460, 0x24FF, "Enclosed Alphanumerics"),
    UnicodeBlock(0x2500, 0x257F, "Box Drawing"),
    UnicodeBlock(0x2580, 0x259F, "Block Elements"),
    UnicodeBlock(0x25A0, 0x25FF, "Geometric Shapes"),
    UnicodeBlock(0x2600, 0x26FF, "Miscellaneous Symbols"),
    UnicodeBlock(0x2700, 0x27BF, "Dingbats"),
    UnicodeBlock(0x27C0, 0x27EF, "Miscellaneous Mathematical Symbols-A"),
    UnicodeBlock(0x27F0, 0x27FF, "Supplemental Arrows-A"),
    UnicodeBlock(0x2800, 0x28FF, "Braille Patterns"),
    UnicodeBlock(0x2900, 0x297F, "Supplemental Arrows-B"),
    UnicodeBlock(0x2980, 0x29FF, "Miscellaneous Mathematical Symbols-B"),
    UnicodeBlock(0x2A00, 0x2AFF, "Supplemental Mathematical Operators"),
    UnicodeBlock(0x2B00, 0x2BFF, "Miscellaneous Symbols and Arrows"),
    UnicodeBlock(0x2C00, 0x2C5F, "Glagolitic"),
    UnicodeBlock(0x2C60, 0x2C7F, "Latin Extended-C"),
    UnicodeBlock(0x2C80, 0x2CFF, "Coptic"),
    UnicodeBlock(0x2D00, 0x2D2F, "Georgian Supplement"),
    UnicodeBlock(0x2D30, 0x2D7F, "Tifinagh"),
    UnicodeBlock(0x2D80, 0x2DDF, "Ethiopic Extended"),
    UnicodeBlock(0x2DE0, 0x2DFF, "Cyrillic Extended-A"),
    UnicodeBlock(0x2E00, 0x2E7F, "Supplemental Punctuation"),
    UnicodeBlock(0x2E80, 0x2EFF, "CJK Radicals Supplement"),
    UnicodeBlock(0x2F00, 0x2FDF, "Kangxi Radicals"),
    UnicodeBlock(0x2FF0, 0x2FFF, "Ideographic Description Characters"),
    UnicodeBlock(0x3000, 0x303F, "CJK Symbols and Punctuation"),
    UnicodeBlock(0x3040, 0x309F, "Hiragana"),
    UnicodeBlock(0x30A0, 0x30FF, "Katakana"),
    UnicodeBlock(0x3100, 0x312F, "Bopomofo"),
    UnicodeBlock(0x3130, 0x318F, "Hangul Compatibility Jamo"),
    UnicodeBlock(0x3190, 0x319F, "Kanbun"),
    UnicodeBlock(0x31A0, 0x31BF, "Bopomofo Extended"),
    UnicodeBlock(0x31C0, 0x31EF, "CJK Strokes"),
    UnicodeBlock(0x31F0, 0x31FF, "Katakana Phonetic Extensions"),
    UnicodeBlock(0x3200, 0x32FF, "Enclosed CJK Letters and Months"),
    UnicodeBlock(0x3300, 0x33FF, "CJK Compatibility"),
    UnicodeBlock(0x3400, 0x4DBF, "CJK Unified Ideographs Extension A"),
    UnicodeBlock(0x4DC0, 0x4DFF, "Yijing Hexagram Symbols"),
    UnicodeBlock(0x4E00, 0x9FFF, "CJK Unified Ideographs"),
    UnicodeBlock(0xA000, 0xA48F, "Yi Syllables"),
    UnicodeBlock(0xA490, 0xA4CF, "Yi Radicals"),
    UnicodeBlock(0xA4D0, 0xA4FF, "Lisu"),
    UnicodeBlock(0xA500, 0xA63F, "Vai"),
    UnicodeBlock(0xA640, 0xA69F, "Cyrillic Extended-B"),
    UnicodeBlock(0xA6A0, 0xA6FF, "Bamum"),
    UnicodeBlock(0xA700, 0xA71F, "Modifier Tone Letters"),
    UnicodeBlock(0xA720, 0xA7FF, "Latin Extended-D"),
    UnicodeBlock(0xA800, 0xA82F, "Syloti Nagri"),
    UnicodeBlock(0xA830, 0xA83F, "Common Indic Number Forms"),
    UnicodeBlock(0xA840, 0xA87F, "Phags-pa"),
    UnicodeBlock(0xA880, 0xA8DF, "Saurashtra"),
    UnicodeBlock(0xA8E0, 0xA8FF, "Devanagari Extended"),
    UnicodeBlock(0xA900, 0xA92F, "Kayah Li"),
    UnicodeBlock(0xA930, 0xA95F, "Rejang"),
    UnicodeBlock(0xA960, 0xA97F, "Hangul Jamo Extended-A"),
    UnicodeBlock(0xA980, 0xA9DF, "Javanese"),
    UnicodeBlock(0xA9E0, 0xA9FF, "Myanmar Extended-B"),
    UnicodeBlock(0xAA00, 0xAA5F, "Cham"),
    UnicodeBlock(0xAA60, 0xAA7F, "Myanmar Extended-A"),
    UnicodeBlock(0xAA80, 0xAADF, "Tai Viet"),
    UnicodeBlock(0xAAE0, 0xAAFF, "Meetei Mayek Extensions"),
    UnicodeBlock(0xAB00, 0xAB2F, "Ethiopic Extended-A"),
    UnicodeBlock(0xAB30, 0xAB6F, "Latin Extended-E"),
    UnicodeBlock(0xAB70, 0xABBF, "Cherokee Supplement"),
    UnicodeBlock(0xABC0, 0xABFF, "Meetei Mayek"),
    UnicodeBlock(0xAC00, 0xD7AF, "Hangul Syllables"),
    UnicodeBlock(0xD7B0, 0xD7FF, "Hangul Jamo Extended-B"),
    UnicodeBlock(0xE000, 0xF8FF, "Private Use Area"),
    UnicodeBlock(0xF900, 0xFAFF, "CJK Compatibility Ideographs"),
    UnicodeBlock(0xFB00, 0xFB4F, "Alphabetic Presentation Forms"),
    UnicodeBlock(0xFB50, 0xFDFF, "Arabic Presentation Forms-A"),
    UnicodeBlock(0xFE00, 0xFE0F, "Variation Selectors"),
    UnicodeBlock(0xFE10, 0xFE1F, "Vertical Forms"),
    UnicodeBlock(0xFE20, 0xFE2F, "Combining Half Marks"),
    UnicodeBlock(0xFE30, 0xFE4F, "CJK Compatibility Forms"),
    UnicodeBlock(0xFE50, 0xFE6F, "Small Form Variants"),
    UnicodeBlock(0xFE70, 0xFEFF, "Arabic Presentation Forms-B"),
    UnicodeBlock(0xFF00, 0xFFEF, "Halfwidth and Fullwidth Forms"),
    UnicodeBlock(0xFFF0, 0xFFFF, "Specials"),
    UnicodeBlock(0x10000, 0x1007F, "Linear B Syllabary"),
    UnicodeBlock(0x10080, 0x100FF, "Linear B Ideograms"),
    UnicodeBlock(0x10100, 0x1013F, "Aegean Numbers"),
    UnicodeBlock(0x10140, 0x1018F, "Ancient Greek Numbers"),
    UnicodeBlock(0x10190, 0x101CF, "Ancient Symbols"),
    UnicodeBlock(0x101D0, 0x101FF, "Phaistos Disc"),
    UnicodeBlock(0x10280, 0x1029F, "Lycian"),
    UnicodeBlock(0x102A0, 0x102DF, "Carian"),
    UnicodeBlock(0x102E0, 0x102FF, "Coptic Epact Numbers"),
    UnicodeBlock(0x10300, 0x1032F, "Old Italic"),
    UnicodeBlock(0x10330, 0x1034F, "Gothic"),
    UnicodeBlock(0x10350, 0x1037F, "Old Permic"),
    UnicodeBlock(0x10380, 0x1039F, "Ugaritic"),
    UnicodeBlock(0x103A0, 0x103DF, "Old Persian"),
    UnicodeBlock(0x10400, 0x1044F, "Deseret"),
    UnicodeBlock(0x10450, 0x1047F, "Shavian"),
    UnicodeBlock(0x10480, 0x104AF, "Osmanya"),
    UnicodeBlock(0x104B0, 0x104FF, "Osage"),
    UnicodeBlock(0x10500, 0x1052F, "Elbasan"),
    UnicodeBlock(0x10530, 0x1056F, "Caucasian Albanian"),
    UnicodeBlock(0x10570, 0x105BF, "Vithkuqi"),
    UnicodeBlock(0x105C0, 0x105FF, "Todhri"),
    UnicodeBlock(0x10600, 0x1077F, "Linear A"),
    UnicodeBlock(0x10780, 0x107BF, "Latin Extended-F"),
    UnicodeBlock(0x10800, 0x1083F, "Cypriot Syllabary"),
    UnicodeBlock(0x10840, 0x1085F, "Imperial Aramaic"),
    UnicodeBlock(0x10860, 0x1087F, "Palmyrene"),
    UnicodeBlock(0x10880, 0x108AF, "Nabataean"),
    UnicodeBlock(0x108E0, 0x108FF, "Hatran"),
    UnicodeBlock(0x10900, 0x1091F, "Phoenician"),
    UnicodeBlock(0x10920, 0x1093F, "Lydian"),
    UnicodeBlock(0x10940, 0x1095F, "Sidetic"),
    UnicodeBlock(0x10980, 0x1099F, "Meroitic Hieroglyphs"),
    UnicodeBlock(0x109A0, 0x109FF, "Meroitic Cursive"),
    UnicodeBlock(0x10A00, 0x10A5F, "Kharoshthi"),
    UnicodeBlock(0x10A60, 0x10A7F, "Old South Arabian"),
    UnicodeBlock(0x10A80, 0x10A9F, "Old North Arabian"),
    UnicodeBlock(0x10AC0, 0x10AFF, "Manichaean"),
    UnicodeBlock(0x10B00, 0x10B3F, "Avestan"),
    UnicodeBlock(0x10B40, 0x10B5F, "Inscriptional Parthian"),
    UnicodeBlock(0x10B60, 0x10B7F, "Inscriptional Pahlavi"),
    UnicodeBlock(0x10B80, 0x10BAF, "Psalter Pahlavi"),
    UnicodeBlock(0x10C00, 0x10C4F, "Old Turkic"),
    UnicodeBlock(0x10C80, 0x10CFF, "Old Hungarian"),
    UnicodeBlock(0x10D00, 0x10D3F, "Hanifi Rohingya"),
    UnicodeBlock(0x10D40, 0x10D8F, "Garay"),
    UnicodeBlock(0x10E60, 0x10E7F, "Rumi Numeral Symbols"),
    UnicodeBlock(0x10E80, 0x10EBF, "Yezidi"),
    UnicodeBlock(0x10EC0, 0x10EFF, "Arabic Extended-C"),
    UnicodeBlock(0x10F00, 0x10F2F, "Old Sogdian"),
    UnicodeBlock(0x10F30, 0x10F6F, "Sogdian"),
    UnicodeBlock(0x10F70, 0x10FAF, "Old Uyghur"),
    UnicodeBlock(0x10FB0, 0x10FDF, "Chorasmian"),
    UnicodeBlock(0x10FE0, 0x10FFF, "Elymaic"),
    UnicodeBlock(0x11000, 0x1107F, "Brahmi"),
    UnicodeBlock(0x11080, 0x110CF, "Kaithi"),
    UnicodeBlock(0x110D0, 0x110FF, "Sora Sompeng"),
    UnicodeBlock(0x11100, 0x1114F, "Chakma"),
    UnicodeBlock(0x11150, 0x1117F, "Mahajani"),
    UnicodeBlock(0x11180, 0x111DF, "Sharada"),
    UnicodeBlock(0x111E0, 0x111FF, "Sinhala Archaic Numbers"),
    UnicodeBlock(0x11200, 0x1124F, "Khojki"),
    UnicodeBlock(0x11280, 0x112AF, "Multani"),
    UnicodeBlock(0x112B0, 0x112FF, "Khudawadi"),
    UnicodeBlock(0x11300, 0x1137F, "Grantha"),
    UnicodeBlock(0x11380, 0x113FF, "Tulu-Tigalari"),
    UnicodeBlock(0x11400, 0x1147F, "Newa"),
    UnicodeBlock(0x11480, 0x114DF, "Tirhuta"),
    UnicodeBlock(0x11580, 0x115FF, "Siddham"),
    UnicodeBlock(0x11600, 0x1165F, "Modi"),
    UnicodeBlock(0x11660, 0x1167F, "Mongolian Supplement"),
    UnicodeBlock(0x11680, 0x116CF, "Takri"),
    UnicodeBlock(0x116D0, 0x116FF, "Myanmar Extended-C"),
    UnicodeBlock(0x11700, 0x1173F, "Ahom"),
    UnicodeBlock(0x11800, 0x1184F, "Dogra"),
    UnicodeBlock(0x118A0, 0x118FF, "Warang Citi"),
    UnicodeBlock(0x11900, 0x1195F, "Dives Akuru"),
    UnicodeBlock(0x119A0, 0x119FF, "Nandinagari"),
    UnicodeBlock(0x11A00, 0x11A4F, "Zanabazar Square"),
    UnicodeBlock(0x11A50, 0x11AAF, "Soyombo"),
    UnicodeBlock(0x11AB0, 0x11ABF, "Unified Canadian Aboriginal Syllabics Extended-A"),
    UnicodeBlock(0x11AC0, 0x11AFF, "Pau Cin Hau"),
    UnicodeBlock(0x11B00, 0x11B5F, "Devanagari Extended-A"),
    UnicodeBlock(0x11B60, 0x11B7F, "Sharada Supplement"),
    UnicodeBlock(0x11BC0, 0x11BFF, "Sunuwar"),
    UnicodeBlock(0x11C00, 0x11C6F, "Bhaiksuki"),
    UnicodeBlock(0x11C70, 0x11CBF, "Marchen"),
    UnicodeBlock(0x11D00, 0x11D5F, "Masaram Gondi"),
    UnicodeBlock(0x11D60, 0x11DAF, "Gunjala Gondi"),
    UnicodeBlock(0x11DB0, 0x11DEF, "Tolong Siki"),
    UnicodeBlock(0x11DF0, 0x11DFF, "Bengali Supplement"),
    UnicodeBlock(0x11EE0, 0x11EFF, "Makasar"),
    UnicodeBlock(0x11F00, 0x11F5F, "Kawi"),
    UnicodeBlock(0x11FB0, 0x11FBF, "Lisu Supplement"),
    UnicodeBlock(0x11FC0, 0x11FFF, "Tamil Supplement"),
    UnicodeBlock(0x12000, 0x123FF, "Cuneiform"),
    UnicodeBlock(0x12400, 0x1247F, "Cuneiform Numbers and Punctuation"),
    UnicodeBlock(0x12480, 0x1254F, "Early Dynastic Cuneiform"),
    UnicodeBlock(0x12550, 0x1268F, "Archaic Cuneiform Numerals"),
    UnicodeBlock(0x12F90, 0x12FFF, "Cypro-Minoan"),
    UnicodeBlock(0x13000, 0x1342F, "Egyptian Hieroglyphs"),
    UnicodeBlock(0x13430, 0x1343F, "Egyptian Hieroglyph Format Controls"),
    UnicodeBlock(0x13460, 0x143FF, "Egyptian Hieroglyphs Extended-A"),
    UnicodeBlock(0x14400, 0x1467F, "Anatolian Hieroglyphs"),
    UnicodeBlock(0x16100, 0x1613F, "Gurung Khema"),
    UnicodeBlock(0x16800, 0x16A3F, "Bamum Supplement"),
    UnicodeBlock(0x16A40, 0x16A6F, "Mro"),
    UnicodeBlock(0x16A70, 0x16ACF, "Tangsa"),
    UnicodeBlock(0x16AD0, 0x16AFF, "Bassa Vah"),
    UnicodeBlock(0x16B00, 0x16B8F, "Pahawh Hmong"),
    UnicodeBlock(0x16D40, 0x16D7F, "Kirat Rai"),
    UnicodeBlock(0x16E40, 0x16E9F, "Medefaidrin"),
    UnicodeBlock(0x16EA0, 0x16EDF, "Beria Erfe"),
    UnicodeBlock(0x16F00, 0x16F9F, "Miao"),
    UnicodeBlock(0x16FE0, 0x16FFF, "Ideographic Symbols and Punctuation"),
    UnicodeBlock(0x17000, 0x187FF, "Tangut"),
    UnicodeBlock(0x18800, 0x18AFF, "Tangut Components"),
    UnicodeBlock(0x18B00, 0x18CFF, "Khitan Small Script"),
    UnicodeBlock(0x18D00, 0x18D8F, "Tangut Supplement"),
    UnicodeBlock(0x18D80, 0x18DFF, "Tangut Components Supplement"),
    UnicodeBlock(0x18E00, 0x1919F, "Jurchen"),
    UnicodeBlock(0x191A0, 0x191DF, "Jurchen Radicals"),
    UnicodeBlock(0x1AFF0, 0x1AFFF, "Kana Extended-B"),
    UnicodeBlock(0x1B000, 0x1B0FF, "Kana Supplement"),
    UnicodeBlock(0x1B100, 0x1B12F, "Kana Extended-A"),
    UnicodeBlock(0x1B130, 0x1B16F, "Small Kana Extension"),
    UnicodeBlock(0x1B170, 0x1B2FF, "Nushu"),
    UnicodeBlock(0x1BC00, 0x1BC9F, "Duployan"),
    UnicodeBlock(0x1BCA0, 0x1BCAF, "Shorthand Format Controls"),
    UnicodeBlock(0x1CC00, 0x1CEBF, "Symbols for Legacy Computing Supplement"),
    UnicodeBlock(0x1CEC0, 0x1CEFF, "Miscellaneous Symbols Supplement"),
    UnicodeBlock(0x1CF00, 0x1CFCF, "Znamenny Musical Notation"),
    UnicodeBlock(0x1D000, 0x1D0FF, "Byzantine Musical Symbols"),
    UnicodeBlock(0x1D100, 0x1D1FF, "Musical Symbols"),
    UnicodeBlock(0x1D200, 0x1D24F, "Ancient Greek Musical Notation"),
    UnicodeBlock(0x1D250, 0x1D28F, "Musical Symbols Supplement"),
    UnicodeBlock(0x1D2C0, 0x1D2DF, "Kaktovik Numerals"),
    UnicodeBlock(0x1D2E0, 0x1D2FF, "Mayan Numerals"),
    UnicodeBlock(0x1D300, 0x1D35F, "Tai Xuan Jing Symbols"),
    UnicodeBlock(0x1D360, 0x1D37F, "Counting Rod Numerals"),
    UnicodeBlock(0x1D400, 0x1D7FF, "Mathematical Alphanumeric Symbols"),
    UnicodeBlock(0x1D800, 0x1DAAF, "Sutton SignWriting"),
    UnicodeBlock(0x1DB00, 0x1DBFF, "Miscellaneous Symbols and Arrows Extended"),
    UnicodeBlock(0x1DF00, 0x1DFFF, "Latin Extended-G"),
    UnicodeBlock(0x1E000, 0x1E02F, "Glagolitic Supplement"),
    UnicodeBlock(0x1E030, 0x1E08F, "Cyrillic Extended-D"),
    UnicodeBlock(0x1E100, 0x1E14F, "Nyiakeng Puachue Hmong"),
    UnicodeBlock(0x1E290, 0x1E2BF, "Toto"),
    UnicodeBlock(0x1E2C0, 0x1E2FF, "Wancho"),
    UnicodeBlock(0x1E4D0, 0x1E4FF, "Nag Mundari"),
    UnicodeBlock(0x1E5D0, 0x1E5FF, "Ol Onal"),
    UnicodeBlock(0x1E6C0, 0x1E6FF, "Tai Yo"),
    UnicodeBlock(0x1E7E0, 0x1E7FF, "Ethiopic Extended-B"),
    UnicodeBlock(0x1E800, 0x1E8DF, "Mende Kikakui"),
    UnicodeBlock(0x1E900, 0x1E95F, "Adlam"),
    UnicodeBlock(0x1EC70, 0x1ECBF, "Indic Siyaq Numbers"),
    UnicodeBlock(0x1ED00, 0x1ED4F, "Ottoman Siyaq Numbers"),
    UnicodeBlock(0x1EE00, 0x1EEFF, "Arabic Mathematical Alphabetic Symbols"),
    UnicodeBlock(0x1F000, 0x1F02F, "Mahjong Tiles"),
    UnicodeBlock(0x1F030, 0x1F09F, "Domino Tiles"),
    UnicodeBlock(0x1F0A0, 0x1F0FF, "Playing Cards"),
    UnicodeBlock(0x1F100, 0x1F1FF, "Enclosed Alphanumeric Supplement"),
    UnicodeBlock(0x1F200, 0x1F2FF, "Enclosed Ideographic Supplement"),
    UnicodeBlock(0x1F300, 0x1F5FF, "Miscellaneous Symbols and Pictographs"),
    UnicodeBlock(0x1F600, 0x1F64F, "Emoticons"),
    UnicodeBlock(0x1F650, 0x1F67F, "Ornamental Dingbats"),
    UnicodeBlock(0x1F680, 0x1F6FF, "Transport and Map Symbols"),
    UnicodeBlock(0x1F700, 0x1F77F, "Alchemical Symbols"),
    UnicodeBlock(0x1F780, 0x1F7FF, "Geometric Shapes Extended"),
    UnicodeBlock(0x1F800, 0x1F8FF, "Supplemental Arrows-C"),
    UnicodeBlock(0x1F900, 0x1F9FF, "Supplemental Symbols and Pictographs"),
    UnicodeBlock(0x1FA00, 0x1FA6F, "Chess Symbols"),
    UnicodeBlock(0x1FA70, 0x1FAFF, "Symbols and Pictographs Extended-A"),
    UnicodeBlock(0x1FB00, 0x1FBFF, "Symbols for Legacy Computing"),
    UnicodeBlock(0x20000, 0x2A6DF, "CJK Unified Ideographs Extension B"),
    UnicodeBlock(0x2A700, 0x2B73F, "CJK Unified Ideographs Extension C"),
    UnicodeBlock(0x2B740, 0x2B81F, "CJK Unified Ideographs Extension D"),
    UnicodeBlock(0x2B820, 0x2CEAF, "CJK Unified Ideographs Extension E"),
    UnicodeBlock(0x2CEB0, 0x2EBEF, "CJK Unified Ideographs Extension F"),
    UnicodeBlock(0x2EBF0, 0x2EE5F, "CJK Unified Ideographs Extension I"),
    UnicodeBlock(0x2F800, 0x2FA1F, "CJK Compatibility Ideographs Supplement"),
    UnicodeBlock(0x30000, 0x3134F, "CJK Unified Ideographs Extension G"),
    UnicodeBlock(0x31350, 0x323AF, "CJK Unified Ideographs Extension H"),
    UnicodeBlock(0x323B0, 0x3347F, "CJK Unified Ideographs Extension J"),
    UnicodeBlock(0x3D000, 0x3FC3F, "Seal"),
    UnicodeBlock(0xE0000, 0xE007F, "Tags"),
    UnicodeBlock(0xE0100, 0xE01EF, "Variation Selectors Supplement"),
    UnicodeBlock(0xF0000, 0xFFFFF, "Supplementary Private Use Area-A"),
    UnicodeBlock(0x100000, 0x10FFFF, "Supplementary Private Use Area-B")
)


private val boards = listOf(
    Board("UNICODE BLOCKS", emptyList()),
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
    Board("CURRENCY", listOf("$","¢","£","¤","¥","֏","؋","৲","৳","૱","௹","฿","៛","₠","₡","₢","₣","₤","₥","₦","₧","₨","₩","₪","₫","€","₭","₮","₯","₰","₱","₲","₳","₴","₵","₶","₷","₸","₹","₺","₻","₼","₽","₾","₿")),
    Board("PUNCTUATION", listOf("¡","¿","‽","⁈","⁉","‼","¶","§","†","‡","•","‣","※","⁂","⁎","⁑","⁕","⁖","⁘","⁙","⁚","⁛","⁜","⁝","⁞","—","–","―","…","⋯","⋮","⋱","「","」","『","』","【","】","〔","〕","《","》","〈","〉","〝","〞","〟","〰")),
    Board("BOX-DRAWING", (0x2500..0x257F).map { String(Character.toChars(it)) } + (0x2580..0x259F).map { String(Character.toChars(it)) }),
    Board("BRAILLE", (0x2800..0x28FF).map { String(Character.toChars(it)) }),
    Board("MUSIC", listOf("♩","♪","♫","♬","♭","♮","♯","𝄞","𝄢","𝄪","𝄫","𝄀","𝄁","𝄂","𝄃","𝄄","𝄅","𝄆","𝄇","𝄈","𝄉","𝄊","𝄋","𝄌","𝄍","𝄎","𝄏")),
    Board("FOREIGN", listOf("Α","Β","Γ","Δ","Ε","Ζ","Η","Θ","Ι","Κ","Λ","Μ","Ν","Ξ","Ο","Π","Ρ","Σ","Τ","Υ","Φ","Χ","Ψ","Ω","α","β","γ","δ","ε","ζ","η","θ","ι","κ","λ","μ","ν","ξ","ο","π","ρ","σ","τ","υ","φ","χ","ψ","ω","А","Б","В","Г","Д","Е","Ё","Ж","З","И","Й","К","Л","М","Н","О","П","Р","С","Т","У","Ф","Х","Ц","Ч","Ш","Щ","Ъ","Ы","Ь","Э","Ю","Я")),
    Board("EMOJI-PEOPLE", "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺️ 😊 😇 🙂 🙃 😉 😌 😍 🥰 😘 😗 😙 😚 😋 😛 😝 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩 🥳 😏 😒 😞 😔 😟 😕 🙁 ☹️ 😣 😖 😫 😩 🥺 😢 😭 😤 😠 😡 🤬 🤯 😳 🥵 🥶 😱 😨 😰 😥 😓 🤗 🤔 🫣 🤭 🫢 🫡 🤫 🫠 🤥 😶 😶‍🌫️ 😐 😑 😬 🙄 😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 😵‍💫 🤐 🥴 🤢 🤮 🤧 😷 🤒 🤕 🤑 🤠 👻 💀 ☠️ 👽 👾 🤖 🎃".split(" ")),
    Board("EMOJI-NATURE", "🐶 🐱 🐭 🐹 🐰 🦊 🐻 🐼 🐻‍❄️ 🐨 🐯 🦁 🐮 🐷 🐸 🐵 🙈 🙉 🙊 🐒 🐔 🐧 🐦 🐤 🦆 🦅 🦉 🦇 🐺 🐗 🐴 🦄 🐝 🪱 🦋 🐌 🐞 🐜 🪰 🦟 🦗 🕷️ 🦂 🐢 🐍 🦎 🐙 🦑 🦀 🦞 🦐 🦪 🐠 🐟 🐡 🦈 🐬 🐳 🐋 🐊 🌵 🎄 🌲 🌳 🌴 🌱 🌿 ☘️ 🍀 🎍 🪴 🌵 🌾 🌷 🌹 🥀 🌺 🌸 🌼 🌻 🌞 🌝 🌛 🌜 🌚 🌕 🌖 🌗 🌘 🌑 🌒 🌓 🌔 🌙 ⭐ 🌟 ✨ ⚡ ☄️ 💥 🔥 🌪️ 🌈 ☀️ 🌤️ ⛅ 🌥️ ☁️ 🌦️ 🌧️ ⛈️ 🌩️ 🌨️ ❄️ ☃️ ⛄ 🌬️ 💨 💧 💦 ☔ 🌊".split(" ")),
    Board("EMOJI-FOOD", "🍏 🍎 🍐 🍊 🍋 🍌 🍉 🍇 🍓 🫐 🍈 🍒 🍑 🥭 🍍 🥥 🥝 🍅 🫒 🥑 🍆 🥔 🥕 🌽 🌶️ 🫑 🥒 🥬 🥦 🧄 🧅 🍄 🥜 🌰 🍞 🥐 🥖 🫓 🥨 🥯 🥞 🧇 🧀 🍖 🍗 🥩 🥓 🍔 🍟 🍕 🌭 🥪 🌮 🌯 🫔 🥙 🧆 🥚 🍳 🥘 🍲 🥣 🥗 🍿 🧈 🧂 🥫 🍱 🍘 🍙 🍚 🍛 🍜 🍝 🍠 🍢 🍣 🍤 🍥 🥮 🍡 🥟 🥠 🥡 🦪 🍦 🍧 🍨 🍩 🍪 🎂 🍰 🧁 🥧 🍫 🍬 🍭 🍮 🍯 🍼 🥛 ☕ 🫖 🍵 🧃 🥤 🧋 🍶 🍺 🍻 🥂 🍷 🥃 🍸 🍹 🧉".split(" ")),
    Board("EMOJI-TRAVEL", "🚗 🚕 🚙 🚌 🚎 🏎️ 🚓 🚑 🚒 🚐 🛻 🚚 🚛 🚜 🏍️ 🛵 🚲 🛴 🛹 🛼 🚏 🛣️ 🛤️ 🛢️ ⛽ 🛞 🚨 🚥 🚦 🛑 🚧 ⚓ 🛟 ⛵ 🚤 🛳️ ⛴️ 🛥️ 🚢 ✈️ 🛩️ 🛫 🛬 🪂 💺 🚁 🚟 🚠 🚡 🛰️ 🚀 🛸 🏖️ 🏝️ 🏜️ 🌋 ⛰️ 🏔️ 🗻 🏕️ ⛺ 🏠 🏡 🏢 🏣 🏤 🏥 🏦 🏨 🏩 🏪 🏫 🏬 🏭 🏯 🏰 💒 🗼 🗽 ⛪ 🕌 🛕 🕍 ⛩️ 🕋 ⛲ ⛺ 🌁 🌃 🌄 🌅 🌆 🌇 🌉 🎠 🎡 🎢".split(" ")),
    Board("EMOJI-ACTIVITY", "⚽ 🏀 🏈 ⚾ 🥎 🎾 🏐 🏉 🥏 🎱 🪀 🏓 🏸 🏒 🏑 🥍 🏏 🪃 🥅 ⛳ 🪁 🏹 🎣 🤿 🥊 🥋 🎽 🛹 🛼 🛷 ⛸️ 🥌 🎿 ⛷️ 🏂 🏋️ 🤼 🤸 ⛹️ 🤺 🏇 🧘 🏄 🏊 🤽 🚣 🧗 🚵 🚴 🏆 🥇 🥈 🥉 🏅 🎖️ 🏵️ 🎗️ 🎫 🎟️ 🎪 🤹 🎭 🩰 🎨 🎬 🎤 🎧 🎼 🎹 🥁 🪘 🎷 🎺 🎸 🪕 🎻 🪗 🎲 ♟️ 🎯 🎳 🎮 🕹️ 🧩".split(" ")),
    Board("EMOJI-OBJECTS", "⌚ 📱 📲 💻 ⌨️ 🖥️ 🖨️ 🖱️ 🖲️ 🕹️ 💽 💾 💿 📀 📼 📷 📸 📹 🎥 📽️ 🎞️ 📞 ☎️ 📟 📠 📺 📻 🎙️ 🎚️ 🎛️ 🧭 ⏱️ ⏲️ ⏰ 🕰️ ⌛ ⏳ 📡 🔋 🪫 🔌 💡 🔦 🕯️ 🪔 🧯 🛢️ 💸 💵 💴 💶 💷 💰 💳 🧾 💎 ⚖️ 🪜 🧰 🔧 🔨 ⚒️ 🛠️ ⛏️ 🪚 🔩 ⚙️ 🧱 ⛓️ 🧲 🔫 💣 🧨 🔪 🗡️ ⚔️ 🛡️ 🚬 ⚰️ 🪦 ⚱️ 🏺 🧿 🪬".split(" ")),
    Board("EMOJI-SYMBOLS", "❤️ 🧡 💛 💚 💙 💜 🖤 🤍 🤎 💔 ❣️ 💕 💞 💓 💗 💖 💘 💝 💟 ☮️ ✝️ ☪️ 🕉️ ☸️ ✡️ 🔯 🕎 ☯️ ☦️ 🛐 ⛎ ♈ ♉ ♊ ♋ ♌ ♍ ♎ ♏ ♐ ♑ ♒ ♓ 🆔 ⚛️ 🉑 ☢️ ☣️ 📴 📳 🈶 🈚 🈸 🈺 🈷️ ✴️ 🆚 💮 🉐 ㊙️ ㊗️ 🈴 🈵 🈹 🈲 🅰️ 🅱️ 🆎 🆑 🅾️ 🆘 ❌ ⭕ 🛑 ⛔ 📛 🚫 💯 💢 ♨️ 🚷 🚯 🚳 🚱 🔞 📵 🚭 ❗ ❕ ❓ ❔ ‼️ ⁉️ 🔅 🔆 🔱 ⚜️ 🔰 ♻️ ✅ ☑️ ✔️ ❎ ➕ ➖ ➗ ✖️ 💲 💱 ™️ ©️ ®️ 〰️ ➰ ➿ 🔚 🔙 🔛 🔝 🔜".split(" ")),
    Board("EMOJI-FLAGS", "🏳️ 🏴 🏁 🚩 🏳️‍🌈 🏳️‍⚧️ 🇮🇳 🇺🇸 🇬🇧 🇨🇦 🇦🇺 🇯🇵 🇨🇳 🇰🇷 🇫🇷 🇩🇪 🇮🇹 🇪🇸 🇧🇷 🇷🇺 🇵🇰 🇧🇩 🇳🇵 🇱🇰 🇦🇪 🇸🇦 🇿🇦 🇳🇿 🇲🇽 🇹🇷 🇮🇩 🇹🇭 🇻🇳 🇵🇭 🇺🇦 🇵🇸".split(" ")),
    Board("EMOJI", "😀 😁 😂 🤣 😊 😍 🥰 😘 😎 🤔 😭 😴 🔥 ✨ ⭐ ❤️ 💙 💜 💚 🖤 👍 👏 🙏 💯 🎉 🌹 ☀️ 🌙 ⚡ 🍀".split(" ")),
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
    var selectedBlock by remember { mutableIntStateOf(0) }
    val block = unicodeBlocks[selectedBlock.coerceIn(0, unicodeBlocks.lastIndex)]
    val chars = if (activeBoard.name == "UNICODE BLOCKS") (block.start..block.end).map { String(Character.toChars(it)) } else when (activeBoard.name) {
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
                    Text(if (selecting) "SELECTION MODE" else if (activeBoard.name == "UNICODE BLOCKS") "UNICODE BLOCKS · ${unicodeBlocks.size} CATEGORIES" else "CHARACTER CATEGORIES", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
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
                if (activeBoard.name == "UNICODE BLOCKS") {
                    Row(Modifier.fillMaxWidth().background(Color(0xFFE5E8F0)).horizontalScroll(rememberScrollState()).padding(horizontal = 6.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                        unicodeBlocks.forEachIndexed { index, item ->
                            FilterChip(selected = selectedBlock == index, onClick = { selectedBlock = index }, label = { Text(item.name, fontSize = 10.sp, maxLines = 1) }, modifier = Modifier.padding(end = 5.dp))
                        }
                    }
                    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(block.name, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("U+%04X – U+%04X".format(block.start, block.end), color = Muted, fontSize = 10.sp)
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
