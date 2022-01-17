package io.embry.hellowordel.presentation.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.data.WordelState
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.HelloWordelTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HelloWordelViewModel by viewModels<HelloWordelViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWordelTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.setup()
                            viewModel.wordel.collect {
                                setContent {
                                    HelloWordel(wordelState = it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun HelloWordel(wordelState: WordelState) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WordelRow(state = wordelState.row1)
            WordelRow(state = wordelState.row2)
            WordelRow(state = wordelState.row3)
            WordelRow(state = wordelState.row4)
            WordelRow(state = wordelState.row5)
            WordelRow(state = wordelState.row6)
        }
    }

    @Composable
    fun WordelRow(state: RowState) {
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Tile(state = state.tile1)
            Tile(state = state.tile2)
            Tile(state = state.tile3)
            Tile(state = state.tile4)
            Tile(state = state.tile5)
        }
    }

    @Composable
    fun Tile(state: TileState) {
        Box(
            Modifier.size(48.dp),
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
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        HelloWordelTheme {
            //HelloWordel()
        }
    }
}