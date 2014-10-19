# Blank Interpreter

## Description

The Blank Interpreter is a language interpreter created in JAVA as exercise of Programming I class @ UFFS

## Compiling

At the root folder:
`javac fonte/*.java fonte/*/*/*.java`

## Creating JAR File

Use the following code at the root folder: <br/>
`jar -cmfv fonte/manifest.txt Blank.jar fonte/*.class fonte/blank/lang`

## Running

The Blank Interpreter needs a file with Blank Instructions to work. You can use any on the `/exemplos` folder
`java -jar Blank.jar {file}`