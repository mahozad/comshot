package ir.mahozad.multiplatform.comshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runSkikoComposeUiTest

@OptIn(ExperimentalTestApi::class)
actual fun captureToImage(composable: @Composable () -> Unit): ImageBitmap {
    // waitForIdle()
    var image: ImageBitmap? = null
    runSkikoComposeUiTest {
        setContent(composable)
        image = captureToImage()
    }
    return image!!
}
