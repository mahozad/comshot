package ir.mahozad.multiplatform.comshot

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.*
import android.view.*
import androidx.annotation.DoNotInline
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.*
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.graphics.HardwareRendererCompat
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

actual fun captureToImageeee(content: @Composable () -> Unit): ImageBitmap {
    error("Use the Activity.captureToImageeee instead")
}

private var composable: @Composable () -> Unit = @Composable {}
private var result: ImageBitmap? = null
private var condition = Semaphore(0)

fun Activity.captureToImageeee(comcon: CompositionContext, content: @Composable () -> Unit): ImageBitmap {
    // Use androidx.appstartup library to get context
    composable = content
    val intent = Intent(this, TheTempActivit::class.java)
    startActivityForResult(intent, -1)
    condition.acquire()
    return result!!
}

internal class TheTempActivit : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        val composeView = ComposeView(this)
        val recomposer = Recomposer(Dispatchers.Unconfined)
        composeView.setParentCompositionContext(recomposer)
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(object : Lifecycle() {
            override val currentState: State get() = State.RESUMED
            override fun addObserver(observer: LifecycleObserver) {}
            override fun removeObserver(observer: LifecycleObserver) {}
        }))
        composeView.createComposition()
        composeView.setContent(composable)

        // Triggers rendering of the composable; only needed for ComposeView; not needed for other view types like TextView
        // The max allowed size for widthBits + heightBits is 31 bits (30_000 requires 15 bit)
        addContentView(composeView, ViewGroup.LayoutParams(30_000, 30_000))
        // OR setContentView(composeView)

        composeView.measure(
            // OR to not constrain the image size: View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            View.MeasureSpec.makeMeasureSpec(15_000, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(15_000, View.MeasureSpec.AT_MOST),
        )
        composeView.layout(
            0,
            0,
            composeView.measuredWidth,
            composeView.measuredHeight
        )
        @OptIn(InternalComposeUiApi::class)
        composeView.showLayoutBounds = true
        println("width: ${composeView.measuredWidth} height: ${composeView.measuredHeight}")
        println("hasComposition: ${composeView.hasComposition}")

        val bitmap = Bitmap.createBitmap(
            composeView.measuredWidth,
            composeView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        // canvas.drawColor(0xFFFF5533.toInt())

        composeView.draw(canvas)
        result = bitmap.asImageBitmap()

        condition.release()
        finish()
    }
}

/**
 * Captures the underlying semantics node's surface into bitmap. This can be used to capture
 * nodes in a normal composable, a dialog if API >=28 and in a Popup. Note that the mechanism
 * used to capture the bitmap from a Popup is not the same as from a normal composable, since
 * a PopUp is in a different window.
 *
 * @throws IllegalArgumentException if we attempt to capture a bitmap of a dialog before API 28.
 */
@OptIn(ExperimentalTestApi::class)
@RequiresApi(Build.VERSION_CODES.O)
fun SemanticsNodeInteraction.captureToImage(): ImageBitmap {
    val node = fetchSemanticsNode("Failed to capture a node to bitmap.")
    // Validate we are in popup
    val popupParentMaybe = @Suppress("INVISIBLE_MEMBER") node.findClosestParentNode(includeSelf = true) {
        it.config.contains(SemanticsProperties.IsPopup)
    }
    if (popupParentMaybe != null) {
        return processMultiWindowScreenshot(node, @Suppress("INVISIBLE_MEMBER") testContext)
    }

    val view = (node.root as ViewRootForTest).view

    // If we are in dialog use its window to capture the bitmap
    val dialogParentNodeMaybe = @Suppress("INVISIBLE_MEMBER") node.findClosestParentNode(includeSelf = true) {
        it.config.contains(SemanticsProperties.IsDialog)
    }
    var dialogWindow: Window? = null
    if (dialogParentNodeMaybe != null) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // TODO(b/163023027)
            throw IllegalArgumentException("Cannot currently capture dialogs on API lower than 28!")
        }

        dialogWindow = findDialogWindowProviderInParent(view)?.window
            ?: throw IllegalArgumentException(
                "Could not find a dialog window provider to capture its bitmap"
            )
    }

    val windowToUse = dialogWindow ?: view.context.getActivityWindow()

    val nodeBounds = node.boundsInRoot
    val nodeBoundsRect = Rect(
        nodeBounds.left.roundToInt(),
        nodeBounds.top.roundToInt(),
        nodeBounds.right.roundToInt(),
        nodeBounds.bottom.roundToInt()
    )

    val locationInWindow = intArrayOf(0, 0)
    view.getLocationInWindow(locationInWindow)
    val x = locationInWindow[0]
    val y = locationInWindow[1]

    // Now these are bounds in window
    nodeBoundsRect.offset(x, y)

    return windowToUse.captureRegionToImage(@Suppress("INVISIBLE_MEMBER") testContext, nodeBoundsRect)
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun Window.captureRegionToImage(
    testContext: TestContext,
    boundsInWindow: Rect,
): ImageBitmap {
    // Turn on hardware rendering, if necessary
    return withDrawingEnabled {
        // First force drawing to happen
        decorView.forceRedraw(testContext)
        // Then we generate the bitmap
        generateBitmap(boundsInWindow).asImageBitmap()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Window.generateBitmap(boundsInWindow: Rect): Bitmap {
    val destBitmap =
        Bitmap.createBitmap(
            boundsInWindow.width(),
            boundsInWindow.height(),
            Bitmap.Config.ARGB_8888
        )
    generateBitmapFromPixelCopy(boundsInWindow, destBitmap)
    return destBitmap
}

internal fun View.forceRedraw(testContext: TestContext) {
    var drawDone = false
    handler.post {
        if (Build.VERSION.SDK_INT >= 29 && isHardwareAccelerated) {
            FrameCommitCallbackHelper.registerFrameCommitCallback(viewTreeObserver) {
                drawDone = true
            }
        } else {
            viewTreeObserver.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
                var handled = false
                override fun onDraw() {
                    if (!handled) {
                        handled = true
                        handler.postAtFrontOfQueue {
                            drawDone = true
                            viewTreeObserver.removeOnDrawListener(this)
                        }
                    }
                }
            })
        }
        invalidate()
    }

    @OptIn(InternalTestApi::class)
    @Suppress("INVISIBLE_MEMBER")
    testContext.testOwner.mainClock.waitUntil(timeoutMillis = 2_000) { drawDone }
}

