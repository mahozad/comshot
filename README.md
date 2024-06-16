[![Kotlin version]][Kotlin release]
[![Compose Multiplatform version]][Compose Multiplatform release]
[![Latest Maven Central release]][Library on Maven Central]

<br>

<div align="center">
    <img src="asset/logo-optimized.svg" width="100px"/>
</div>

# Comshot
Multiplatform library to take/capture screenshot/snapshot/picture/image of composables (and also Android Views).

⚠️ This is very experimental and tested only on Windows and Android.

## Usage

```kotlin
implementation("ir.mahozad.multiplatform:comshot:0.1.0")
```

  - Android (Composable)  
    You need to pass your activity to `captureToImage`. Also, it should be called from the Main aka UI thread (see its KDoc for more information).
    ```kotlin
    @Composable
    fun Activity.MyComposable() {
        val activity = this
        var screenshot by remember { mutableStateOf<ImageBitmap?>(null) }
        val composable: @Composable () -> Unit = remember {
            @Composable {
                Row {
                    Text(text = "Hello")
                    Text(text = "Meow!")
                }
            }
        }
        // You can also render your composable simply like this:
        // composable()
        Column {
            Button(onClick = { screenshot = captureToImage(activity, composable) }) {
                Text(text = "Capture")
            }
            screenshot?.let {
                Image(
                    bitmap = it,
                    modifier = Modifier.width(200.dp),
                    contentDescription = null
                )
            }
        }
    }
    ```
  - Android (View)
    ```kotlin
    val view = findViewById<TextView>(R.id.myTextView)
    val screenshot = captureToImage(view)
    // If you want Bitmap:
    val androidBitmap = screenshot.asAndroidBitmap()
    ```
 - Other targets
   ```kotlin
   @Composable
   fun MyComposable() {
       var screenshot by remember { mutableStateOf<ImageBitmap?>(null) }
       val composable: @Composable () -> Unit = remember {
           @Composable {
               Row {
                   Text(text = "Hello")
                   Text(text = "Meow!")
               }
           }
       }
       // You can also render your composable simply like this:
       // composable()
       Column {
           Button(onClick = { screenshot = captureToImage(composable) }) {
               Text(text = "Capture")
           }
           screenshot?.let {
               Image(
                   bitmap = it,
                   modifier = Modifier.width(200.dp),
                   contentDescription = null
               )
           }
       }
   }
   ```

## Related
  - Capturable : https://github.com/PatilShreyas/Capturable
  - Compose ScreenshotBox: https://github.com/SmartToolFactory/Compose-Screenshot
  - Generate bitmap from composable: https://github.com/JohannRosenberg/bitmap-from-composable
  - Generate JPEG from composable: https://github.com/Vipul12Thawre/JetCapture
  - https://developer.android.com/studio/preview/compose-screenshot-testing
  - https://developer.android.com/reference/androidx/test/espresso/screenshot/ViewInteractionCapture

