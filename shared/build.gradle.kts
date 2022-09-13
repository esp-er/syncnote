
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    id("org.jetbrains.kotlin.plugin.serialization").version("1.7.10")
    id("org.jetbrains.compose")
}

version = "1.0"

kotlin {


    android() //Define android target

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //compileOnly("org.jetbrains.compose:compose-gradle-plugin:1.2.0-alpha-custom") //Present on mavenLocal

               //implementation("org.jetbrains.compose:compose-gradle-plugin:1.2.0-alpha01-dev755") //Present on mavenLocal
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("io.insert-koin:koin-core:${rootProject.extra["koin_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.extra["ktor_version"]}")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")
                implementation("io.ktor:ktor-server-content-negotiation:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-core-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-websockets-jvm:${rootProject.ext["ktor_version"]}")

                //implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")
                //implementation("io.ktor:ktor-server-netty:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")

                implementation("com.russhwolf:multiplatform-settings:${rootProject.ext["multi_settings_version"]}")

                //compileOnly("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.2.0-alpha-custom2")

                //implementation("io.ktor:ktor-server-cio:${rootProject.ext["ktor_version"]}")
                //
                // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3

                //For random strings
                implementation("org.apache.commons:commons-lang3:3.12.0")

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
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0")
                implementation("com.squareup.sqldelight:android-driver:${rootProject.extra["sqldelight_version"]}")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.0")
                implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.5.0")
                implementation("androidx.appcompat:appcompat:1.4.2")

                //Ktor
                implementation("io.ktor:ktor-client-cio:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-core:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-android:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-websockets:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-client-content-negotiation:${rootProject.extra["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.ext["ktor_version"]}")

                //koin
                implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")


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
    implementation("androidx.compose.ui:ui-geometry:1.2.0")
}

sqldelight{ //TODO: change package name
    database("OpenNotesDb"){
        packageName = "com.raywenderlich.jetnotes"
        sourceFolders = listOf("notesdb")
        schemaOutputDirectory = file("src/commonMain/sqldelight/com/raywenderlich/jetnotes/db")
    }
}
