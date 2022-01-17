package io.embry.hellowordel.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TilePosition
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject


@HiltViewModel
class HelloWordelViewModel @Inject constructor() : ViewModel() {
    private var wordelState: WordelState = resetWordel()
    private val _wordelState = MutableStateFlow(wordelState)
    val wordel: StateFlow<WordelState>
        get() = _wordelState

    fun setup() {
        resetWordel()
    }

    fun validateRow(rowState: RowState): Boolean {
        return rowState.tile1.text.isNotBlank() &&
                rowState.tile1.text.isNotBlank() &&
                rowState.tile1.text.isNotBlank() &&
                rowState.tile1.text.isNotBlank() &&
                rowState.tile1.text.isNotBlank() &&
                rowState.tile1.text.isNotBlank()
    }

    fun onLetterEntered(tilePosition: TilePosition, rowPosition: RowPosition, letter: String) {
        viewModelScope.launch {
            val tileState = getTileState(rowPosition = rowPosition, tilePosition = tilePosition)
            tileState.text = if (letter.isEmpty()) "" else letter.last().toString()
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
            row6 = RowState()
        )
        resetRow(rowState = wordelState.row1, rowPosition = RowPosition.FIRST)
        resetRow(rowState = wordelState.row2, rowPosition = RowPosition.SECOND)
        resetRow(rowState = wordelState.row3, rowPosition = RowPosition.THIRD)
        resetRow(rowState = wordelState.row4, rowPosition = RowPosition.FOURTH)
        resetRow(rowState = wordelState.row5, rowPosition = RowPosition.FIFTH)
        resetRow(rowState = wordelState.row6, rowPosition = RowPosition.SIXTH)
        return wordelState
    }

    private fun resetRow(rowState: RowState, rowPosition: RowPosition) {
        rowState.tile1.rowPosition = rowPosition
        rowState.tile2.rowPosition = rowPosition
        rowState.tile3.rowPosition = rowPosition
        rowState.tile4.rowPosition = rowPosition
        rowState.tile5.rowPosition = rowPosition
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
            RowPosition.SIXTH -> {
                wordelState.row6
            }
            RowPosition.NONE -> {
                throw IllegalStateException("Row position must not be null")
            }
        }
    }

    private fun getTileState(rowPosition: RowPosition, tilePosition: TilePosition): TileState {
        val rowState = getRowState(rowPosition = rowPosition)
        return when (tilePosition) {
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
            TilePosition.FIFTH -> {
                rowState.tile5
            }
        }
    }
}