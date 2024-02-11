// Based on https://github.com/JetBrains/compose-multiplatform-core/blob/245896948b2c3716022312242691a0843ca131df/compose/ui/ui-test/src/skikoMain/kotlin/androidx/compose/ui/test/ComposeUiTest.skikoMain.kt

// Relevant Code:
// https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/scene/ComposeScene.skiko.kt
// https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui-test/src/commonMain/kotlin/androidx/compose/ui/test/ComposeUiTest.kt
// https://docs.google.com/document/d/1e7IlnBkJ5w6vJV5LqnXFPCGvomTdDxdXZRbfyBXQYsM/edit
// https://github.com/JetBrains/compose-multiplatform/issues/4167
// https://github.com/JetBrains/compose-multiplatform-core/pull/589
// https://developer.android.com/jetpack/compose/migrate/interoperability-apis/compose-in-views
// https://github.com/JetBrains/compose-multiplatform/issues/3972#event-11759307640
// The compose-multiplatform-core implementation for Android:
// https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui-test/src/androidMain/kotlin/androidx/compose/ui/test/AndroidImageHelpers.android.kt
// https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/ui/ui-test/src/androidMain/kotlin/androidx/compose/ui/test/android/WindowCapture.android.kt

// Similar libraries:
// https://github.com/PatilShreyas/Capturable
// https://github.com/SmartToolFactory/Compose-Screenshot
// https://github.com/JohannRosenberg/bitmap-from-composable
// https://github.com/Vipul12Thawre/JetCapture

// StackOverflow Posts:
// https://stackoverflow.com/questions/63861095/jetpack-compose-take-screenshot-of-composable-function
// https://stackoverflow.com/questions/74450838/convert-a-composable-view-into-image-in-jetpack-compose
// https://stackoverflow.com/questions/76606244/how-to-take-a-full-screenshot-capture-in-android-compose
// https://stackoverflow.com/questions/76283419/using-jetpack-compose-desktop-how-can-i-render-to-an-image-without-a-window
// https://stackoverflow.com/questions/53529300/rendering-to-an-offscreen-view-and-saving-a-screenshot
// https://stackoverflow.com/questions/68355969/take-screenshot-of-a-composable-fun-programmatically-in-jetpack-compose
// https://stackoverflow.com/questions/69388898/taking-screenshot-of-webview-androidview-in-jetpack-compose
// https://stackoverflow.com/questions/72802387/compose-view-instead-of-native-view-for-markers-info-window
// https://stackoverflow.com/questions/73294041/crash-when-trying-to-measure-a-composeview-to-generate-a-bitmap-of-it
// https://stackoverflow.com/questions/70944722/composable-to-bitmap-without-displaying-it
// https://stackoverflow.com/questions/75461048/how-to-renderor-convert-a-composable-to-image-in-compose-desktop
// https://stackoverflow.com/questions/5604125/android-taking-screenshot-of-offscreen-page
// https://stackoverflow.com/questions/7854664/render-view-to-bitmap-off-screen-in-android
// https://stackoverflow.com/questions/4346710/bitmap-from-view-not-displayed-on-android
// https://stackoverflow.com/questions/1240967/draw-inflated-layout-xml-on-an-bitmap
// https://stackoverflow.com/questions/9062015/create-bitmap-of-layout-that-is-off-screen
// https://stackoverflow.com/questions/2661536/how-to-programmatically-take-a-screenshot-on-android
// https://stackoverflow.com/questions/29797590/android-webview-offscreen-rendering
// https://stackoverflow.com/questions/14631682/creating-and-using-a-fragments-view-whilst-not-visible-to-the-user
// https://stackoverflow.com/questions/11198872/execute-code-on-main-thread-in-android-without-access-to-an-activity

/**
 * The compose-multiplatform-core/compose/ui/ui-test-junit4 depends on compose-multiplatform-core/compose/ui/ui-test
 * The Desktop, iOS, and Web depend on a SkikoMain source set for their same implementation.
 * The Android has a different implementation.
 */

package ir.mahozad.multiplatform.comshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

expect fun captureToImage(composable: @Composable () -> Unit): ImageBitmap
