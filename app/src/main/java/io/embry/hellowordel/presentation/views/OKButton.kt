package io.embry.hellowordel.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.embry.hellowordel.ui.theme.Teal200


@Composable
fun OKButton(onClick: () -> Unit, label: String) {
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