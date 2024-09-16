package dev.jameshenderson.tokenizerexperiment

import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
    private var input = ""

    @Test
    fun `test tag start and end`()  {
            input = "{% some_tag %}"

            val expectedTokens =
                listOf(
                    Token(TokenType.TAG_START, "{%", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 3),
                    Token(TokenType.IDENTIFIER, "some_tag", 1, 4),
                    Token(TokenType.WHITESPACE, " ", 1, 12),
                    Token(TokenType.TAG_END, "%}", 1, 13),
                )
            val actualTokens = getActualTokens(input)

            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test output start and end`() {
            input = "{{ some_variable }}"

            val expectedTokens =
                listOf(
                    Token(TokenType.OUTPUT_START, "{{", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 3),
                    Token(TokenType.IDENTIFIER, "some_variable", 1, 4),
                    Token(TokenType.WHITESPACE, " ", 1, 17),
                    Token(TokenType.OUTPUT_END, "}}", 1, 18),
                )

            val actualTokens = getActualTokens(input)

            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test string literals`() {
            val input = "\"Hello, world!\" 'Single quotes'"

            val expectedTokens =
                listOf(
                    Token(TokenType.STRING, "Hello, world!", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 16),
                    Token(TokenType.STRING, "Single quotes", 1, 17),
                )

            val actualTokens = getActualTokens(input)
            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test number literals`() {
            val input = "123 45.67 -89"

            val expectedTokens =
                listOf(
                    Token(TokenType.NUMBER, "123", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 4),
                    Token(TokenType.NUMBER, "45.67", 1, 5),
                    Token(TokenType.WHITESPACE, " ", 1, 10),
                    Token(TokenType.MINUS, "-", 1, 11), // Handle minus separately
                    Token(TokenType.NUMBER, "89", 1, 12),
                )

            val actualTokens = getActualTokens(input)
            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test identifiers and keywords`() {
            val input = "product.title if else true false null"

            val expectedTokens =
                listOf(
                    Token(TokenType.IDENTIFIER, "product", 1, 1),
                    Token(TokenType.DOT, ".", 1, 8),
                    Token(TokenType.IDENTIFIER, "title", 1, 9),
                    Token(TokenType.WHITESPACE, " ", 1, 14),
                    Token(TokenType.TAG_IDENTIFIER, "if", 1, 15),
                    Token(TokenType.WHITESPACE, " ", 1, 17),
                    Token(TokenType.TAG_IDENTIFIER, "else", 1, 18),
                    Token(TokenType.WHITESPACE, " ", 1, 22),
                    Token(TokenType.BOOLEAN_TRUE, "true", 1, 23),
                    Token(TokenType.WHITESPACE, " ", 1, 27),
                    Token(TokenType.BOOLEAN_FALSE, "false", 1, 28),
                    Token(TokenType.WHITESPACE, " ", 1, 33),
                    Token(TokenType.NULL, "null", 1, 34),
                )

            val actualTokens = getActualTokens(input)
            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test operators and punctuation`() {
            val input = "+ - * / % == != > >= < <= ., : | ( ) [ ]"

            val expected =
                listOf(
                    Token(TokenType.PLUS, "+", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 2),
                    Token(TokenType.MINUS, "-", 1, 3),
                    Token(TokenType.WHITESPACE, " ", 1, 4),
                    Token(TokenType.MULTIPLY, "*", 1, 5),
                    Token(TokenType.WHITESPACE, " ", 1, 6),
                    Token(TokenType.DIVIDE, "/", 1, 7),
                    Token(TokenType.WHITESPACE, " ", 1, 8),
                    Token(TokenType.MODULO, "%", 1, 9),
                    Token(TokenType.WHITESPACE, " ", 1, 10),
                    Token(TokenType.EQUAL, "==", 1, 11),
                    Token(TokenType.WHITESPACE, " ", 1, 13),
                    Token(TokenType.NOT_EQUAL, "!=", 1, 14),
                    Token(TokenType.WHITESPACE, " ", 1, 16),
                    Token(TokenType.GREATER_THAN, ">", 1, 17),
                    Token(TokenType.WHITESPACE, " ", 1, 18),
                    Token(TokenType.GREATER_THAN_OR_EQUAL, ">=", 1, 19),
                    Token(TokenType.WHITESPACE, " ", 1, 21),
                    Token(TokenType.LESS_THAN, "<", 1, 22),
                    Token(TokenType.WHITESPACE, " ", 1, 23),
                    Token(TokenType.LESS_THAN_OR_EQUAL, "<=", 1, 24),
                    Token(TokenType.WHITESPACE, " ", 1, 26),
                    Token(TokenType.DOT, ".", 1, 27),
                    Token(TokenType.COMMA, ",", 1, 28),
                    Token(TokenType.WHITESPACE, " ", 1, 29),
                    Token(TokenType.COLON, ":", 1, 30),
                    Token(TokenType.WHITESPACE, " ", 1, 31),
                    Token(TokenType.PIPE, "|", 1, 32),
                    Token(TokenType.WHITESPACE, " ", 1, 33),
                    Token(TokenType.LEFT_PAREN, "(", 1, 34),
                    Token(TokenType.WHITESPACE, " ", 1, 35),
                    Token(TokenType.RIGHT_PAREN, ")", 1, 36),
                    Token(TokenType.WHITESPACE, " ", 1, 37),
                    Token(TokenType.LEFT_SQUARE, "[", 1, 38),
                    Token(TokenType.WHITESPACE, " ", 1, 39),
                    Token(TokenType.RIGHT_SQUARE, "]", 1, 40),
                )

            val actual = getActualTokens(input)

            assertEquals(expected, actual)
        }

    @Test
    fun `test comments`() {
            val input =
                """
                {% comment %}
                  Renders an article card for a given blog with settings to either show the image or not.

                {% endcomment %}
                {% product.title %}
                """.trimIndent()

            // Expect only the start and end tag tokens, as the comment should be ignored
            val expectedTokens =
                listOf(
                    Token(
                        TokenType.WHITESPACE,
                        value = "\n",
                        line = 4,
                        column = 17,
                    ),
                    Token(
                        TokenType.TAG_START,
                        value = "{%",
                        line = 5,
                        column = 1,
                    ),
                    Token(
                        TokenType.WHITESPACE,
                        value = " ",
                        line = 5,
                        column = 3,
                    ),
                    Token(
                        TokenType.IDENTIFIER,
                        "product",
                        line = 5,
                        column = 4,
                    ),
                    Token(
                        TokenType.DOT,
                        ".",
                        line = 5,
                        column = 11,
                    ),
                    Token(
                        TokenType.IDENTIFIER,
                        "title",
                        line = 5,
                        column = 12,
                    ),
                    Token(
                        TokenType.WHITESPACE,
                        " ",
                        line = 5,
                        column = 17,
                    ),
                    Token(
                        TokenType.TAG_END,
                        "%}",
                        line = 5,
                        column = 18,
                    ),
                )

            val actualTokens = getActualTokens(input)
            assertEquals(expectedTokens, actualTokens)
        }

    @Test
    fun `test whitespace and newlines`() {
            val input = "  \n \t  {% tag %}\n"
            // ... (Define expected tokens for whitespace and newline characters)
            val expected =
                listOf<Token>(
                    Token(TokenType.WHITESPACE, "  \n \t  ", 1, 1),
                    Token(TokenType.TAG_START, "{%", 2, 5),
                    Token(TokenType.WHITESPACE, " ", 2, 7),
                    Token(TokenType.IDENTIFIER, "tag", 2, 8),
                    Token(TokenType.WHITESPACE, " ", 2, 11),
                    Token(TokenType.TAG_END, "%}", 2, 12),
                    Token(TokenType.WHITESPACE, "\n", 2, 14),
                )

            val actual = getActualTokens(input)

            assertEquals(expected, actual)
        }

    @Test
    fun `test complex expression`() {
            val input = "{% if (price > 100 and quantity< 5) or discount_code == 'SUMMER20' %}"

            val expected =
                listOf(
                    Token(TokenType.TAG_START, "{%", 1, 1),
                    Token(TokenType.WHITESPACE, " ", 1, 3),
                    Token(TokenType.TAG_IDENTIFIER, "if", 1, 4),
                    Token(TokenType.WHITESPACE, " ", 1, 6),
                    Token(TokenType.LEFT_PAREN, "(", 1, 7),
                    Token(TokenType.IDENTIFIER, "price", 1, 8),
                    Token(TokenType.WHITESPACE, " ", 1, 13),
                    Token(TokenType.GREATER_THAN, ">", 1, 14),
                    Token(TokenType.WHITESPACE, " ", 1, 15),
                    Token(TokenType.NUMBER, "100", 1, 16),
                    Token(TokenType.WHITESPACE, " ", 1, 19),
                    Token(TokenType.AND, "and", 1, 20),
                    Token(TokenType.WHITESPACE, " ", 1, 23),
                    Token(TokenType.IDENTIFIER, "quantity", 1, 24),
                    Token(TokenType.LESS_THAN, "<", 1, 32),
                    Token(TokenType.WHITESPACE, " ", 1, 33),
                    Token(TokenType.NUMBER, "5", 1, 34),
                    Token(TokenType.RIGHT_PAREN, ")", 1, 35),
                    Token(TokenType.WHITESPACE, " ", 1, 36),
                    Token(TokenType.OR, "or", 1, 37),
                    Token(TokenType.WHITESPACE, " ", 1, 39),
                    Token(TokenType.IDENTIFIER, "discount_code", 1, 40),
                    Token(TokenType.WHITESPACE, " ", 1, 53),
                    Token(TokenType.EQUAL, "==", 1, 54),
                    Token(TokenType.WHITESPACE, " ", 1, 56),
                    Token(TokenType.STRING, "SUMMER20", 1, 57),
                    // add 10 to column to account for string escape chars '
                    Token(TokenType.WHITESPACE, " ", 1, 67),
                    Token(TokenType.TAG_END, "%}", 1, 68),
                )
            val actual = getActualTokens(input)

            assertEquals(expected, actual)
        }

    @Test
    fun `test escaped characters`() {
            input = "\"This is a quote: \\\"\""
            val actual = getActualTokens(input)
            val expected =
                listOf(
                    Token(
                        TokenType.STRING,
                        "This is a quote: \"",
                        1,
                        1,
                    ),
                )
            assertEquals(expected, actual)
        }

    @Test
    fun `test empty string`() {
            input = "\"\""
            val actual = getActualTokens(input)
            val expected =
                listOf(
                    Token(
                        TokenType.STRING,
                        "",
                        1,
                        1,
                    ),
                )
            assertEquals(expected, actual)
        }

    @Test
    fun `test leading zeros`() {
            input = "007"
            val actual = getActualTokens(input)
            val expected =
                listOf(
                    Token(TokenType.NUMBER, "007", 1, 1),
                )
            assertEquals(expected, actual)
        }

    private fun getActualTokens(input: String): List<Token> {
        val newTokenizer = Tokenizer(input)
        return generateSequence(newTokenizer.nextToken()) { newTokenizer.nextToken() }.toList()
    }
}
