package comshot.showcase

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import comshot.showcase.shared.generated.resources.Res
import ir.mahozad.multiplatform.comshot.captureToImage
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Activity.MainView() {
    // To check UI responsiveness
    var counter by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            counter++
            delay(100.milliseconds)
        }
    }
    var time by remember { mutableStateOf<Duration?>(null) }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var padding by remember { mutableIntStateOf(0) }
    val composable: @Composable () -> Unit = remember {
        @Composable {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "!".repeat(padding))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.photo_by_lucas_chizzali_on_unsplash),
                        modifier = Modifier.size(200.dp),
                        contentDescription = null
                    )
                    Text("Photo by Lucas Chizzali on Unsplash")
                    Button({}) { Text("Example button $padding") }
                }
            }
        }
    }
    Column {
        Text(text = "Counter to check responsiveness: $counter")
        Button(
            onClick = {
                val timedValue = measureTimedValue { captureToImage(this@MainView, composable) }
                time = timedValue.duration
                image = timedValue.value
                padding++
            }
        ) {
            Text(text = "Capture ${if (time != null) "(Last one took $time)" else ""}")
        }
        image?.let { Image(it, contentDescription = null) }
    }
}
