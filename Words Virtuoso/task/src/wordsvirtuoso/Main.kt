package wordsvirtuoso

import java.io.File
import kotlin.random.Random
import kotlin.system.exitProcess

class VirtuosoException( str: String ): Exception(str)

var words = setOf<String>() // words dictionary
var candidates = setOf<String>() // candidates for secret word
var clues = mutableListOf<String>() // list of clues given to user
var wrongChars = sortedSetOf<Char>() // wrong input characters

fun main(args: Array<String>) {
    try {
        checkFiles(args)
    } catch (e: VirtuosoException) {
        println(e.message)
        exitProcess(0)
    }
    println("Words Virtuoso")

    // get a random word from candidates
    val guessed = candidates.elementAt(Random.nextInt(0, candidates.size))
    var attempt = 0
    val startTime = System.currentTimeMillis()
    while (true) {
        println("\nInput a 5-letter word:")
        val nextWord = readln().lowercase()
        attempt++
        if (nextWord.equals("exit", true)) {
            println("\nThe game is over.")
            exitProcess(0)
        }
        when (isWordCorrect(nextWord)) {
            1 -> {
                println("The input isn't a 5-letter word.")
                continue
            }
            2 -> {
                println("One or more letters of the input aren't valid.")
                continue
            }
            3 -> {
                println("The input has duplicate letters.")
            }
            0 -> {
                if ( !words.contains(nextWord) ) {
                    println("The input word isn't included in my words list.")
                    continue
                } else printClue(guessed, nextWord)
                if (guessed.equals(nextWord)) {
                    val endTime = System.currentTimeMillis()
                    val seconds = (endTime - startTime) / 1_000L
                    println("\nCorrect!")
                    if (attempt == 1) println("Amazing luck! The solution was found at once.")
                    else println("The solution was found after $attempt tries in $seconds seconds.")
                    exitProcess(0)
                } else printWrongChars()
            }
        }
    }
}

fun printWrongChars() {
    print("\u001B[48:5:14m")
    for (ch in wrongChars) print(ch)
    println("\u001B[0m")
}

fun printClue(guessed: String, word: String) {
    var clue = ""
    for (i in word.indices) {
        val ch = word[i].uppercaseChar()
        if (word[i] == guessed[i])
            clue += "\u001B[48:5:10m$ch\u001B[0m"
        else if (guessed.contains(word[i]))
            clue += "\u001B[48:5:11m$ch\u001B[0m"
        else {
            clue += "\u001B[48:5:7m$ch\u001B[0m"
            wrongChars.add(ch)
        }
    }
    clues.add(clue)
    for (elem in clues) println(elem)
}

fun checkFiles(args: Array<String>) {

    // check number of arguments
    if (args.size != 2)
        throw VirtuosoException("Error: Wrong number of arguments.")

    // check if words file exists
    val fileWords = File(args[0])
    if (!fileWords.exists())
        throw VirtuosoException("Error: The words file ${args[0]} doesn't exist.")

    // check if candidates file exists
    val fileCandidate = File(args[1])
    if (!fileCandidate.exists())
        throw VirtuosoException("Error: The candidate words file ${args[1]} doesn't exist.")

    // check if the words are valid
    words = readCheckWords(fileWords)
    candidates = readCheckWords(fileCandidate)

    // check how many candidates are not included in words
    var numCandNotIncluded = 0
    for (cand in candidates) if (!words.contains(cand)) numCandNotIncluded++
    if (numCandNotIncluded > 0)
        throw VirtuosoException("Error: $numCandNotIncluded candidate words are not included in the ${args[0]} file.")
}

fun readCheckWords( file: File ): Set<String> {
    val wordsList = file.readLines().toMutableList()
    for(i in wordsList.indices) wordsList[i] = wordsList[i].lowercase()
    val words = wordsList.toSet()
    var numIncorrectWords = 0
    for (word in words){
        if (isWordCorrect(word) != 0) numIncorrectWords++
    }
    if (numIncorrectWords != 0) throw VirtuosoException("Error: $numIncorrectWords invalid words were found in the ${file.name} file.")
    return words
}

/** Check if the input string is a valid word.
 *  Returns 0 if the word is valid, returns:
 *  1 if the length isn't 5
 *  2 if the word contains incorrect characters
 *  3 if contains duplicates
 */
fun isWordCorrect( str: String) : Int {
    val regex = Regex("[a-zA-Z]{5}")
    return if (str.length != 5) 1
    else if (!regex.matches(str)) 2
    else if (str.lowercase().toSet().size != 5) 3
    else 0
}
