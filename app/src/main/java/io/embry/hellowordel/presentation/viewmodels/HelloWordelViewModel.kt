package io.embry.hellowordel.presentation.viewmodels

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TilePosition
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.domain.WordsRepo
import io.embry.hellowordel.ui.theme.Approximate
import io.embry.hellowordel.ui.theme.Blank
import io.embry.hellowordel.ui.theme.Correct
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.Incorrect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.Locale
import javax.inject.Inject
import kotlin.IllegalStateException
import kotlin.random.Random

@HiltViewModel
class HelloWordelViewModel @Inject constructor(private val wordsRepo: WordsRepo) : ViewModel() {
    private var matchedLetters = mutableListOf<String>()
    private var guessedLetters = mutableListOf<GuessedLetter>()
    private var wordelState: WordelState = resetWordel()
    private var currentTilePosition: TilePosition = TilePosition.ZERO
    private var currentRowPosition: RowPosition = RowPosition.ZERO
    private lateinit var word: String
    private var seed: Int = 0

    private val _shareState = MutableLiveData<Pair<String, Int>>()
    val shareState: LiveData<Pair<String, Int>>
        get() = _shareState

    private var previousUiState: WordelUiState? = null

    private val _wordelUiState =
        MutableStateFlow<WordelUiState>(
            WordelUiState.RowInProgress(
                wordelState = wordelState,
                emptyList()
            )
        )
    val wordel: StateFlow<WordelUiState>
        get() = _wordelUiState

    fun setup(seed: Int? = null) {
        viewModelScope.launch {
            wordsRepo.initialiseRepo {
                resetWordel()
                if (seed == null) {
                    val wordel = wordsRepo.getNextWord()
                    this@HelloWordelViewModel.word = wordel.second
                    this@HelloWordelViewModel.seed = wordel.first
                } else {
                    this@HelloWordelViewModel.seed = seed
                    _wordelUiState.value = WordelUiState.ChallengeDialog(seed = seed)
                }
            }
        }
    }

    data class GuessedLetter(val letter: String, val color: Color) {
        override fun equals(other: Any?): Boolean {
            return if (other !is GuessedLetter) false
            else other.letter.equals(letter, true)
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    fun onNextButtonClicked() {
        val random = Random.nextInt(200)
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = null,
            animationRowPosition = null
        )
    }

