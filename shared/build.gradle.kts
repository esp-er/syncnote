plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("org.jetbrains.compose") version "1.1.1"
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
                api(compose.foundation)
                api(compose.runtime)
                api(compose.material)
                api(compose.materialIconsExtended)
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
                }
            }
        }
    }
}

android {
    compileSdk =  32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
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
        sourceFolders = listOf("sqldelight", "db")
        schemaOutputDirectory = file("src/commonMain/sqldelight/com/raywenderlich/jetnotes/db")
    }
}
