package io.embry.hellowordel.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.ui.theme.HelloWordelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloWordelTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    HelloWordel()
                }
            }
        }
    }
}

@Composable
fun HelloWordel() {
    Cells()
}

@Composable
fun Cells() {
    val firstRowState = RowState(
        tile1 = TileState(),
        tile2 = TileState(),
        tile3 = TileState(),
        tile4 = TileState(),
        tile5 = TileState(),
        tile6 = TileState()
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WordleRow(state = firstRowState)
        WordleRow(state = firstRowState)
        WordleRow(state = firstRowState)
        WordleRow(state = firstRowState)
        WordleRow(state = firstRowState)
        WordleRow(state = firstRowState)
    }
}

@Composable
fun WordleRow(state: RowState) {
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
        Tile(state = state.tile6)
    }
}

@Composable
fun Tile(state: TileState) {
    Box(
        Modifier.size(48.dp),
    ) {
        Card(
            border = BorderStroke(1.dp, Color.Black),
            backgroundColor = state.color,
            modifier = Modifier.size(48.dp),
            shape = RectangleShape,
            content = {}
        )
        TextField(
            value = state.text,
            onValueChange = {}
        )
    }
    Spacer(modifier = Modifier.size(16.dp))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HelloWordelTheme {
        HelloWordel()
    }
}