    fun onStartButtonClicked() {
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = null,
            animationRowPosition = null
        )
        val random = Random.nextInt(200)
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = null,
            animationRowPosition = null
        )
    }

    sealed class WordelUiState {
        data class RowInProgress(
            val wordelState: WordelState,
            val guessedLetters: List<GuessedLetter>?,
            val animationRowPosition: RowPosition? = null
        ) : WordelUiState()

        data class RowComplete(
            val wordelState: WordelState,
            val guessedLetters: List<GuessedLetter>?
        ) : WordelUiState()

        data class InvalidWordError(
            val wordelState: WordelState,
            val guessedLetters: List<GuessedLetter>?
        ) : WordelUiState()

        data class Victory(val wordelState: WordelState, val animationRowPosition: RowPosition) :
            WordelUiState()

        data class Loss(val wordelState: WordelState) : WordelUiState()
        data class ShowHelp(
            val wordelState: WordelState,
            val guessedLetters: List<GuessedLetter>?
        ) : WordelUiState()

        data class LettersMissingError(
            val wordelState: WordelState,
            val guessedLetters: List<GuessedLetter>?
        ) : WordelUiState()

        data class ChallengeDialog(val seed: Int) : WordelUiState()
        object Loading : WordelUiState()
    }

    fun onLetterEntered(letter: String) {
        val tileState =
            getTileState(rowPosition = currentRowPosition, tilePosition = currentTilePosition)
        tileState.text = if (letter.isEmpty() || letter.toCharArray()
                .any { !it.isLetter() }
        ) "" else letter.last().toString()
        //get current row from tile position.
        val row = getRowState(rowPosition = currentRowPosition)
        //check if all letters are entered, if not just emit the letter changed
        if (!areAllLettersFilled(rowState = row)) {
            _wordelUiState.value = WordelUiState.RowInProgress(
                wordelState = wordelState,
                guessedLetters = guessedLetters.toList()
            )
            incrementTile()
        } else {
            _wordelUiState.value = WordelUiState.RowComplete(
                wordelState = wordelState,
                guessedLetters = guessedLetters.toList()
            )
        }
    }

    fun onHelpPressed() {
        if (_wordelUiState.value is WordelUiState.ShowHelp) return
        previousUiState = _wordelUiState.value
        _wordelUiState.value =
            WordelUiState.ShowHelp(wordelState = wordelState, guessedLetters = guessedLetters)
    }

    fun challengeDismissed(ignoreChallenge: Boolean) {
        if (ignoreChallenge) {
            resetWordel()
            val wordel = wordsRepo.getNextWord()
            this.word = wordel.second
            this.seed = wordel.first
        } else {
            val wordel = wordsRepo.getSeed(seed = seed)
            this.word = wordel.second
            this.seed = wordel.first
        }
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = null,
            animationRowPosition = null
        )
    }

    fun onHelpDismissed() {
        _wordelUiState.value =
            previousUiState ?: throw IllegalStateException("Previous UI State cannot be null!")
    }

    fun onVictoryDismissed() {
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = emptyList()
        )
    }

    fun deletePressed() {
        val currentRow = getRowState(rowPosition = currentRowPosition)
        if (!areAllLettersFilled(currentRow)) {
            decrementTile()
        }
        val tileState =
            getTileState(rowPosition = currentRowPosition, tilePosition = currentTilePosition)
        tileState.text = ""
        _wordelUiState.value = WordelUiState.RowInProgress(
            wordelState = wordelState,
            guessedLetters = guessedLetters,
            animationRowPosition = null
        )
        if (areAllLettersFilled(currentRow)) {
            decrementTile()
        }
    }

    fun enterPressed() {
        val row = getRowState(rowPosition = currentRowPosition)
        if (!areAllLettersFilled(rowState = row)) {
            _wordelUiState.value = WordelUiState.LettersMissingError(
                wordelState = wordelState,
                guessedLetters = guessedLetters
            )
            return
        }
        //all letters are filled, check if word is correct
        val correct = validateAnswer(rowState = row)

        if (!wordsRepo.containsWord(getWord(rowState = row))) {
            _wordelUiState.value = WordelUiState.InvalidWordError(
                wordelState = wordelState,
                guessedLetters = guessedLetters.toList()
            )
            return
        }
        if (correct) {
            //success!
            gameComplete(rowState = row)
            _wordelUiState.value = WordelUiState.Victory(
                wordelState = wordelState,
                animationRowPosition = currentRowPosition
            )
            incrementRow()
        } else {
            //detect if a letter is in the correct spot, or failing which, detect if a letter is contained within word
            word.forEach {
                matchedLetters.add(it.toString())
            }
            detectCorrectLetters(row.tile0)
            detectCorrectLetters(row.tile1)
            detectCorrectLetters(row.tile2)
            detectCorrectLetters(row.tile3)
            detectCorrectLetters(row.tile4)
            val newRowPosition = getNextRowPosition(rowPosition = currentRowPosition)
            matchedLetters.clear()
            if (newRowPosition == null) {
                _wordelUiState.value = WordelUiState.Loss(wordelState = wordelState)
                return
            }
            _wordelUiState.value = WordelUiState.RowInProgress(
                wordelState = wordelState,
                guessedLetters = guessedLetters.toList(),
                animationRowPosition = currentRowPosition
            )
            incrementRow()
        }
    }

    fun onSharePressed() {
        val sb = StringBuilder()
        wordelState.getRows().filter { it.getTiles().all { it.color != Blank } }
            .forEach { rowState ->
                sb.append(generateShareRow(rowState = rowState) + "\n")
            }
        _shareState.value = Pair(sb.toString(), seed)
    }

    //region private
    private fun generateShareRow(rowState: RowState): String {
        val sb = StringBuilder()
        sb.append(generateShareTile(tileState = rowState.tile0))
        sb.append(generateShareTile(tileState = rowState.tile1))
        sb.append(generateShareTile(tileState = rowState.tile2))
        sb.append(generateShareTile(tileState = rowState.tile3))
        sb.append(generateShareTile(tileState = rowState.tile4))
        return sb.toString()
    }


    private fun generateShareTile(tileState: TileState): String? {
        return when (tileState.color) {
            Correct -> {
                "🟧"
            }
            Approximate -> {
                "🟦"
            }
            Incorrect -> {
                "⬜"
            }
            else -> {
                null
            }
        }
    }

    private fun resetWordel(): WordelState {
        matchedLetters.clear()
        guessedLetters.clear()
        wordelState = WordelState(
            row1 = RowState(),
            row2 = RowState(),
            row3 = RowState(),
            row4 = RowState(),
            row5 = RowState(),
            row0 = RowState()
        )
        resetRow(rowState = wordelState.row1, rowPosition = RowPosition.FIRST)
        resetRow(rowState = wordelState.row2, rowPosition = RowPosition.SECOND)
        resetRow(rowState = wordelState.row3, rowPosition = RowPosition.THIRD)
        resetRow(rowState = wordelState.row4, rowPosition = RowPosition.FOURTH)
        resetRow(rowState = wordelState.row5, rowPosition = RowPosition.FIFTH)
        resetRow(rowState = wordelState.row0, rowPosition = RowPosition.ZERO)
        currentRowPosition = RowPosition.ZERO
        currentTilePosition = TilePosition.ZERO
        return wordelState
    }

    private fun resetRow(rowState: RowState, rowPosition: RowPosition) {
        rowState.tile1.rowPosition = rowPosition
        rowState.tile2.rowPosition = rowPosition
        rowState.tile3.rowPosition = rowPosition
        rowState.tile4.rowPosition = rowPosition
        rowState.tile0.rowPosition = rowPosition
    }

    private fun getRowState(rowPosition: RowPosition): RowState {
        return when (rowPosition) {
            RowPosition.FIRST -> {
                wordelState.row1
            }
            RowPosition.SECOND -> {
                wordelState.row2
            }
            RowPosition.THIRD -> {
                wordelState.row3
            }
            RowPosition.FOURTH -> {
                wordelState.row4
            }
            RowPosition.FIFTH -> {
                wordelState.row5
            }
            RowPosition.ZERO -> {
                wordelState.row0
            }
        }
    }

    private fun validateAnswer(rowState: RowState): Boolean {
        return rowState.tile0.text.equals(word.first().toString(), true) &&
                rowState.tile1.text.equals(word[1].toString(), true) &&
                rowState.tile2.text.equals(word[2].toString(), true) &&
                rowState.tile3.text.equals(word[3].toString(), true) &&
                rowState.tile4.text.equals(word[4].toString(), true)
    }

    private fun gameComplete(rowState: RowState) {
        rowState.tile0.color = Correct
        rowState.tile0.textColor = FilledText
        rowState.tile1.color = Correct
        rowState.tile1.textColor = FilledText
        rowState.tile2.color = Correct
        rowState.tile2.textColor = FilledText
        rowState.tile3.color = Correct
        rowState.tile3.textColor = FilledText
        rowState.tile4.color = Correct
        rowState.tile4.textColor = FilledText
    }

    private fun areAllLettersFilled(rowState: RowState): Boolean {
        return rowState.tile1.text.isNotBlank() &&
                rowState.tile2.text.isNotBlank() &&
                rowState.tile3.text.isNotBlank() &&
                rowState.tile4.text.isNotBlank() &&
                rowState.tile0.text.isNotBlank()
    }

    private fun detectCorrectLetters(tileState: TileState) {
        val correct = tileState.text.equals(word[tileState.tilePosition.position].toString(), true)
        if (correct) {
            val letter = matchedLetters.firstOrNull { it.equals(tileState.text, true) }
            if (letter != null) {
                matchedLetters.remove(letter)
                val guess = GuessedLetter(letter = letter.uppercase(Locale.ROOT), color = Correct)
                if (guessedLetters.contains(guess)) {
                    guessedLetters.remove(guess)
                }
                guessedLetters.add(guess)
            }
            tileState.textColor = FilledText
            tileState.color = Correct
        } else {
            val match = word.contains(tileState.text, true)
            return if (match) {
                val letter = matchedLetters.firstOrNull { it.equals(tileState.text, true) }
                if (letter == null) {
                    tileState.color = Incorrect
                    tileState.textColor = FilledText
                } else {
                    val guess =
                        GuessedLetter(letter = letter.uppercase(Locale.ROOT), color = Approximate)
                    if (!guessedLetters.contains(guess)) {
                        guessedLetters.add(guess)
                    }
                    matchedLetters.remove(letter)
                    tileState.color = Approximate
                    tileState.textColor = FilledText
                }
            } else {
                val guess =
                    GuessedLetter(letter = tileState.text.uppercase(Locale.ROOT), color = Incorrect)
                if (!guessedLetters.contains(guess)) {
                    guessedLetters.add(guess)
                }
                tileState.color = Incorrect
                tileState.textColor = FilledText
            }
        }
    }

    private fun getTileState(rowPosition: RowPosition, tilePosition: TilePosition): TileState {
        val rowState = getRowState(rowPosition = rowPosition)
        return when (tilePosition) {
            TilePosition.ZERO -> {
                rowState.tile0
            }
            TilePosition.FIRST -> {
                rowState.tile1
            }
            TilePosition.SECOND -> {
                rowState.tile2
            }
            TilePosition.THIRD -> {
                rowState.tile3
            }
            TilePosition.FOURTH -> {
                rowState.tile4
            }
        }
    }

    private fun getNextRowPosition(rowPosition: RowPosition): RowPosition? {
        return when (rowPosition) {
            RowPosition.FIRST -> {
                RowPosition.SECOND
            }
            RowPosition.SECOND -> {
                RowPosition.THIRD
            }
            RowPosition.THIRD -> {
                RowPosition.FOURTH
            }
            RowPosition.FOURTH -> {
                RowPosition.FIFTH
            }
            RowPosition.FIFTH -> {
                null
            }
            RowPosition.ZERO -> {
                RowPosition.FIRST
            }
        }
    }

    private fun incrementTile() {
        if (currentRowPosition == RowPosition.FIFTH && currentTilePosition == TilePosition.FOURTH) return
        if (currentTilePosition == TilePosition.FOURTH) {
            return
        }
        var tile = currentTilePosition.position
        tile++
        currentTilePosition = TilePosition.values()[tile]
    }

    private fun decrementTile() {
        if (currentRowPosition == RowPosition.ZERO && currentTilePosition == TilePosition.ZERO) return
        if (currentTilePosition.position <= TilePosition.ZERO.position) {
            return
        }
        var tile = currentTilePosition.position
        tile--
        currentTilePosition = TilePosition.values()[tile]
        Log.v("TAGGART", "current row is ${currentRowPosition.position}")
        Log.v("TAGGART", "current tile is ${currentTilePosition.position}")
    }

    private fun incrementRow() {
        if (currentRowPosition == RowPosition.FIFTH && currentTilePosition == TilePosition.FOURTH) return
        if (currentRowPosition == RowPosition.FIFTH) return
        var row = currentRowPosition.position
        row++
        currentRowPosition = RowPosition.values()[row]
        currentTilePosition = TilePosition.ZERO
    }

    private fun getWord(rowState: RowState): String {
        return (rowState.tile0.text + rowState.tile1.text + rowState.tile2.text + rowState.tile3.text + rowState.tile4.text).lowercase(
            Locale.ROOT
        )
    }
}
//endregion