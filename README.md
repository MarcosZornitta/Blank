# Blank Interpreter

## Description

The Blank Interpreter is a "Blank Instructions" interpreter created in JAVA as exercise of Programming I class @ UFFS

## Compiling

At the "fonte" folder:
`javac *.java */*/*.java`

## Creating JAR File

Use the following code at the "fonte" folder: <br/>
`jar -cmfv manifest.txt ../Blank.jar *.class blank/lang/*.class`

## Running

The Blank Interpreter needs a file with Blank Instructions to work. You can use any on the `/exemplos` folder. Use the following command at the root folder after generate the JAR file: <br />
`java -jar Blank.jar {file}`