import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("com.android.application")
  kotlin("android")
  id("org.jetbrains.compose") version Compose.desktopVersion
  id("org.jetbrains.kotlin.plugin.compose") version Kotlin.version
  id("io.github.takahirom.roborazzi")
}

android {
  namespace = "com.zachklipp.richtext.sample"
  compileSdk = AndroidConfiguration.compileSdk

  defaultConfig {
    minSdk = AndroidConfiguration.minSdk
    targetSdk = AndroidConfiguration.targetSdk
  }

  buildFeatures {
    compose = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all { test ->
        test.systemProperty("robolectric.pixelCopyRenderMode", "hardware")
      }
    }
  }
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_11
  }
}

dependencies {
  implementation(project(":richtext-commonmark"))
  implementation(project(":richtext-ui-material3"))
  implementation(AndroidX.appcompat)
  implementation(Compose.activity)
  implementation(compose.foundation)
  implementation(compose.materialIconsExtended)
  implementation(compose.material3)
  implementation(compose.uiTooling)

  testImplementation("androidx.compose.ui:ui-test-junit4:1.8.2")
  testImplementation("androidx.compose.ui:ui-test-manifest:1.8.2")
  testImplementation("io.github.sergio-sastre.ComposablePreviewScanner:android:0.8.1")
  testImplementation("io.github.takahirom.roborazzi:roborazzi:1.59.0")
  testImplementation("io.github.takahirom.roborazzi:roborazzi-compose:1.59.0")
  testImplementation("io.github.takahirom.roborazzi:roborazzi-compose-preview-scanner-support:1.59.0")
  testImplementation("junit:junit:4.13.2")
  testImplementation("org.robolectric:robolectric:4.16.1")
}

roborazzi {
  @OptIn(ExperimentalRoborazziApi::class)
  generateComposePreviewRobolectricTests {
    enable = true
    packages = listOf("com.zachklipp.richtext.sample")
    includePrivatePreviews = true
    robolectricConfig = mapOf(
      "sdk" to "[36]",
      "qualifiers" to "RobolectricDeviceQualifiers.Pixel5",
    )
  }
}
