# A Lox language interpreter written in Kotlin

This is my attempt to write a Lox language interpreter in Kotlin by following [Crafting Interpreters](http://www.craftinginterpreters.com/) book written by Robert Nystrom.

## Done:
- [x] Scanner.
  - [x] Implement block comments as a homework.
- [ ] Parser.

## Prerequisites
Have JDK 17 in $PATH.

## How to build
```
cd {project folder}
./gradlew shadowJar
```

Built jar is found in `{project folder}/build/lib/klox.jar`

## How to run
You can run the interpreter in REPL mode and with a filename.

To run the interpreter in REPL mode, type:

`java -jar ./build/lib/klox.jar`

You will be prompted to enter Lox source code.

To run the interpreter with a filename, type:

`java -jar ./build/lib/klox.jar {filename}`

Where `filename` is an absolute or relative path to a .lox source code file. There are several examples in `examples` folder.