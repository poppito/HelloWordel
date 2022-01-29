package io.embry.hellowordel.domain

interface WordsRepo {
    fun getNextWord(): Pair<Int, String>
    fun containsWord(word: String): Boolean
    fun getSeed(seed: Int): Pair<Int, String>
}