[Kotlin version]: https://img.shields.io/badge/Kotlin-1.9.22-303030.svg?labelColor=303030&logo=data:image/svg+xml;base64,PHN2ZyB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAxOC45MyAxOC45MiIgd2lkdGg9IjE4IiBoZWlnaHQ9IjE4IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgogIDxyYWRpYWxHcmFkaWVudCBpZD0iZ3JhZGllbnQiIHI9IjIxLjY3OSIgY3g9IjIyLjQzMiIgY3k9IjMuNDkzIiBncmFkaWVudFRyYW5zZm9ybT0ibWF0cml4KDEgMCAwIDEgLTQuMTMgLTIuNzE4KSIgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiPgogICAgPHN0b3Agc3RvcC1jb2xvcj0iI2U0NDg1NyIgb2Zmc2V0PSIuMDAzIi8+CiAgICA8c3RvcCBzdG9wLWNvbG9yPSIjYzcxMWUxIiBvZmZzZXQ9Ii40NjkiLz4KICAgIDxzdG9wIHN0b3AtY29sb3I9IiM3ZjUyZmYiIG9mZnNldD0iMSIvPgogIDwvcmFkaWFsR3JhZGllbnQ+CiAgPHBhdGggZmlsbD0idXJsKCNncmFkaWVudCkiIGQ9Ik0gMTguOTMsMTguOTIgSCAwIFYgMCBIIDE4LjkzIEwgOS4yNyw5LjMyIFoiLz4KPC9zdmc+Cg==
[Compose Multiplatform version]: https://img.shields.io/badge/Compose_Multiplatform-1.6.0_beta02-303030.svg?labelColor=303030&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAACXBIWXMAAA7DAAAOwwHHb6hkAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAj5JREFUOI2Vk0FIVFEUhv9znBllplBIF7loK1jtJKhFNG/EVtYicNkmKghCMpJGq0HoPcWQVi2KUMqdixaJi0KdXVBILQojs4wCaTGC4LyX+N47fwtFpnEKOnDh3p//fudeDr+QRK3KukGHCscAwCjXi4PphVo+qQZkhzaa61J6m8RhAfpisS01HQOwZin0F29kftYEdDxCsqnkX6HgIonR+YHM00pjzg26oXRBPrNw30ixgM1dgDMcnFFyyIAphpn7xQI2Tw6XW5LQO0L+isPQKxaa1rNDaJCkf02BHhMpzOfTzxUA1GyCxEcFxjcOIu50/b4kZQnkZQJ9mkwuOV5wqaUdYSIhTwBZFto4AOj2R+S7qEwZMNtU8lcoGAPximZHDegAsCjgw7XP/rJFnDHBhEB+AABIIueW35FEdsQ/67hl5jz/AklUrpxX7nfcMp27wYnKO/rHCAwhANDkffW4DPJhZxtV6lpt/N+qCRCND+3RDHs0AEhUHii6KIxXSZnq9PxJTUhetrQ+VrsH4TlAvlgUfd3zAgMau0aD1uLNhm8WBm0CjBDoiSN8ijReJHBaRAYtTB8pFvaXukaDVgMadwFC6bWIM47n54GWaHYgM5CwunaASwBe1yXQNptPewDgeH7eIs4IpXcXMDeYnl5vzhxTINCUv+B4/vkXtxpWQEwK8Phlf3o15wbdmvLfCFgfh5njc4Pp6e3mVWHqHN44AOidnTC9NVpJRE+BKP0zTNW1HWc8IMxIvfq3OP8GvjkzgYHHZZMAAAAASUVORK5CYII=
[Latest Maven Central release]: https://img.shields.io/maven-central/v/ir.mahozad.multiplatform/comshot?label=Maven%20Central&labelColor=303030&logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZlcnNpb249IjEuMSIgdmlld0JveD0iMCAwIDE2IDE2IiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciPgogIDxkZWZzPgogICAgPHN0eWxlPi5he2ZpbGw6bm9uZTt9LmJ7Y2xpcC1wYXRoOnVybCgjYSk7fS5je2ZpbGw6I2ZmZjt9PC9zdHlsZT4KICAgIDxjbGlwUGF0aCBpZD0iYSI+CiAgICAgIDxyZWN0IGNsYXNzPSJhIiB4PSIxNC43IiB5PSIxMSIgd2lkdGg9IjE3MSIgaGVpZ2h0PSIxNTEiLz4KICAgIDwvY2xpcFBhdGg+CiAgICA8Y2xpcFBhdGggaWQ9ImNsaXBQYXRoMTMiPgogICAgICA8cmVjdCBjbGFzcz0iYSIgeD0iMTQuNyIgeT0iMTEiIHdpZHRoPSIxNzEiIGhlaWdodD0iMTUxIi8+CiAgICA8L2NsaXBQYXRoPgogIDwvZGVmcz4KICA8cGF0aCBjbGFzcz0iYyIgdHJhbnNmb3JtPSJtYXRyaXgoLjE2NCAwIDAgLjE2NCAtOC4zNyAtMS44MSkiIGQ9Im0xMDAgMTEtNDIuMyAyNC40djQ4LjlsNDIuMyAyNC40IDQyLjMtMjQuNHYtNDguOXptMzAuMiA2Ni4zLTMwLjIgMTcuNC0zMC4yLTE3LjR2LTM0LjlsMzAuMi0xNy40IDMwLjIgMTcuNHoiIGNsaXAtcGF0aD0idXJsKCNjbGlwUGF0aDEzKSIvPgo8L3N2Zz4K
[Kotlin release]: https://github.com/JetBrains/kotlin/releases/tag/v1.9.22
[Compose Multiplatform release]: https://github.com/JetBrains/compose-multiplatform/releases/tag/v1.6.0-beta02
[Library on Maven Central]: https://repo1.maven.org/maven2/ir/mahozad/multiplatform/comshot/0.1.0/
