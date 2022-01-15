package io.embry.hellowordel.data

import androidx.compose.ui.graphics.Color
import io.embry.hellowordel.ui.theme.Blank

data class TileState(var color: Color = Blank, var text: String = "")
data class RowState(
    val tile1: TileState,
    val tile2: TileState,
    val tile3: TileState,
    val tile4: TileState,
    val tile5: TileState,
    val tile6: TileState
)

data class WordleState(
    val row1: RowState,
    val row2: RowState,
    val row3: RowState,
    val row4: RowState,
    val row5: RowState,
    val row6: RowState,
)