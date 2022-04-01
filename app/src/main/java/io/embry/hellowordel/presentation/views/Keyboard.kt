package io.embry.hellowordel.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.R
import io.embry.hellowordel.presentation.viewmodels.HelloWordelViewModel
import io.embry.hellowordel.ui.theme.Teal200

@Composable
fun Keyboard(viewModel: HelloWordelViewModel) {
    ControlKeys(viewModel::enterPressed, viewModel::deletePressed)
}

@Composable
fun ControlKeys(enterPressed: () -> Unit, deletePressed: () -> Unit) {
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
                enterPressed.invoke()
            }) {
            ButtonLabel(label = stringResource(id = R.string.btn_enter))
        }
        Spacer(modifier = Modifier.size(4.dp))
        Button(
            modifier = Modifier
                .width(100.dp)
                .height(32.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Teal200
            ),
            onClick = {
                deletePressed.invoke()
            }) {
            ButtonLabel(label = stringResource(id = R.string.btn_del))
        }
    }
}
