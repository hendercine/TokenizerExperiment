package dev.jameshenderson.tokenizerexperiment

import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.AND
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.BACK_SLASH
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.COLON
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.COMMA
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.COMMENT_TAG
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.CONTAINS
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.DIVIDE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.DOT
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.DOUBLE_EQUAL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.DOUBLE_QUOTE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.ELSE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.END_COMMENT_TAG
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.FALSE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.GREATER_THAN
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.GREATER_THAN_OR_EQUAL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.HASH
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.HORIZONTAL_TAB
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.IF
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LEFT_CURLY
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LEFT_PAREN
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LEFT_SQUARE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LESS_THAN
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LESS_THAN_OR_EQUAL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LOWERCASE_A
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LOWERCASE_N
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LOWERCASE_T
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.LOWERCASE_Z
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.MINUS
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.MODULO
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.MULTIPLY
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.NEW_LINE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.NINE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.NOT
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.NOT_EQUAL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.NULL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.OR
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.OUTPUT_END
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.OUTPUT_START
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.PIPE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.PLUS
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.RIGHT_CURLY
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.RIGHT_PAREN
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.RIGHT_SQUARE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.SINGLE_EQUAL
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.SINGLE_QUOTE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.TAG_END
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.TAG_START
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.TRUE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.UNDERSCORE
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.UPPERCASE_A
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.UPPERCASE_Z
import dev.jameshenderson.tokenizerexperiment.TokenizerConstants.ZERO

