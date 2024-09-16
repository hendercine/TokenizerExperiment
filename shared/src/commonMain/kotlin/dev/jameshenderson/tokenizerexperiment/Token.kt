package dev.jameshenderson.tokenizerexperiment

data class Token(
    val type: TokenType? = null,
    val value: String? = null,
    val line: Int? = null,
    val column: Int? = null,
)