// Unfortunately this is a copy paste from AndroidComposeTestRule. At this moment it is a bit
// tricky to share this method. We can expose it on TestOwner in theory.
private fun MainTestClock.waitUntil(timeoutMillis: Long, condition: () -> Boolean) {
    val startTime = System.nanoTime()
    while (!condition()) {
        if (autoAdvance) {
            advanceTimeByFrame()
        }
        // Let Android run measure, draw and in general any other async operations.
        Thread.sleep(10)
        if (System.nanoTime() - startTime > timeoutMillis * 1_000_000) {
            throw ComposeTimeoutException(
                "Condition still not satisfied after $timeoutMillis ms"
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private object FrameCommitCallbackHelper {
    @DoNotInline
    fun registerFrameCommitCallback(viewTreeObserver: ViewTreeObserver, runnable: Runnable) {
        viewTreeObserver.registerFrameCommitCallback(runnable)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Window.generateBitmapFromPixelCopy(boundsInWindow: Rect, destBitmap: Bitmap) {
    val latch = CountDownLatch(1)
    var copyResult = 0
    val onCopyFinished = PixelCopy.OnPixelCopyFinishedListener { result ->
        copyResult = result
        latch.countDown()
    }
    PixelCopyHelper.request(
        this,
        boundsInWindow,
        destBitmap,
        onCopyFinished,
        Handler(Looper.getMainLooper())
    )

    if (!latch.await(1, TimeUnit.SECONDS)) {
        throw AssertionError("Failed waiting for PixelCopy!")
    }
    if (copyResult != PixelCopy.SUCCESS) {
        throw AssertionError("PixelCopy failed with result $copyResult!")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private object PixelCopyHelper {
    @DoNotInline
    fun request(
        source: Window,
        srcRect: Rect?,
        dest: Bitmap,
        listener: PixelCopy.OnPixelCopyFinishedListener,
        listenerThread: Handler
    ) {
        PixelCopy.request(source, srcRect, dest, listener, listenerThread)
    }
}

private fun <R> withDrawingEnabled(block: () -> R): R {
    val wasDrawingEnabled = HardwareRendererCompat.isDrawingEnabled()
    try {
        if (!wasDrawingEnabled) {
            HardwareRendererCompat.setDrawingEnabled(true)
        }
        return block.invoke()
    } finally {
        if (!wasDrawingEnabled) {
            HardwareRendererCompat.setDrawingEnabled(false)
        }
    }
}

@ExperimentalTestApi
@RequiresApi(Build.VERSION_CODES.O)
private fun processMultiWindowScreenshot(
    node: SemanticsNode,
    testContext: TestContext
): ImageBitmap {

    (node.root as ViewRootForTest).view.forceRedraw(testContext)

    val nodePositionInScreen = findNodePosition(node)
    val nodeBoundsInRoot = node.boundsInRoot

    val combinedBitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()

    val finalBitmap = Bitmap.createBitmap(
        combinedBitmap,
        (nodePositionInScreen.x + nodeBoundsInRoot.left).roundToInt(),
        (nodePositionInScreen.y + nodeBoundsInRoot.top).roundToInt(),
        nodeBoundsInRoot.width.roundToInt(),
        nodeBoundsInRoot.height.roundToInt()
    )
    return finalBitmap.asImageBitmap()
}

private fun findNodePosition(
    node: SemanticsNode
): Offset {
    val view = (node.root as ViewRootForTest).view
    val locationOnScreen = intArrayOf(0, 0)
    view.getLocationOnScreen(locationOnScreen)
    val x = locationOnScreen[0]
    val y = locationOnScreen[1]

    return Offset(x.toFloat(), y.toFloat())
}

internal fun findDialogWindowProviderInParent(view: View): DialogWindowProvider? {
    if (view is DialogWindowProvider) {
        return view
    }
    val parent = view.parent ?: return null
    if (parent is View) {
        return findDialogWindowProviderInParent(parent)
    }
    return null
}

private fun Context.getActivityWindow(): Window {
    fun Context.getActivity(): Activity {
        return when (this) {
            is Activity -> this
            is ContextWrapper -> this.baseContext.getActivity()
            else -> throw IllegalStateException(
                "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
                        "An Activity context is required to get a Window instance"
            )
        }
    }
    return getActivity().window
}
