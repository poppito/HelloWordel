package io.embry.hellowordel.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.WordelState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class HelloWordelViewModel @Inject constructor() : ViewModel() {
    private var wordelState: WordelState = resetWordel()
    private val _wordelState = MutableStateFlow(wordelState)
    val wordel: StateFlow<WordelState>
        get() = _wordelState

    private var firstRowState = RowState()
    private var secondRowState = RowState()
    private var thirdRowState = RowState()
    private var fourthRowState = RowState()
    private var fifthRowState = RowState()
    private var sixthRowState = RowState()

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
        resetRow(rowState = wordelState.row1)
        resetRow(rowState = wordelState.row2)
        resetRow(rowState = wordelState.row3)
        resetRow(rowState = wordelState.row4)
        resetRow(rowState = wordelState.row5)
        resetRow(rowState = wordelState.row6)
        return wordelState
    }

    private fun resetRow(rowState: RowState) {
        rowState.tile1.rowPosition = RowPosition.FIRST
        rowState.tile2.rowPosition = RowPosition.SECOND
        rowState.tile3.rowPosition = RowPosition.THIRD
        rowState.tile4.rowPosition = RowPosition.FOURTH
        rowState.tile5.rowPosition = RowPosition.FIFTH
    }
}