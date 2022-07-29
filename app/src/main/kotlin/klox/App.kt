package klox

import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) {
        println("Usage: jlox [script]")
        exitProcess(64)
    } else if (args.size == 1) {
         runFile(args[0])
    } else {
         runPrompt()
    }
}

fun runPrompt() {
    while (true) {
        print("> ")
        val sourceCode = readLine() ?: break
        runSourceCode(sourceCode)
    }
}

fun runFile(filename: String) {
    val sourceCode = File(filename).readText(StandardCharsets.UTF_8)
    runSourceCode(sourceCode)
}

fun runSourceCode(sourceCode: String) {
    TODO("Not yet implemented")
}
