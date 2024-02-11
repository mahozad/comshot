package ir.mahozad.multiplatform.comshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runSkikoComposeUiTest

@OptIn(ExperimentalTestApi::class)
actual fun captureToImageeee(content: @Composable () -> Unit): ImageBitmap {
    // waitForIdle()
    var image: ImageBitmap? = null
    runSkikoComposeUiTest {
        setContent(content)
        image = captureToImage()
    }
    return image!!
}
