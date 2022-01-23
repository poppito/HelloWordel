package io.embry.hellowordel.data

import android.content.Context
import io.embry.hellowordel.domain.WordsRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.IllegalStateException
import javax.inject.Inject
import kotlin.random.Random

class WordsRepoImpl @Inject constructor(private val context: Context) : WordsRepo {
    //I am a bit torn. A data class with field names is a lot more descriptive than a map with a bunch of map entries
    private var words = mutableMapOf<Int, String>()
    private val scope = CoroutineScope(Dispatchers.IO)

    //potentially not the best way of doing this.
    override suspend fun createContent(): Boolean {
        var response = true
        scope.launch {
            val task = async {
                try {
                    val stream = context.assets.open("words.csv")
                    val reader = BufferedReader(InputStreamReader(stream))
                    do {
                        val line = reader.readLine()
                        if (line != null && line != "") {
                            val word = generateWord(input = line)
                            words[word.first] = word.second
                        }
                    } while (line != null && line != "")
                } catch (e: Exception) {
                    response = false
                }
            }
            task.await()
        }
        return response
    }

    //elegance is but a myth
    override fun getNextWord(): Pair<Int, String> {
        val random = Random(words.size - 1).nextInt()
        val word = words[random] ?: throw IllegalStateException("Word cannot be null")
        return Pair(random, word)
    }

    private fun generateWord(input: String): Pair<Int, String> {
        val types = input.split(",").toTypedArray()
        return Pair(types[0].toInt(), types[1])
    }
}