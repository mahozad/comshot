package comshot.showcase

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        title = "Comshot showcase",
        onCloseRequest = ::exitApplication
    ) {
        MainView()
    }
}
