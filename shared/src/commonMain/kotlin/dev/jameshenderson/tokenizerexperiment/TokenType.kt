package dev.jameshenderson.tokenizerexperiment

enum class TokenType {
    // Tags
    TAG_START, // {%
    TAG_END, // %}
    TAG_IDENTIFIER, // e.g., if, for, assign, include

    // Output
    OUTPUT_START, // {{
    OUTPUT_END, // }}// Literals
    STRING, // "hello world" or 'single quotes'
    NUMBER, // 123, 4.56
    BOOLEAN_TRUE, // true
    BOOLEAN_FALSE, // false
    NULL, // null

    // Identifiers & Variables
    IDENTIFIER, // product, collection, etc.

    // Operators
    PLUS, // +
    MINUS, // -
    MULTIPLY, // *
    DIVIDE, // /
    MODULO, // %
    EQUAL, // ==
    NOT_EQUAL, // !=
    GREATER_THAN, // >
    LESS_THAN, // <
    GREATER_THAN_OR_EQUAL, // >=
    LESS_THAN_OR_EQUAL, // <=
    AND, // and
    OR, // or
    NOT, // not
    CONTAINS, // contains

    // Punctuation
    DOT, // .
    COMMA, // ,
    COLON, // :
    PIPE, // | (for filters)
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    LEFT_SQUARE, // [
    RIGHT_SQUARE, // ]

    // Whitespace & Comments
    WHITESPACE, // Spaces, tabs, newlines
    COMMENT, // {# ... #}

    // Special
    EOF, // End of file
}
