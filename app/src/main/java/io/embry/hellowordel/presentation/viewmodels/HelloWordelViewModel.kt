package io.embry.hellowordel.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TilePosition
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.ui.theme.Approximate
import io.embry.hellowordel.ui.theme.Correct
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.Incorrect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject


@HiltViewModel
class HelloWordelViewModel @Inject constructor() : ViewModel() {
    private var wordelState: WordelState = resetWordel()
    private var word = "PLOUGH"
    private val _wordelState = MutableStateFlow(wordelState)
    val wordel: StateFlow<WordelState>
        get() = _wordelState

    fun setup() {
        resetWordel()
    }

    fun onLetterEntered(tilePosition: TilePosition, rowPosition: RowPosition, letter: String) {
        viewModelScope.launch {
            val tileState = getTileState(rowPosition = rowPosition, tilePosition = tilePosition)
            tileState.text = if (letter.isEmpty()) "" else letter.last().toString()
            //get current row from tile position.
            val row = getRowState(rowPosition = rowPosition)
            //check if all letters are entered, if not just emit the letter changed
            if (!areAllLettersFilled(rowState = row)) {
                _wordelState.value = wordelState
                //focus on next blank tile
                return@launch
            }
            //all letters are filled, check if word is correct
            val correct = validateAnswer(rowState = row)
            if (correct) {
                //success!
                gameComplete(rowState = row)
            } else {
                //detect if a letter is in the correct spot, or failing which, detect if a letter is contained within word
                detectCorrectLetters(row.tile1)
                detectCorrectLetters(row.tile2)
                detectCorrectLetters(row.tile3)
                detectCorrectLetters(row.tile4)
                detectCorrectLetters(row.tile0)
            }
            _wordelState.value = wordelState
        }
    }

    //region private
    private fun resetWordel(): WordelState {
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
            RowPosition.NONE -> {
                throw IllegalStateException("Row position must not be null")
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
        rowState.tile1.color = Correct
        rowState.tile2.color = Correct
        rowState.tile3.color = Correct
        rowState.tile4.color = Correct
        rowState.tile0.color = Correct
    }

    private fun areAllLettersFilled(rowState: RowState): Boolean {
        val filled = rowState.tile1.text.isNotBlank() &&
                rowState.tile2.text.isNotBlank() &&
                rowState.tile3.text.isNotBlank() &&
                rowState.tile4.text.isNotBlank() &&
                rowState.tile0.text.isNotBlank()
        return filled
    }

    private fun detectCorrectLetters(tileState: TileState) {
        val correct = tileState.text.equals(word[tileState.tilePosition.position].toString(), true)
        if (correct) {
            tileState.textColor = FilledText
            tileState.color = Correct
        } else {
            val match = word.contains(tileState.text, true)
            return if (match) {
                tileState.color = Approximate
                tileState.textColor = FilledText
            } else {
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
            TilePosition.SECOND -> {
                rowState.tile2
            }
            TilePosition.THIRD -> {
                rowState.tile3
            }
            TilePosition.FOURTH -> {
                rowState.tile4
            }
            TilePosition.FIRST -> {
                rowState.tile1
            }
        }
    }
}