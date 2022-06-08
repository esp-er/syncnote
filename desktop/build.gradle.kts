import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.1"
}

group = "com.patriker.syncnote"
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

                //Koin depinjection
                implementation("io.insert-koin:koin-core:${rootProject.ext["koin_version"]}")

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

                //Ktor deps
                implementation("io.ktor:ktor-server-content-negotiation:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-core-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-websockets-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-netty:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth-jwt:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.ext["ktor_version"]}")
                implementation("ch.qos.logback:logback-classic:${rootProject.ext["logback_version"]}")
                implementation("io.ktor:ktor-network-tls-certificates:${rootProject.ext["ktor_version"]}")

                    implementation("io.netty:netty-tcnative:2.0.52.Final")
                    //:w
                // implementation("io.netty:netty-tcnative-boringssl-static:2.0.52.Final:linux-x86_64")
                    implementation("io.netty:netty-tcnative:2.0.52.Final:linux-x86_64")

                //QR code generation
                implementation("io.github.g0dkar:qrcode-kotlin-jvm:3.1.0")

                implementation(compose.desktop.currentOs)
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
        mainClass = "com.patriker.syncnote.MainKt"

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