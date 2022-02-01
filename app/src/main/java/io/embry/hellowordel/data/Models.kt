package io.embry.hellowordel.data

import androidx.compose.ui.graphics.Color
import io.embry.hellowordel.ui.theme.Blank
import io.embry.hellowordel.ui.theme.BlankText


val keyboardLine1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
val keyboardLine2 = listOf("A", "S", "D", "F", "G", "H", "I", "J", "K", "L")
val keyboardLine3 = listOf("Z", "X", "C", "V", "B", "N", "M")

data class TileState(
    var color: Color = Blank,
    var textColor: Color = BlankText,
    var text: String = "",
    val tilePosition: TilePosition,
    var rowPosition: RowPosition = RowPosition.ZERO,
    var readOnly: Boolean = false
)

data class RowState(
    val tile0: TileState = TileState(tilePosition = TilePosition.ZERO),
    val tile1: TileState = TileState(tilePosition = TilePosition.FIRST),
    val tile2: TileState = TileState(tilePosition = TilePosition.SECOND),
    val tile3: TileState = TileState(tilePosition = TilePosition.THIRD),
    val tile4: TileState = TileState(tilePosition = TilePosition.FOURTH)
) {
    fun getTiles(): List<TileState> = listOf(tile0, tile1, tile2, tile3, tile4)
}

data class WordelState(
    val row0: RowState,
    val row1: RowState,
    val row2: RowState,
    val row3: RowState,
    val row4: RowState,
    val row5: RowState
) {
    //surely there is a more elegant way of emitting this
    //perhaps a comparison of letters on each tile?
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    fun getRows(): List<RowState> = listOf(row0, row1, row2, row3, row4, row5)
}

enum class RowPosition(val position: Int) {
    ZERO(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5);
}

enum class TilePosition(val position: Int) {
    ZERO(0),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);
}