import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.ir.backend.js.compile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose").version("1.2.0-alpha01-dev741")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "com.patriker.syncnote"
version = "1.0.0"

// 3
kotlin {
    jvm { //Set up jvm target
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "17"
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

                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                //Ktor deps
                implementation("io.ktor:ktor-server-content-negotiation:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-core-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-websockets-jvm:${rootProject.ext["ktor_version"]}")
                //implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")
                //implementation("io.ktor:ktor-server-netty:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-cio-jvm:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")
                //implementation("io.ktor:ktor-server-cio:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth-jwt:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.ext["ktor_version"]}")
                //implementation("ch.qos.logback:logback-classic:${rootProject.ext["logback_version"]}")

                //Tabler icons
                implementation("br.com.devsrsouza.compose.icons.jetbrains:tabler-icons:1.0.0")
                implementation("br.com.devsrsouza.compose.icons.jetbrains:octicons:1.0.0")
                implementation("br.com.devsrsouza.compose.icons.jetbrains:line-awesome:1.0.0")

                //Android lifecycle on desktop

                implementation(compose.desktop.currentOs)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.ui)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
            }
        }
    }
}

tasks.named<Copy>("jvmProcessResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<ShadowJar>("shadowJar"){
    exclude("**/org/sqlite/Windows/*")
    exclude(".so")
    exclude("**/Mac/*")
        //dependsOn(tasks.named("minifyJar"))
        //from("minifyJar")

        //archiveFileName.set("syncnote-shadow.jar")
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


tasks.register<proguard.gradle.ProGuardTask>("minifyJar") {
    val packageUberJarForCurrentOS by tasks.getting
    dependsOn(packageUberJarForCurrentOS)
    val files = packageUberJarForCurrentOS.outputs.files
    injars(files)
    outjars(files.map { file -> File(file.parentFile, "${file.nameWithoutExtension}.min.jar") })

    val library = if (System.getProperty("java.version").startsWith("1.")) "lib/rt.jar" else "jmods"
    libraryjars("${System.getProperty("java.home")}/$library")

    //TODO: add platform detection here
    configuration("proguard-rules-linux.pro")
    //configuration("proguard-rules-windows.pro")
    //configuration("proguard-rules-windows.pro")
}

