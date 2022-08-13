package klox

class Scanner(private val source: String) {

    val errors: MutableList<ScannerError> = mutableListOf()

    private val tokens: MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens(): ScanningResult {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, line))
        return ScanningResult(tokens, errors)
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> if (match('=')) addToken(TokenType.BANG_EQUAL) else addToken(TokenType.BANG)
            '=' -> if (match('=')) addToken(TokenType.EQUAL_EQUAL) else addToken(TokenType.EQUAL)
            '<' -> if (match('=')) addToken(TokenType.LESS_EQUAL) else addToken(TokenType.LESS)
            '>' -> if (match('=')) addToken(TokenType.GREATER_EQUAL) else addToken(TokenType.GREATER)
            '/' ->
                if (match('/'))
                    while (peek() !in listOf(NULL_TERMINATOR, '\n')) advance()
                else if (match('*')) {
                    blockComment()
                } else addToken(TokenType.SLASH)
            ' ', '\r', '\t' -> {}
            '\n' -> line++
            '"' -> string()
            else -> {
                when {
                    c.isDigit() -> number()
                    c.isAlpha() -> identifier()
                    else -> errors.add(ScannerError(line, "Unexpected character $c"))
                }
            }
        }
    }

    private fun blockComment() {
        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                current += 2
                return
            }
            if (peek() == '\n') line++
            advance()
        }
        errors.add(ScannerError(line, "Unclosed block comment"))
    }

    private fun identifier() {
        while (peek().isAlphaNumeric()) advance()
        val lexeme = source.substring(start, current)
        val tokenType: TokenType = if (lexeme in keywords) keywords.getValue(lexeme) else TokenType.IDENTIFIER
        addToken(tokenType)
    }

    private fun number() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            errors.add(ScannerError(line, "Unterminated string"))
        }
        advance()
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun peek() = if (isAtEnd()) NULL_TERMINATOR else source[current]

    private fun peekNext() = if (current + 1 >= source.length) NULL_TERMINATOR else source[current + 1]

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, line, text, literal))
    }

    private fun advance() = source[current++]

    private fun isAtEnd() = current >= source.length
}

private fun Char.isDigit() = this in '0'..'9'

private fun Char.isAlpha() = this in 'a'..'z' || this in 'A'..'Z' || this == '_'

private fun Char.isAlphaNumeric() = isAlpha() || isDigit()

data class Token(
    val type: TokenType,
    val line: Int,
    val lexeme: String = "",
    val literal: Any? = null,
) {
    override fun toString() = "$type $lexeme ${literal ?: ""}"
}

data class ScanningResult(val tokens: List<Token>, val errors: List<ScannerError>)

enum class TokenType {
    // Single-character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    EOF
}

data class ScannerError(val line: Int, val message: String)

private const val NULL_TERMINATOR: Char = '\u0000'

private val keywords = mapOf(
    "and" to TokenType.AND,
    "class" to TokenType.CLASS,
    "else" to TokenType.ELSE,
    "false" to TokenType.FALSE,
    "for" to TokenType.FOR,
    "fun" to TokenType.FUN,
    "if" to TokenType.IF,
    "nil" to TokenType.NIL,
    "or" to TokenType.OR,
    "print" to TokenType.PRINT,
    "return" to TokenType.RETURN,
    "super" to TokenType.SUPER,
    "this" to TokenType.THIS,
    "true" to TokenType.TRUE,
    "var" to TokenType.VAR,
    "while" to TokenType.WHILE
)