package io.embry.hellowordel.presentation.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.embry.hellowordel.R
import io.embry.hellowordel.data.keyboardLine1
import io.embry.hellowordel.data.RowPosition
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.data.keyboardLine2
import io.embry.hellowordel.data.keyboardLine3
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.Blank
import io.embry.hellowordel.ui.theme.BlankText
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.HelloWordelTheme
import io.embry.hellowordel.ui.theme.Teal200
import io.embry.hellowordel.ui.theme.typography
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private val viewModel: HelloWordelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val seed = intent?.data?.pathSegments?.last()?.toInt()
        viewModel.setup(seed = seed)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.wordel.collect {
                    setContent {
                        HelloWordelTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(color = MaterialTheme.colors.background) {
                                HelloWordel(wordelUiState = it)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HelloWordel(wordelUiState: HelloWordelViewModel.WordelUiState) {
        Scaffold {
            Column {
                Setup(showHelp = { viewModel.onHelpPressed() })
                when (wordelUiState) {
                    is HelloWordelViewModel.WordelUiState.Loading -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is HelloWordelViewModel.WordelUiState.ChallengeDialog -> {
                        StartChallengeScreen(seed = wordelUiState.seed, viewModel = viewModel)
                    }
                    is HelloWordelViewModel.WordelUiState.RowInProgress -> {
                        WordelGame(
                            wordelState = wordelUiState.wordelState,
                            showEnter = true,
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
                            showEnter = true,
                            showError = true,
                            error = stringResource(id = R.string.txt_invalid_word),
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
                        HelpScreen(viewModel = viewModel)
                    }
                    is HelloWordelViewModel.WordelUiState.LettersMissingError -> {
                        WordelGame(
                            wordelState = wordelUiState.wordelState,
                            showEnter = true,
                            showError = true,
                            error = stringResource(id = R.string.txt_missing_letters),
                            guessedLetters = wordelUiState.guessedLetters
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun WordelGame(
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

            Keyboard(guessedLetters = guessedLetters)

            if (showEnter) {
                Keyboard(viewModel = viewModel)
            }
            if (showError && error != null) {
                Error(error = error)
            }
        }
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
    fun Victory() {
        HelloWordelTheme {
            viewModel.shareState.observe(this) {
                shareIntent(body = it.first, seed = it.second)
            }
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
    fun Keyboard(guessedLetters: List<HelloWordelViewModel.GuessedLetter>?) {
        Spacer(modifier = Modifier.size(16.dp))
        KeyboardLine(letters = keyboardLine1, guessedLetters = guessedLetters)
        KeyboardLine(letters = keyboardLine2, guessedLetters = guessedLetters)
        KeyboardLine(letters = keyboardLine3, guessedLetters = guessedLetters)
    }

    @Composable
    fun KeyboardLine(
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

    private fun shareIntent(body: String, seed: Int) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.action = Intent.ACTION_SEND
        val appUrl = getString(R.string.txt_app_url, seed)
        val send = String.format("%s\n%s", body, appUrl)
        shareIntent.putExtra(Intent.EXTRA_TEXT, send)
        shareIntent.type = "text/html"
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(shareIntent)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ButtonLabel(label = "Enter")
    }
}