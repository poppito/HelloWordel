package io.embry.hellowordel.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.R
import io.embry.hellowordel.data.RowState
import io.embry.hellowordel.data.TilePosition
import io.embry.hellowordel.data.TileState
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.Approximate
import io.embry.hellowordel.ui.theme.Correct
import io.embry.hellowordel.ui.theme.FilledText
import io.embry.hellowordel.ui.theme.HelloWordelTheme
import io.embry.hellowordel.ui.theme.Incorrect
import io.embry.hellowordel.ui.theme.typography


@Composable
fun HelpScreen(viewModel: HelloWordelViewModel) {
    HelloWordelTheme {
        AlertDialog(
            onDismissRequest = { viewModel.onHelpDismissed() },
            buttons = {
                OKButton(
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
private fun HelpInfo() {
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
            ),
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
            ),
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
            ),
            animate = false
        )
        Text(
            text = stringResource(id = R.string.txt_help_dialog_body5),
            style = typography.body2,
            textAlign = TextAlign.Center
        )
    }
}