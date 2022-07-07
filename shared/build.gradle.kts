plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("org.jetbrains.compose") version "1.1.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"

}

version = "1.0"

kotlin {
    android() //Define android target

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.extra["ktor_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
                api(compose.foundation)
                api(compose.runtime)
                api(compose.material)
                api(compose.ui)
                api(compose.uiTooling)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-rc01")
                implementation("com.squareup.sqldelight:android-driver:${rootProject.extra["sqldelight_version"]}")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
                implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.4.1")
                implementation("androidx.appcompat:appcompat:1.4.1")


                //Ktor
                implementation("io.ktor:ktor-client-cio:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-core:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-android:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-websockets:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-content-negotiation:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.ext["ktor_version"]}")

            }
            val androidTest by getting {
                dependencies {
                    implementation(kotlin("test-junit"))
                    implementation("junit:junit:4.13.2")
                }
            }
            val desktopMain by getting {
                dependsOn(commonMain)
                dependencies {
                    implementation("com.squareup.sqldelight:sqlite-driver:${rootProject.extra["sqldelight_version"]}")
                    //QR code generation
                    implementation("io.github.g0dkar:qrcode-kotlin-jvm:3.1.0")
                }
            }
        }
    }
}

android {
    compileSdk =  32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-geometry:1.1.1")
}

sqldelight{
    database("OpenNotesDb"){
        packageName = "com.raywenderlich.jetnotes"
        sourceFolders = listOf("notesdb")
        schemaOutputDirectory = file("src/commonMain/sqldelight/com/raywenderlich/jetnotes/db")
    }
}
