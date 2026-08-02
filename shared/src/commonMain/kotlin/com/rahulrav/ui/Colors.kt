package com.rahulrav.ui

import androidx.compose.ui.graphics.Color
import com.rahulrav.diff.State
import com.rahulrav.parser.Token

// Colors used by IntelliJ Island Dark Palette
val Background = Color(0xFF1E1F22)  // Canvas / Surface
val Foreground = Color(0xFFBCBEC4)  // Default text / Parameters / Operators
val Keyword = Color(0xFFCF8E6D)  // 'fun', 'val', 'import', 'class', etc.
val String = Color(0xFF6AAB73)  // "string literals"
val Comment = Color(0xFF7A7E85)  // // comments
val Number = Color(0xFF2AACB8)  // 123, 0xFF, etc.
val Constant = Color(0xFFCF8E6D)  // true, false, null (IntelliJ uses Keyword color)
val Function = Color(0xFF56A8F5)  // function Calls and definitions
val Type = Color(0xFFC77DBB)  // Classes, Interfaces, Generics
val Property = Color(0xFFC77DBB)  // Member properties / fields
val Annotation = Color(0xFFB3AE60)  // @Composable, @Preview
val Punctuation = Color(0xFFA3A6AD)  // Brackets { }, Parens ( ), Commas, Dots
val Invalid = Color(0xFFFA6675)  // Syntax errors

fun Token.color(): Color {
    return when {
        scope.contains("annotation") -> Annotation
        scope.contains("attribute") -> Annotation
        scope.contains("keyword") -> Keyword
        scope.contains("storage") -> Keyword
        scope.contains("string") -> String
        scope.contains("comment") -> Comment
        scope.contains("constant.language") -> Constant
        scope.contains("constants.numeric") -> Number
        scope.contains("constant") -> Number
        scope.contains("function") -> Function
        scope.contains("entity.name.type") -> Type
        scope.contains("class") -> Type
        scope.contains("property") -> Property
        scope.contains("punctuation") -> Punctuation
        scope.contains("invalid") -> Invalid
        // Fallback to foreground color
        else -> Foreground
    }
}

fun State.color(): Color {
    return when (this) {
        // Should never really happen
        is State.Empty -> Foreground
        is State.Match -> current.color()
        is State.Insert -> token.color()
        is State.Delete -> token.color()
    }
}
