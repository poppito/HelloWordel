package io.embry.hellowordel.domain

interface WordsRepo {
    suspend fun createContent(): Boolean
    fun getNextWord(): Pair<Int, String>
}