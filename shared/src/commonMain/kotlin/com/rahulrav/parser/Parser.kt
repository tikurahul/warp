package com.rahulrav.parser

/**
 * Parses the source [code] using `TextMate grammar` to obtain a list of tokens.
 */
expect fun parseKotlin(code: String): List<Token>
