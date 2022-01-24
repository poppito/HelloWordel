package io.embry.hellowordel.presentation.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                            viewModel.setup()
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
        Help(showHelp = { viewModel.onHelpPressed() })
        when (wordelUiState) {
            is HelloWordelViewModel.WordelUiState.RowInProgress -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = false,
                    showError = false
                )
            }
            is HelloWordelViewModel.WordelUiState.RowComplete -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = true,
                    showError = false
                )
            }
            is HelloWordelViewModel.WordelUiState.InvalidWordError -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = false,
                    showError = true
                )
            }
            is HelloWordelViewModel.WordelUiState.Loss -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = false,
                    showError = false
                )
            }
            is HelloWordelViewModel.WordelUiState.Victory -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = false,
                    showError = false
                )
            }
            is HelloWordelViewModel.WordelUiState.ShowHelp -> {
                WordelGame(
                    wordelState = wordelUiState.wordelState,
                    showEnter = false,
                    showError = false
                )
                HelpAlert()
            }
        }
    }

    @Composable
    fun WordelGame(wordelState: WordelState, showEnter: Boolean, showError: Boolean) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WordelRow(
                state = wordelState.row0,
                enabled = wordelState.currentActiveRow == RowPosition.ZERO
            )
            WordelRow(
                state = wordelState.row1,
                enabled = wordelState.currentActiveRow == RowPosition.FIRST
            )
            WordelRow(
                state = wordelState.row2,
                enabled = wordelState.currentActiveRow == RowPosition.SECOND
            )
            WordelRow(
                state = wordelState.row3,
                enabled = wordelState.currentActiveRow == RowPosition.THIRD
            )
            WordelRow(
                state = wordelState.row4,
                enabled = wordelState.currentActiveRow == RowPosition.FOURTH
            )
            WordelRow(
                state = wordelState.row5,
                enabled = wordelState.currentActiveRow == RowPosition.FIFTH
            )
            if (showEnter) {
                Enter()
            }
            if (showError) {
                InvalidWordError()
            }
        }
    }

    @Composable
    fun WordelRow(state: RowState, enabled: Boolean) {
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Tile(state = state.tile0, enabled = enabled)
            Tile(state = state.tile1, enabled = enabled)
            Tile(state = state.tile2, enabled = enabled)
            Tile(state = state.tile3, enabled = enabled)
            Tile(state = state.tile4, enabled = enabled)
        }
    }

    @Composable
    fun Tile(state: TileState, enabled: Boolean) {
        Card(
            modifier = Modifier.size(48.dp),
            backgroundColor = state.color
        ) {
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
                readOnly = !enabled
            )
        }
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
    fun Help(showHelp: () -> Unit) {
        Spacer(modifier = Modifier.size(96.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.btn_help),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(48.dp)
                    .clickable {
                        showHelp.invoke()
                    },
                style = typography.h1,
                textAlign = TextAlign.Center,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
            Text(
                text = stringResource(id = R.string.btn_help),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(48.dp)
                    .clickable { /* TODO */ },
                style = typography.h1,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun HelpAlert() {
        HelloWordelTheme() {
            AlertDialog(
                onDismissRequest = { viewModel.onHelpDismissed() },
                buttons = {
                    OkButton {
                        viewModel.onHelpDismissed()
                    }
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
    fun OkButton(onClick: () -> Unit) {
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
                ButtonLabel(label = stringResource(id = R.string.btn_ok))
            }
        }
        Spacer(modifier = Modifier.size(48.dp))
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
                ), enabled = false
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
                ), enabled = false
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
                ), enabled = false
            )
            Text(
                text = stringResource(id = R.string.txt_help_dialog_body5),
                style = typography.body2,
                textAlign = TextAlign.Center
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ButtonLabel(label = "Enter")
    }
}