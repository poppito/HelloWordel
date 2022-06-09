package io.embry.hellowordel.presentation.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.data.keyboardLine1
import io.embry.hellowordel.data.keyboardLine2
import io.embry.hellowordel.data.keyboardLine3
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.Blank
import io.embry.hellowordel.ui.theme.BlankText
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.typography

@Composable
fun WordelGame(
    viewModel: HelloWordelViewModel,
    wordelState: WordelState, showEnter: Boolean, showError: Boolean,
    guessedLetters: List<HelloWordelViewModel.GuessedLetter>?,
    error: String? = null,
    animateRowPosition: RowPosition? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WordelRow(
            state = wordelState.row0,
            animate = animateRowPosition == RowPosition.ZERO
        )
        WordelRow(
            state = wordelState.row1,
            animate = animateRowPosition == RowPosition.FIRST
        )
        WordelRow(
            state = wordelState.row2,
            animate = animateRowPosition == RowPosition.SECOND
        )
        WordelRow(
            state = wordelState.row3,
            animate = animateRowPosition == RowPosition.THIRD
        )
        WordelRow(
            state = wordelState.row4,
            animate = animateRowPosition == RowPosition.FOURTH
        )
        WordelRow(
            state = wordelState.row5,
            animate = animateRowPosition == RowPosition.FIFTH
        )

        Keyboard(guessedLetters = guessedLetters, viewModel = viewModel)

        if (showEnter) {
            Keyboard(viewModel = viewModel)
        }
        if (showError && error != null) {
            Error(error = error)
        }
    }
}

@Composable
fun Keyboard(
    viewModel: HelloWordelViewModel,
    guessedLetters: List<HelloWordelViewModel.GuessedLetter>?
) {
    Spacer(modifier = Modifier.size(16.dp))
    KeyboardLine(letters = keyboardLine1, guessedLetters = guessedLetters, viewModel = viewModel)
    KeyboardLine(letters = keyboardLine2, guessedLetters = guessedLetters, viewModel = viewModel)
    KeyboardLine(letters = keyboardLine3, guessedLetters = guessedLetters, viewModel = viewModel)
}

@Composable
fun KeyboardLine(
    viewModel: HelloWordelViewModel,
    letters: List<String>,
    guessedLetters: List<HelloWordelViewModel.GuessedLetter>?
) {
    LazyRow(
        modifier = Modifier
            .background(color = if (isSystemInDarkTheme()) Color.Black else Color.White)
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // items
        items(letters.size) { index ->
            val guessed = guessedLetters?.firstOrNull { it.letter == letters[index] }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .padding(start = 4.dp, bottom = 4.dp)
                    .background(guessed?.color ?: Blank),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letters[index],
                    textAlign = TextAlign.Center,
                    color = if (guessed != null) FilledText else BlankText,
                    style = typography.h1,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { viewModel.onLetterEntered(letter = letters[index]) }
                )
            }
        }
    }
}

@Composable
fun WordelRow(state: RowState, animate: Boolean) {
    Spacer(modifier = Modifier.size(16.dp))
    Row(
        Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Tile(state = state.tile0, animate = animate)
        Tile(state = state.tile1, animate = animate)
        Tile(state = state.tile2, animate = animate)
        Tile(state = state.tile3, animate = animate)
        Tile(state = state.tile4, animate = animate)
    }
}

@Composable
fun Tile(state: TileState, animate: Boolean) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(scale) {
        scale.animateTo(
            1f,
            TweenSpec(
                durationMillis = 1000,
                delay = 200,
            )
        )
    }
    TextField(
        value = state.text,
        onValueChange = {
            //unused now
        },
        singleLine = true,
        maxLines = 1,
        textStyle = TextStyle(color = state.textColor),
        readOnly = true,
        modifier = Modifier
            .size(48.dp)
            .scale(
                scaleX = if (animate) scale.value else 1f,
                scaleY = if (animate) scale.value else 1f
            ),
        colors = TextFieldDefaults.textFieldColors(
            textColor = state.textColor,
            backgroundColor = state.color
        )
    )
    Spacer(modifier = Modifier.size(4.dp))
}

@Composable
fun Error(error: String) {
    Spacer(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
    )
    Text(
        text = error,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
        style = typography.body1
    )
}