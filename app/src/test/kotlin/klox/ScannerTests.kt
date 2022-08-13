package klox

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScannerTests : FunSpec({

    test("Should ignore block comment when surrounded by other tokens") {
        Scanner("class /* abc */ this").scanTokens().tokens shouldBe listOf(
            Token(TokenType.CLASS, 1, "class"),
            Token(TokenType.THIS, 1, "this"),
            Token(TokenType.EOF, 1)
        )
    }

    test("Should not crash when at the end of a file") {
        Scanner("/* */").scanTokens().tokens shouldBe listOf(
            Token(TokenType.EOF, 1)
        )
    }

    test("Should return error if not closed") {
        Scanner("/*").scanTokens().errors shouldBe listOf(
            ScannerError(1, "Unclosed block comment")
        )
    }

    test("Should return error if not closed properly") {
        Scanner("/* *").scanTokens().errors shouldBe listOf(
            ScannerError(1, "Unclosed block comment")
        )
    }

    test("Should increase current line number") {
        val source = """
            /*
            */
            """.trimIndent()
        Scanner(source).scanTokens().tokens shouldBe listOf(
            Token(TokenType.EOF, 2)
        )
    }
})