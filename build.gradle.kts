/*
val kotlin_version by extra("1.6.10")
val compose_version by extra("1.1.1")
val sqldelight_version by extra("1.5.3")
val koin_version by extra("3.1.5")
*/

buildscript {
   extra["ktor_version"]  = "2.0.1"
   extra["kotlin_version"] = "1.6.10"
   extra["koin_version"] = "3.1.5"
   extra["sqldelight_version"] = "1.5.3"
   extra["compose_version"] = "1.1.1"
   extra["composedesktop_version"] = "1.1.1"

    repositories {
        google()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {

        classpath("com.android.tools.build:gradle:7.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
        classpath("com.squareup.sqldelight:gradle-plugin:${project.extra["sqldelight_version"]}")
        classpath("io.insert-koin:koin-core:${project.extra["koin_version"]}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
