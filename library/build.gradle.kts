import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import java.util.*

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compatibility)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    id("maven-publish")
    id("signing")
}

buildscript {
    dependencies {
        val dokkaVersion = libs.versions.dokka.get()
        classpath("org.jetbrains.dokka:dokka-base:$dokkaVersion")
    }
}

group = "ir.mahozad.multiplatform"
version = "0.1.0-SNAPTHOT"

// See https://central.sonatype.com/namespace/org.jetbrains.compose.material
// for the targets that Compose Multiplatform supports
kotlin {
    androidTarget { publishLibraryVariants("release") }
    // Windows, Linux, macOS (with Java runtime)
    jvm(name = "desktop" /* Renames jvm to desktop */)
    // Kotlin/JS drawing to a canvas
    js(compiler = IR) {
        nodejs()
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    // Building and publishing for iOS target requires a machine running macOS;
    // otherwise, the .klib will not be produced and the compiler warns about that.
    // See https://kotlinlang.org/docs/multiplatform-mobile-understand-project-structure.html#ios-framework
    listOf(
        // By declaring these targets, the iosMain source set will be created automatically
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "library"
            isStatic = true
        }
    }

    // Native targets:
    // macosX64()
    // macosArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(compose.runtime)
            implementation(compose.ui)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.skiko)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.lifecycle)
            }
        }
        val androidUnitTest by getting {}
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.junit)
                implementation(libs.junit4)
            }
        }
        val desktopMain by getting {
            dependencies {
                // api(libs.androidx.ui.desktop)
                // implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.92")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                // Needed because of build errors; remove if tests run
                implementation(compose.desktop.windows_x64)
            }
        }
        val jsMain by getting {}
        val jsTest by getting {}
        val wasmJsMain by getting {}
        val wasmJsTest by getting {}
        val iosX64Main by getting {}
        val iosArm64Main by getting {}
        val iosSimulatorArm64Main by getting {}
        // See above
        // val macosArm64Main by getting {}
        // val macosX64Main by getting {}
    }
}

android {
    namespace = "ir.mahozad.multiplatform"

    defaultConfig {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
}

tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap(DokkaTask::outputDirectory))
    archiveClassifier = "javadoc"
}

tasks.dokkaHtml {
    // outputDirectory = layout.buildDirectory.get().resolve("dokka")
    offlineMode = false
    moduleName = "Comshot"

    // See the buildscript block above and also
    // https://github.com/Kotlin/dokka/issues/2406
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        customAssets = listOf(file("../asset/logo-optimized.svg"))
        customStyleSheets = listOf(file("../asset/dokka-styles.css"))
        separateInheritedMembers = true
    }

    dokkaSourceSets {
        configureEach {
            reportUndocumented = true
            noAndroidSdkLink = false
            noStdlibLink = false
            noJdkLink = false
            jdkVersion = libs.versions.java.get().toInt()
            // sourceLink {
            //     // Unix based directory relative path to the root of the project (where you execute gradle respectively).
            //     // localDirectory.set(file("src/main/kotlin"))
            //     // URL showing where the source code can be accessed through the web browser
            //     // remoteUrl = uri("https://github.com/mahozad/${project.name}/blob/main/${project.name}/src/main/kotlin").toURL()
            //     // Suffix which is used to append the line number to the URL. Use #L for GitHub
            //     remoteLineSuffix = "#L"
            // }
        }
    }
}

val properties = Properties().apply {
    runCatching { rootProject.file("local.properties") }
        .getOrNull()
        .takeIf { it?.exists() ?: false }
        ?.reader()
        ?.use(::load)
}
// For information about signing.* properties,
// see comments on signing { ... } block below
val environment: Map<String, String?> = System.getenv()
extra["ossrhUsername"] = properties["ossrh.username"] as? String
    ?: environment["OSSRH_USERNAME"] ?: ""
extra["ossrhPassword"] = properties["ossrh.password"] as? String
    ?: environment["OSSRH_PASSWORD"] ?: ""
extra["githubToken"] = properties["github.token"] as? String
    ?: environment["GITHUB_TOKEN"] ?: ""

publishing {
    repositories {
        maven {
            name = "CustomLocal"
            url = uri("file://${layout.buildDirectory.get()}/local-repository")
        }
        maven {
            name = "MavenCentral"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = extra["ossrhUsername"]?.toString()
                password = extra["ossrhPassword"]?.toString()
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mahozad/${project.name}")
            credentials {
                username = "mahozad"
                password = extra["githubToken"]?.toString()
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(javadocJar) // Required a workaround. See below
        pom {
            url = "https://mahozad.ir/${project.name}"
            name = project.name
            description = """
                Multiplatform library to capture image of Composables (and also Android Views).
            """.trimIndent()
            inceptionYear = "2024"
            licenses {
                license {
                    name = "Apache-2.0 License"
                    url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                }
            }
            developers {
                developer {
                    id = "mahozad"
                    name = "Mahdi Hosseinzadeh"
                    url = "https://mahozad.ir/"
                    email = ""
                    roles = listOf("Lead Developer")
                    timezone = "GMT+4:30"
                }
            }
            contributors {
                // contributor {}
            }
            scm {
                tag = "HEAD"
                url = "https://github.com/mahozad/${project.name}"
                connection = "scm:git:github.com/mahozad/${project.name}.git"
                developerConnection = "scm:git:ssh://github.com/mahozad/${project.name}.git"
            }
            issueManagement {
                system = "GitHub"
                url = "https://github.com/mahozad/${project.name}/issues"
            }
            ciManagement {
                system = "GitHub Actions"
                url = "https://github.com/mahozad/${project.name}/actions"
            }
        }
    }
}

// TODO: Remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
//  Thanks to KSoup repository for this code snippet
tasks.withType(AbstractPublishToMaven::class).configureEach {
    dependsOn(tasks.withType(Sign::class))
}

/*
 * Uses signing.* properties defined in gradle.properties in ~/.gradle/ or project root
 * Can also pass from command line like below
 * ./gradlew task -Psigning.secretKeyRingFile=... -Psigning.password=... -Psigning.keyId=...
 * See https://docs.gradle.org/current/userguide/signing_plugin.html
 * and https://stackoverflow.com/a/67115705
 */
signing {
    sign(publishing.publications)
}
