package klox

class Parser(val tokens: List<Token>) {

    val errors = mutableListOf<ParserError>()
    var current = 0

    fun parse() = try {
        ParserResult(expr = expression())
    } catch (ex: ParserException) {
        errors.add(ParserError(ex.token, ex.message ?: "Unknown error"))
        ParserResult(errors = errors)
    }

    private fun expression() = equality()

    private fun parseLeftBinary(operandParser: () -> Expr, vararg types: TokenType): Expr {
        var expr = operandParser()
        while (match(*types)) {
            val operator = previous()
            val right = operandParser()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun equality() = parseLeftBinary(
        ::comparison,
        TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL
    )

    private fun comparison() = parseLeftBinary(
        ::term,
        TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL
    )

    private fun term() = parseLeftBinary(
        ::factor,
        TokenType.MINUS, TokenType.PLUS
    )

    private fun factor() = parseLeftBinary(
        ::unary,
        TokenType.SLASH, TokenType.STAR
    )

    private fun unary(): Expr {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            val operator = previous()
            val right = unary()
            return Unary(operator, right)
        }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.FALSE)) return Literal(false)
        if (match(TokenType.TRUE)) return Literal(true)
        if (match(TokenType.NIL)) return Literal(null)

        if (match(TokenType.NUMBER, TokenType.STRING)) return Literal(previous().literal)

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression")
            return Grouping(expr)
        }
        throw ParserException(peek(), "Expect expression")
    }

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return
            if (peek().type in statementStart) return
            advance()
        }
    }

    private fun consume(type: TokenType, message: String) {
        if (check(type)) advance() else throw ParserException(peek(), message)
    }

    private fun error(token: Token, message: String) {
        errors.add(ParserError(token, message))
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType) = !isAtEnd() && peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == TokenType.EOF

    private fun peek() = tokens[current]

    private fun previous() = tokens[current - 1]

    companion object {
        val statementStart = listOf(
            TokenType.CLASS,
            TokenType.FUN,
            TokenType.VAR,
            TokenType.FOR,
            TokenType.IF,
            TokenType.WHILE,
            TokenType.PRINT,
            TokenType.RETURN
        )
    }
}

data class ParserError(val token: Token, val message: String)
class ParserException(val token: Token, message: String) : Exception(message)
data class ParserResult(val expr: Expr? = null, val errors: List<ParserError> = emptyList())