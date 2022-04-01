package io.embry.hellowordel.presentation.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel

@Composable
fun GameScreen(viewModel: HelloWordelViewModel) {
}

@Composable
private fun GameScreen() {
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
