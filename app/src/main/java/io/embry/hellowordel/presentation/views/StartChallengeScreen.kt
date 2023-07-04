package io.embry.hellowordel.presentation.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.embry.hellowordel.R
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.HelloWordelTheme
import io.embry.hellowordel.ui.theme.typography

@Composable
fun StartChallengeScreen(modifier: Modifier, seed: Int, viewModel: HelloWordelViewModel) {
    StartChallengeDialog(modifier = modifier, challengeDismissed = viewModel::challengeDismissed)
}


@Composable
private fun StartChallengeDialog(modifier: Modifier, challengeDismissed: (Boolean) -> Unit) {
    HelloWordelTheme {
        AlertDialog(modifier = modifier,
            onDismissRequest = {
                //not dismissible
            }, buttons =
            {
                OKButton(
                    onClick = { challengeDismissed.invoke(false) },
                    label = stringResource(id = R.string.btn_yes)
                )
                OKButton(
                    onClick = { challengeDismissed.invoke(true) },
                    label = stringResource(id = R.string.btn_no)
                )
            },
            title = {
                Text(
                    text = stringResource(id = R.string.txt_title_challenge),
                    style = typography.h1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.txt_title_challenge_body),
                    style = typography.h1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            })
    }
}