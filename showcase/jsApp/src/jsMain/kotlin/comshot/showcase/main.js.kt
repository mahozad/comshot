package comshot.showcase

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    console.log("Hello, Kotlin/JS!")
    onWasmReady {
        CanvasBasedWindow(title = "Comshot showcase") {
            MainView()
        }
    }
}
