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





/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////





// /**
//  * Runs the given action on the UI thread.
//  *
//  * This method is blocking until the action is complete.
//  */
// internal actual fun <T> runOnUiThread(action: () -> T): T {
//     return action()
// }
//
// /**
//  * Returns if the call is made on the main thread.
//  */
// internal actual fun isOnUiThread(): Boolean = true
//
// /**
//  * Throws an [UnsupportedOperationException].
//  */
// internal actual fun sleep(timeMillis: Long) {
//     throw UnsupportedOperationException("sleep is not supported in JS target")
// }