class Tokenizer(
    private val input: String,
) {
    private var position = 0
    private var line = 1
    private var column = 1

    fun nextToken(): Token? {
        // skip whitespace
        if (position < input.length && input[position].isWhitespace()) {
            return tokenizeWhiteSpace()
        }
        // handle recursion if at the start of a new line
        if (position < input.length && input[position] == NEW_LINE) {
            line++
            column = 1
        }

        if (position >= input.length) return null // end of input

        val startLine = line
        val startColumn = column

        return when {
            input.startsWith(COMMENT_TAG, position) -> {
                position += 12
                column += 12
                skipComment()
            }

            input[position] == LEFT_CURLY -> {
                if (position + 1 < input.length) {
                    when (input[position + 1]) {
                        MODULO -> {
                            if (input.startsWith(COMMENT_TAG, position)) { // Check for
                                // comment start
                                position += 12
                                column += 12
                                skipComment()
                            } else { // Handle regular tag start
                                position += 2
                                column += 2
                                Token(
                                    TokenType.TAG_START,
                                    TAG_START,
                                    startLine,
                                    startColumn,
                                )
                            }
                        }

                        LEFT_CURLY -> { // Output start
                            position += 2
                            column += 2
                            Token(
                                TokenType.OUTPUT_START,
                                OUTPUT_START,
                                startLine,
                                startColumn,
                            )
                        }

                        HASH -> { // Comment start
                            position += 2
                            column += 2
                            skipComment()
                        }

                        else -> {
                            // Handle error: Invalid token starting with '{'
                            null
                        }
                    }
                } else {
                    // Handle error: Unexpected end of input after '{'
                    null
                }
            }

            input[position] == RIGHT_CURLY -> {
                if (position + 1 < input.length) {
                    if (position + 1 < input.length && input[position + 1] == RIGHT_CURLY) { // Check for
                        // OUTPUT_END
                        position += 2
                        column += 2
                        Token(
                            TokenType.OUTPUT_END,
                            OUTPUT_END,
                            startLine,
                            startColumn,
                        )
                    } else {
                        // Handle error: Invalid token starting with '}' (or handle other contexts)
                        null
                    }
                } else {
                    // Handle error: Unexpected end of input after '}'
                    null
                }
            }

            input[position] in listOf(BACK_SLASH, SINGLE_QUOTE, DOUBLE_QUOTE) -> {
                val startChar = input[position]
                position++
                column++
                if (startChar == BACK_SLASH) {
                    val stringBuilder = StringBuilder()
                    when (input[position]) {
                        BACK_SLASH -> stringBuilder.append(BACK_SLASH)
                        SINGLE_QUOTE -> stringBuilder.append(SINGLE_QUOTE)
                        DOUBLE_QUOTE -> stringBuilder.append(DOUBLE_QUOTE)
                    }
                    position++
                    column++
                    return Token(
                        TokenType.STRING,
                        stringBuilder.toString(),
                        startLine,
                        startColumn,
                    )
                }
                val stringBuilder = StringBuilder()
                while (position < input.length && input[position] != startChar) {
                    if (input[position] == BACK_SLASH) {
                        position++
                        column++

                        val escapedChar =
                            when (input[position]) {
                                LOWERCASE_N -> NEW_LINE
                                LOWERCASE_T -> HORIZONTAL_TAB
                                BACK_SLASH -> BACK_SLASH
                                SINGLE_QUOTE -> SINGLE_QUOTE
                                DOUBLE_QUOTE -> DOUBLE_QUOTE
                                else -> input[position]
                            }
                        stringBuilder.append(escapedChar)
                    } else {
                        stringBuilder.append(input[position])
                    }
                    position++
                    column++
                }
                if (position < input.length && input[position] == startChar) {
                    position++
                    column++
                    Token(
                        type = TokenType.STRING,
                        value = stringBuilder.toString(),
                        line = startLine,
                        column = startColumn,
                    )
                } else {
                    // Handle error: Unclosed string literal
                    null
                }
            }

            // Numbers
            input[position] in ZERO..NINE -> {
                val stringBuilder = StringBuilder()
                while (position < input.length && (input[position].isDigit() || input[position] == DOT)) {
                    stringBuilder.append(input[position])
                    position++
                    column++
                }
                Token(
                    type = TokenType.NUMBER,
                    value = stringBuilder.toString(),
                    line = startLine,
                    column = startColumn,
                )
            }
            // Identifiers and keywords - lowercase
            input[position] in LOWERCASE_A..LOWERCASE_Z -> {
                val stringBuilder = StringBuilder()
                while (position < input.length &&
                    (
                        input[position].isLetterOrDigit() ||
                            input[position] == UNDERSCORE
                    )
                ) {
                    stringBuilder.append(input[position])
                    position++
                    column++
                }
                val identifier = stringBuilder.toString()
                val tokenType = getTokenType(identifier)
                Token(tokenType, identifier, startLine, startColumn)
            }
            // Identifiers and keywords - uppercase
            input[position] in UPPERCASE_A..UPPERCASE_Z -> {
                val stringBuilder = StringBuilder()
                while (position < input.length &&
                    (
                        input[position].isLetterOrDigit() ||
                            input[position] == UNDERSCORE
                    )
                ) {
                    stringBuilder.append(input[position])
                    position++
                    column++
                }

                val identifier = stringBuilder.toString()
                val tokenType = getTokenType(identifier)
                Token(tokenType, identifier, startLine, startColumn)
            }
            // Identifiers and keywords - underscore
            input[position] == UNDERSCORE -> {
                val stringBuilder = StringBuilder()
                while (position < input.length &&
                    (
                        input[position].isLetterOrDigit() ||
                            input[position] == UNDERSCORE
                    )
                ) {
                    stringBuilder.append(input[position])
                    position++
                    column++
                }
                val identifier = stringBuilder.toString()
                val tokenType = getTokenType(identifier)
                Token(tokenType, identifier, startLine, startColumn)
            }
            input[position] == MODULO -> {
                if (position + 1 < input.length && input[position + 1] == RIGHT_CURLY) { // Check for TAG_END
                    position += 2
                    column += 2
                    Token(TokenType.TAG_END, TAG_END, startLine, startColumn)
                } else {
                    position++
                    column++
                    Token(
                        TokenType.MODULO,
                        MODULO.toString(),
                        startLine,
                        startColumn,
                    ) // Handle as MODULO operator
                }
            }

            input[position] == GREATER_THAN -> {
                if (position < input.length && input[position + 1] == SINGLE_EQUAL) {
                    position += 2
                    column += 2
                    Token(TokenType.GREATER_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL, startLine, startColumn)
                } else {
                    position++
                    column++
                    Token(TokenType.GREATER_THAN, GREATER_THAN.toString(), startLine, startColumn)
                }
            }

            input[position] == LESS_THAN -> {
                if (position < input.length && input[position + 1] == SINGLE_EQUAL) {
                    position += 2
                    column += 2
                    Token(TokenType.LESS_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, startLine, startColumn)
                } else {
                    position++
                    column++
                    Token(TokenType.LESS_THAN, LESS_THAN.toString(), startLine, startColumn)
                }
            }

            // Check for tokens in the lookup table
            tokenLookup.any { (tokenValue, tokenType) ->
                if (input.startsWith(tokenValue, position)) {
                    position += tokenValue.length
                    column += tokenValue.length
                    return@nextToken Token(tokenType, tokenValue, startLine, startColumn)
                } else {
                    false
                }
            } -> {
                // Handle error: Unrecognized character
                val invalidChar = input[position]
                val errorMessage = "Invalid character '$invalidChar' at line $line, column $column"
                error(errorMessage)
            }

            else -> null
        }
    }

    private fun tokenizeWhiteSpace(): Token {
        val startLine = line
        val startColumn = column
        val stringBuilder = StringBuilder()
        while (position < input.length && input[position].isWhitespace()) {
            stringBuilder.append(input[position])
            if (input[position] == NEW_LINE) {
                line++
                column = 1
            } else {
                column++
            }
            position++
        }
        return Token(
            type = TokenType.WHITESPACE,
            value = stringBuilder.toString(),
            line = startLine,
            column = startColumn,
        )
    }

    private fun getTokenType(identifier: String) =
        when (identifier) {
            TRUE -> TokenType.BOOLEAN_TRUE
            FALSE -> TokenType.BOOLEAN_FALSE
            NULL -> TokenType.NULL
            IF -> TokenType.TAG_IDENTIFIER
            ELSE -> TokenType.TAG_IDENTIFIER
            OR -> TokenType.OR
            AND -> TokenType.AND
            NOT -> TokenType.NOT
            CONTAINS -> TokenType.CONTAINS
            else -> TokenType.IDENTIFIER
        }

    private fun skipComment(): Token? {
        while (position < input.length && !input.startsWith(END_COMMENT_TAG, position)) {
            if (input[position] == NEW_LINE) {
                line++
                column = 1
            } else {
                column++
            }
            position++
        }
        // Consume '{% endcomment %}' if found - ALWAYS, not just if there are 15 chars left
        if (input.startsWith(END_COMMENT_TAG, position)) {
            position += 16
            column += 16
        }
        return nextToken()
    }

    companion object {
        private val tokenLookup =
            mapOf(
                PLUS.toString() to TokenType.PLUS,
                MINUS.toString() to TokenType.MINUS,
                MULTIPLY.toString() to TokenType.MULTIPLY,
                DIVIDE.toString() to TokenType.DIVIDE,
                DOUBLE_EQUAL to TokenType.EQUAL,
                NOT_EQUAL to TokenType.NOT_EQUAL,
                DOT.toString() to TokenType.DOT,
                COMMA.toString() to TokenType.COMMA,
                COLON.toString() to TokenType.COLON,
                PIPE.toString() to TokenType.PIPE,
                LEFT_PAREN.toString() to TokenType.LEFT_PAREN,
                RIGHT_PAREN.toString() to TokenType.RIGHT_PAREN,
                LEFT_SQUARE.toString() to TokenType.LEFT_SQUARE,
                RIGHT_SQUARE.toString() to TokenType.RIGHT_SQUARE,
                IF to TokenType.TAG_IDENTIFIER,
                ELSE to TokenType.TAG_IDENTIFIER,
                TRUE to TokenType.BOOLEAN_TRUE,
                FALSE to TokenType.BOOLEAN_FALSE,
                NULL to TokenType.NULL,
                AND to TokenType.AND,
                OR to TokenType.OR,
                NOT to TokenType.NOT,
                CONTAINS to TokenType.CONTAINS,
            )
    }
}
