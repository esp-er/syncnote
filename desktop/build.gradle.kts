import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.1"
}

group = "com.raywenderlich.jetnotes"
version = "1.0.0"

// 3
kotlin {
    jvm { //Set up jvm target
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    //sources and resources for the jvm
    sourceSets {

        val jvmMain by getting {

            kotlin.srcDirs("src/jvmMain/kotlin")
            dependencies {

                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
                implementation("io.insert-koin:koin-core:${rootProject.ext["koin_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)
                api(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
            }
        }
    }
}

tasks.named<Copy>("jvmProcessResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi"
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi,
                TargetFormat.Deb)
            packageName = "OpenNotes"
            macOS {
                bundleID = "com.raywenderlich.jetnotes"
            }
        }
    }
}