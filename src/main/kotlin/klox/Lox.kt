package klox

import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: jlox [full path to script file]")
        exitProcess(64)
    } else if (args.size == 1) {
        Lox().runFile(args[0])
    } else {
        Lox().runPrompt()
    }
}

class Lox {
    private var hadError = false

    fun runPrompt() {
        while (true) {
            print("> ")
            val sourceCode = readLine() ?: break
            runSource(sourceCode)
            hadError = false
        }
    }

    fun runFile(filename: String) {
        val file = File(filename)
        if (!file.exists()) {
            System.err.println("There is no such file: $filename")
            exitProcess(66)
        }
        val source = file.readText(StandardCharsets.UTF_8)
        runSource(source)
        if (hadError) exitProcess(65)
    }

    private fun runSource(source: String) {
        val (tokens, errors) = Scanner(source).scanTokens()
        if (errors.isNotEmpty()) {
            errors.forEach { scannerError -> error(scannerError.line, scannerError.message) }
            hadError = true
            return
        }
        for (token in tokens) println(token)
    }

    private fun error(line: Int, message: String, where: String = "") {
        System.err.println("[line $line] Error$where: $message")
        hadError = true
    }
}
