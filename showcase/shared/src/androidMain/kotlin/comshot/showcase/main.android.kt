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
import ir.mahozad.multiplatform.comshot.captureToImageeee
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Activity.MainView() {
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var i by remember { mutableIntStateOf(1) }
    val composable: @Composable () -> Unit = remember {
        @Composable {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "!".repeat(i))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.photo_by_lucas_chizzali_on_unsplash),
                        modifier = Modifier.size(200.dp),
                        contentDescription = null
                    )
                    Text("Photo by Lucas Chizzali on Unsplash")
                    Button({}) { Text("Example button $i") }
                }
            }
        }
    }
    Column {
        val compositionContext = rememberCompositionContext()
        Button(
            onClick = {
                image = captureToImageeee(compositionContext, composable)
                i++
            }
        ) {
            Text("take")
        }
        image?.let { Image(it, contentDescription = null) }
    }
}
