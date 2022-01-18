package io.embry.hellowordel.data

import androidx.compose.ui.graphics.Color
import io.embry.hellowordel.ui.theme.BlankBg
import io.embry.hellowordel.ui.theme.BlankText

data class TileState(
    var color: Color = BlankBg,
    var textColor: Color = BlankText,
    var text: String = "",
    val tilePosition: TilePosition,
    var rowPosition: RowPosition = RowPosition.NONE,
    var readOnly: Boolean = false
)

data class RowState(
    val tile1: TileState = TileState(tilePosition = TilePosition.FIRST),
    val tile2: TileState = TileState(tilePosition = TilePosition.SECOND),
    val tile3: TileState = TileState(tilePosition = TilePosition.THIRD),
    val tile4: TileState = TileState(tilePosition = TilePosition.FOURTH),
    val tile5: TileState = TileState(tilePosition = TilePosition.FIFTH)
)

data class WordelState(
    val row1: RowState,
    val row2: RowState,
    val row3: RowState,
    val row4: RowState,
    val row5: RowState,
    val row6: RowState,
) {
    //surely there is a more elegant way of emitting this
    //perhaps a comparison of letters on each tile?
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

enum class RowPosition(val position: Int?) {
    NONE(null),
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    SIXTH(6)
}

enum class TilePosition(val position: Int) {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5)
}