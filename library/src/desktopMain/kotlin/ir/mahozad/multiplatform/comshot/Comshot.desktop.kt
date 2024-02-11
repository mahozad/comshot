package ir.mahozad.multiplatform.comshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runSkikoComposeUiTest

// private val surface = Surface.makeRasterN32Premul(800, 600)
// @OptIn(InternalComposeUiApi::class)
// private lateinit var scene: ComposeScene
//
// @OptIn(InternalComposeUiApi::class)
// private fun render(timeMillis: Long) {
//     scene.render(
//         surface.canvas.asComposeCanvas(),
//         timeMillis * NanoSecondsPerMilliSecond
//     )
// }
//
// @OptIn(InternalComposeUiApi::class)
// private fun createUi() = MultiLayerComposeScene(
//     layoutDirection = LayoutDirection.Ltr,
//     invalidate = { render(0) },
//     // density = Density(1f),
//     // coroutineContext = Dispatchers.Unconfined,
//     // composeSceneContext = TestComposeSceneContext(),
// ).also {
//     it.boundsInWindow = IntSize(600, 400).toIntRect()
// }
//
// @OptIn(InternalComposeUiApi::class)
// actual fun initializeeeeeee() {
//     scene = createUi()
// }
//
// @OptIn(InternalComposeUiApi::class)
// actual fun setContenttt(content: @Composable () -> Unit) {
//     scene.setContent(content)
// }

@OptIn(ExperimentalTestApi::class)
actual fun captureToImageeee(content: @Composable () -> Unit): ImageBitmap {
    // waitForIdle()
    var image: ImageBitmap? = null
    runSkikoComposeUiTest {
        setContent(content)
        image = captureToImage()
    }
    return image!!
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    // return renderComposeScene(300, 300) {
    //     content()
    // }.toComposeImageBitmap()
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    // val scene = ImageComposeScene(
    //     width = 300, // Or whatever you need
    //     height = 300, // Or whatever you need
    //     // density = Density(1f), // Or whatever you need
    //     // coroutineContext = Dispatchers.Unconfined,
    // ) {
    //     content()
    // }
    // val img = scene.render()
    // return Bitmap.makeFromImage(img).asComposeImageBitmap()
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    // MultiLayerComposeScene(
    //     layoutDirection = LayoutDirection.Ltr,
    //     invalidate = { render(0) },
    //     // density = Density(1f),
    //     // coroutineContext = Dispatchers.Unconfined,
    //     // composeSceneContext = TestComposeSceneContext()
    // ).apply {
    //     boundsInWindow = IntSize(surface.width, surface.height).toIntRect()
    // }
}





/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////





// import java.util.concurrent.ExecutionException
// import java.util.concurrent.FutureTask
// import javax.swing.SwingUtilities
//
// /**
//  * Runs the given action on the UI thread.
//  *
//  * This method is blocking until the action is complete.
//  */
// internal actual fun <T> runOnUiThread(action: () -> T): T {
//     return if (isOnUiThread()) {
//         action()
//     } else {
//         val task: FutureTask<T> = FutureTask(action)
//         SwingUtilities.invokeAndWait(task)
//         try {
//             return task.get()
//         } catch (e: ExecutionException) { // Expose the original exception
//             throw e.cause!!
//         }
//     }
// }
//
// /**
//  * Returns if the call is made on the main thread.
//  */
// internal actual fun isOnUiThread(): Boolean {
//     return SwingUtilities.isEventDispatchThread()
// }
//
// /**
//  * Blocks the calling thread for [timeMillis] milliseconds.
//  */
// internal actual fun sleep(timeMillis: Long) {
//     Thread.sleep(timeMillis)
// }
