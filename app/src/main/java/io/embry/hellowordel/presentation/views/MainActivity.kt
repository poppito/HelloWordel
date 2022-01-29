package io.embry.hellowordel.presentation.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.embry.hellowordel.R
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TilePosition
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.Approximate
import io.embry.hellowordel.ui.theme.Correct
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.HelloWordelTheme
import io.embry.hellowordel.ui.theme.Incorrect
import io.embry.hellowordel.ui.theme.Teal200
import io.embry.hellowordel.ui.theme.headerFont
import io.embry.hellowordel.ui.theme.typography
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private val viewModel: HelloWordelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWordelTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            viewModel.setup(
                                seed = intent.data?.pathSegments?.firstOrNull()?.toInt()
                            )
                            viewModel.wordel.collect {
                                setContent {
                                    HelloWordel(wordelUiState = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HelloWordel(wordelUiState: HelloWordelViewModel.WordelUiState) {
        Column(
            Modifier.scrollable(
                ScrollState(0),
                orientation = Orientation.Vertical
            )
        ) {
            Setup(showHelp = { viewModel.onHelpPressed() })
            when (wordelUiState) {
                is HelloWordelViewModel.WordelUiState.RowInProgress -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = false,
                        showError = false,
                        guessedLetters = wordelUiState.guessedLetters,
                        animateRowPosition = wordelUiState.animationRowPosition
                    )
                }
                is HelloWordelViewModel.WordelUiState.RowComplete -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = true,
                        showError = false,
                        guessedLetters = wordelUiState.guessedLetters
                    )
                }
                is HelloWordelViewModel.WordelUiState.InvalidWordError -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = false,
                        showError = true,
                        guessedLetters = wordelUiState.guessedLetters
                    )
                }
                is HelloWordelViewModel.WordelUiState.Loss -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = false,
                        showError = false,
                        guessedLetters = null
                    )
                    Loss()
                }
                is HelloWordelViewModel.WordelUiState.Victory -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = false,
                        showError = false,
                        guessedLetters = null
                    )
                    Victory()
                }
                is HelloWordelViewModel.WordelUiState.ShowHelp -> {
                    WordelGame(
                        wordelState = wordelUiState.wordelState,
                        showEnter = false,
                        showError = false,
                        guessedLetters = wordelUiState.guessedLetters
                    )
                    HelpAlert()
                }
            }
        }
    }

    @Composable
    fun WordelGame(
        wordelState: WordelState, showEnter: Boolean, showError: Boolean,
        guessedLetters: List<HelloWordelViewModel.GuessedLetter>?,
        animateRowPosition: RowPosition? = null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WordelRow(
                state = wordelState.row0,
                enabled = wordelState.currentActiveRow == RowPosition.ZERO,
                animate = animateRowPosition == RowPosition.ZERO
            )
            WordelRow(
                state = wordelState.row1,
                enabled = wordelState.currentActiveRow == RowPosition.FIRST,
                animate = animateRowPosition == RowPosition.FIRST
            )
            WordelRow(
                state = wordelState.row2,
                enabled = wordelState.currentActiveRow == RowPosition.SECOND,
                animate = animateRowPosition == RowPosition.SECOND
            )
            WordelRow(
                state = wordelState.row3,
                enabled = wordelState.currentActiveRow == RowPosition.THIRD,
                animate = animateRowPosition == RowPosition.THIRD
            )
            WordelRow(
                state = wordelState.row4,
                enabled = wordelState.currentActiveRow == RowPosition.FOURTH,
                animate = animateRowPosition == RowPosition.FOURTH
            )
            WordelRow(
                state = wordelState.row5,
                enabled = wordelState.currentActiveRow == RowPosition.FIFTH,
                animate = animateRowPosition == RowPosition.FIFTH
            )

            GuessedLetters(letters = guessedLetters)

            if (showEnter) {
                Enter()
            }
            if (showError) {
                InvalidWordError()
            }
        }
    }

    @Composable
    fun WordelRow(state: RowState, enabled: Boolean, animate: Boolean) {
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Tile(state = state.tile0, enabled = enabled, animate = animate)
            Tile(state = state.tile1, enabled = enabled, animate = animate)
            Tile(state = state.tile2, enabled = enabled, animate = animate)
            Tile(state = state.tile3, enabled = enabled, animate = animate)
            Tile(state = state.tile4, enabled = enabled, animate = animate)
        }
    }

    @Composable
    fun Tile(state: TileState, enabled: Boolean, animate: Boolean) {
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
                viewModel.onLetterEntered(
                    tilePosition = state.tilePosition,
                    rowPosition = state.rowPosition,
                    letter = it.uppercase(Locale.ROOT)
                )
            },
            singleLine = true,
            maxLines = 1,
            textStyle = TextStyle(color = state.textColor, fontFamily = headerFont),
            readOnly = !enabled,
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
    fun InvalidWordError() {
        Spacer(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth()
        )
        Text(
            text = stringResource(id = R.string.txt_invalid_word),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            style = typography.body1
        )
    }

    @Composable
    fun Enter() {
        Spacer(modifier = Modifier.size(24.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.height(32.dp)
        ) {
            Button(
                modifier = Modifier
                    .width(100.dp)
                    .height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Teal200
                ),
                onClick = {
                    viewModel.enterPressed()
                }) {
                ButtonLabel(label = stringResource(id = R.string.btn_enter))
            }
        }
    }

    @Composable
    fun ButtonLabel(label: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                textAlign = TextAlign.Center,
                style = typography.caption
            )
            Spacer(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
            )
        }
    }

    @Composable
    fun Setup(showHelp: () -> Unit) {
        Spacer(modifier = Modifier.size(24.dp))
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.txt_app_title),
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth(),
                    style = typography.h1,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.btn_help),
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .clickable {
                            showHelp.invoke()
                        },
                    style = typography.h1,
                    textAlign = TextAlign.Center,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
        }
    }

    @Composable
    fun HelpAlert() {
        HelloWordelTheme {
            AlertDialog(
                onDismissRequest = { viewModel.onHelpDismissed() },
                buttons = {
                    OkButton(
                        onClick = { viewModel.onHelpDismissed() },
                        label = stringResource(id = R.string.btn_ok)
                    )
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.txt_help_dialog_title),
                        style = typography.h1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = { HelpInfo() }
            )
        }
    }

    @Composable
    fun OkButton(onClick: () -> Unit, label: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onClick.invoke() },
                colors = ButtonDefaults.buttonColors(backgroundColor = Teal200)
            ) {
                ButtonLabel(label = label)
            }
        }
        Spacer(modifier = Modifier.size(48.dp))
    }

    @Composable
    fun Victory() {
        HelloWordelTheme {
            viewModel.shareState.observe(this, {
                shareIntent(body = it.first, seed = it.second)
            })
            AlertDialog(
                onDismissRequest = {
                    //not dismissable
                },
                buttons = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_restart),
                            contentDescription = stringResource(id = R.string.btn_restart),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    viewModel.setup()
                                    viewModel.onVictoryDismissed()
                                }
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = stringResource(id = R.string.btn_share),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    viewModel.onSharePressed()
                                }
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.txt_win_dialog_title),
                        style = typography.h1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.txt_win_dialog_body),
                        style = typography.h1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            )
        }
    }

    @Composable
    fun Loss() {
        HelloWordelTheme {
            AlertDialog(
                onDismissRequest = {
                    //not dismissable
                },
                buttons = {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_restart),
                            contentDescription = stringResource(id = R.string.btn_restart),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    viewModel.setup()
                                    viewModel.onVictoryDismissed()
                                }
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.txt_loss_dialog_title),
                        style = typography.h1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = stringResource(id = R.string.txt_loss_dialog_body),
                        style = typography.h1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }
            )
        }
    }

    @Composable
    fun HelpInfo() {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body1),
                style = typography.body1,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body2),
                style = typography.body1,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            WordelRow(
                state = RowState(
                    tile0 = TileState(
                        color = Correct,
                        textColor = FilledText,
                        text = "C",
                        tilePosition = TilePosition.ZERO
                    ),
                    tile1 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "O",
                        tilePosition = TilePosition.FIRST
                    ),
                    tile2 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "N",
                        tilePosition = TilePosition.SECOND
                    ),
                    tile3 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "E",
                        tilePosition = TilePosition.THIRD
                    ),
                    tile4 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "S",
                        tilePosition = TilePosition.FOURTH
                    )
                ), enabled = false,
                animate = false
            )
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body3),
                style = typography.body2,
                textAlign = TextAlign.Center
            )
            WordelRow(
                state = RowState(
                    tile0 = TileState(
                        color = Correct,
                        textColor = FilledText,
                        text = "C",
                        tilePosition = TilePosition.ZERO
                    ),
                    tile1 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "L",
                        tilePosition = TilePosition.FIRST
                    ),
                    tile2 = TileState(
                        color = Approximate,
                        textColor = FilledText,
                        text = "U",
                        tilePosition = TilePosition.SECOND
                    ),
                    tile3 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "E",
                        tilePosition = TilePosition.THIRD
                    ),
                    tile4 = TileState(
                        color = Approximate,
                        textColor = FilledText,
                        text = "Y",
                        tilePosition = TilePosition.FOURTH
                    )
                ), enabled = false,
                animate = false
            )
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body4),
                style = typography.body2,
                textAlign = TextAlign.Center
            )
            WordelRow(
                state = RowState(
                    tile0 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "Z",
                        tilePosition = TilePosition.ZERO
                    ),
                    tile1 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "O",
                        tilePosition = TilePosition.FIRST
                    ),
                    tile2 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "N",
                        tilePosition = TilePosition.SECOND
                    ),
                    tile3 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "E",
                        tilePosition = TilePosition.THIRD
                    ),
                    tile4 = TileState(
                        color = Incorrect,
                        textColor = FilledText,
                        text = "S",
                        tilePosition = TilePosition.FOURTH
                    )
                ), enabled = false,
                animate = false
            )
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body5),
                style = typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }

    @ExperimentalFoundationApi
    @Composable
    fun GuessedLetters(letters: List<HelloWordelViewModel.GuessedLetter>?) {
        if (letters.isNullOrEmpty()) return
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            cells = GridCells.Adaptive(minSize = 52.dp),
            contentPadding = PaddingValues(24.dp)
        ) {
            // items
            items(letters.size) { index ->
                GuessedLetter(letter = letters[index])
            }
        }
    }

    @Composable
    fun GuessedLetter(letter: HelloWordelViewModel.GuessedLetter) {
        val state =
            TileState(
                color = letter.color,
                text = letter.letter,
                textColor = FilledText,
                tilePosition = TilePosition.ZERO
            )
        Column {
            Row {
                Spacer(modifier = Modifier.size(4.dp))
                Tile(state = state, enabled = false, animate = false)
            }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }

    private fun shareIntent(body: String, seed: Int) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.action = Intent.ACTION_SEND
        val appUrl = getString(R.string.txt_app_url, seed)
        val send = String.format("%s\n%s", body, appUrl)
        shareIntent.putExtra(Intent.EXTRA_TEXT, send)
        shareIntent.type = "text/plain"
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ButtonLabel(label = "Enter")
    }
}