package io.embry.hellowordel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HelloWordelTheme {
        HelloWordel()
    }
}