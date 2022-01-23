package io.embry.hellowordel.domain

interface WordsRepo {
    fun getNextWord(): Pair<Int, String>
}