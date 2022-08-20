package klox

fun printAst(expr: Expr) {
    val builder = StringBuilder()
    astToString(expr, builder, "")
    println(builder)
}

private fun astToString(expr: Expr, builder: StringBuilder, prefix: String) {
    when (expr) {
        is Literal -> builder
            .appendLine(expr.value)
        is Unary -> {
            builder
                .appendLine(expr.operator.lexeme)
                .append("$prefix└── ")
            astToString(expr.right, builder, "$prefix├── ")
        }
        is Binary -> {
            builder
                .appendLine(expr.operator.lexeme)
                .append("$prefix├── ")
            astToString(expr.left, builder, "$prefix|   ")
            builder
                .append("$prefix└── ")
            astToString(expr.right, builder, "$prefix    ")
        }
        is Grouping -> {
            builder
                .appendLine("()")
                .append("$prefix└── ")
            astToString(expr.expr, builder, "$prefix    ")
        }
    }
}

fun main(args: Array<String>) {
    val expr = Binary(
        Unary(
            Token(TokenType.MINUS, 1, "-"),
            Literal(123)
        ),
        Token(TokenType.STAR, 1, "*"),
        Binary(
            Grouping(
                Literal(45.67)
            ),
            Token(TokenType.PLUS, 1, "+"),
            Literal(45.67)
        )
    )
    printAst(expr)
}