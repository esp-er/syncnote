import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.ir.backend.js.compile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.compose.desktop.application.dsl.JvmApplication

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.github.johnrengelman.shadow").version("7.1.2")
}

group = "com.patriker.syncnote"
version = "1.0.0"



with(tasks) {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.majorVersion
    }
}
// 3
kotlin {
    /*
    jvm { //Set up jvm target
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }*/
    //sources and resources for the jvm
        //val jvmMain by getting {

            //kotlin.srcDirs("src/jvmMain/kotlin")
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
                implementation("io.ktor:ktor-server-netty-jvm:${rootProject.ext["ktor_version"]}")
                //implementation("io.ktor:ktor-server-cio:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-server-auth-jwt:${rootProject.ext["ktor_version"]}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${rootProject.ext["ktor_version"]}")
                //implementation("ch.qos.logback:logback-classic:${rootProject.ext["logback_version"]}")
                implementation("com.russhwolf:multiplatform-settings:${rootProject.ext["multi_settings_version"]}")

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
        //}
}

/*tasks.named<Copy>("jvmProcessResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}*/

//Note: This does nothing at the moment
tasks.named<ShadowJar>("shadowJar"){
    exclude("**/org/sqlite/Windows/*")
    exclude(".so")
    exclude("**/Mac/*")
    //dependsOn(tasks.named("minifyJar"))
    //archiveFileName.set("syncnote-shadow.jar")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    //kotlinOptions.freeCompilerArgs += "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi"
}

//val minifyJar by registering(proguard.gradle.ProGuardTask::class)
//val minifyJar by tasks.registering(proguard.gradle.ProGuardTask::class)

compose.desktop {
    application {
        mainClass = "com.patriker.syncnote.MainKt"
        //disableDefaultConfiguration()
        //fromFiles(minifyJar.outputs.files.asFileTree)
        //mainJar.set(tasks.getByName("minifyJar").outputs.files.first())
        val releaseBuild = System.getenv("RELEASE").toBoolean()
        val extraArgs = if(releaseBuild)
                            listOf("-XX:+AutoCreateSharedArchive",
                            "-XX:SharedArchiveFile=../lib/app/resources/syncnote.jsa", "-Xms8M","-Xmx50M","-Xss500K","-XX:TieredStopAtLevel=1",
                            "-Dskiko.vsync.enabled=false")
                        else listOf("-XX:TieredStopAtLevel=1", "-Xmx100M", "-Dskiko.vsync.enabled=false")

        val winArgs = listOf("-XX:+AutoCreateSharedArchive",
            "-XX:SharedArchiveFile=syncnote.jsa", "-Xms8M","-Xmx50M","-Xss500K","-XX:TieredStopAtLevel=1",
            "-Dskiko.vsync.enabled=false")

        jvmArgs += extraArgs
        //jvmArgs += listOf("-Dsun.java2d.uiScale=2.0")
            //"-Dskiko.vsync.enabled=false", "-XX:SharedArchiveFile=syncnote.jsa")
        nativeDistributions {
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "SyncNote"
            macOS {
                bundleID = "com.raywenderlich.jetnotes"
            }
            linux {
                menuGroup = "SyncNote"
                iconFile.set(project.file("syncicon_128.png"))
                packageVersion = "0.1.0"
                // a version only for the deb package
                debPackageVersion = "0.1.0"
                // a version only for the rpm package
                rpmPackageVersion = "0.1.0"

            }
            windows{
                iconFile.set(project.file("syncicon_win.ico"))
                console = false
                perUserInstall = true
                dirChooser = true
                menuGroup = "SyncNote"
                shortcut = true
            }
            modules("java.base", "java.desktop", "java.sql", "java.logging")
        }

        //if OPTIMIZE is set to true in env
        if (System.getenv("OPTIMIZE").toBoolean()) {
            configureProguard()
        }

        //disableDefaultConfiguration()
        //fromFiles(obfuscate.get().outputs.files.asFileTree)
        //mainJar.set(tasks["jar"].outputs.files.forEach{ RegularFile { mapObfuscatedJarFile(it.archiveFile.get().asFile) } })
    }
}

fun JvmApplication.configureProguard() {
    val jarTask = tasks.named<Jar>("jar")
    val allJars =
        jarTask.get().outputs.files + sourceSets.get("main").runtimeClasspath.filter { it.path.endsWith(".jar") }
            // workaround https://github.com/JetBrains/compose-jb/issues/1971
            .filterNot { it.name.startsWith("skiko-awt-") && !it.name.startsWith("skiko-awt-runtime-") }
            .distinctBy { it.name } // Prevent duplicate jars

    // Split the Jars to get the ones that need obfuscation and those that do not
    val (obfuscateJars, otherJars) = allJars.partition {
        !it.name.contains("slf4j", ignoreCase = true)
            .or(it.name.contains("logback", ignoreCase = true))
    }

    // Proguard Task definition!
    val proguard by tasks.register<proguard.gradle.ProGuardTask>("proguard") {
        dependsOn(jarTask)
        println("Config ProGuard")
        for (file in obfuscateJars) {
            injars(file)
            outjars(mapObfuscatedJarFile(file))
        }
        //val library = if (System.getProperty("java.version").startsWith("1.")) "lib/rt.jar" else "jmods"
        val library = "jmods"
        val customCompose = System.getenv("CUSTOMCOMPOSE").toBoolean()
        val windowsBuild = System.getenv("WINDOWSBUILD").toBoolean()
        if(windowsBuild)
            libraryjars("C:/java/zulu18fx/$library")
        else if(customCompose)
            libraryjars("/home/patrik/.sdkman/candidates/java/18.0.1.fx-zulu/$library")
        else
            libraryjars("${compose.desktop.application.javaHome ?: System.getProperty("java.home")}/$library")
        libraryjars(otherJars)
        configuration("proguard-rules-linux.pro")
    }

    // Disable Compose Desktop default config and add your own Jars
    disableDefaultConfiguration()
    fromFiles(proguard.outputs.files.asFileTree)
    fromFiles(otherJars)
    mainJar.set(jarTask.map { RegularFile { mapObfuscatedJarFile(it.archiveFile.get().asFile) } })
}

// Map Files to a known path
fun mapObfuscatedJarFile(file: File) =
    File("${project.buildDir}/tmp/obfuscated/${file.nameWithoutExtension}.min.jar")


tasks.register<proguard.gradle.ProGuardTask>("minifyJar") {
//minifyJar.configure{
    val packageUberJarForCurrentOS by tasks.getting
    //val packageUberJarForCurrentOS = tasks.get("packageUberJarForCurrentOS")
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

