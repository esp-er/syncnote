
buildscript {
   extra["ktor_version"]  = "2.0.3"
   extra["kotlin_version"] = "1.7.0"
   extra["koin_version"] = "3.1.5"
   extra["sqldelight_version"] = "1.5.3"
   extra["compose_version"] = "1.2.0-rc03"
   extra["compose_compiler_version"] = "1.2.0"
   extra["composedesktop_version"] = "1.2.0-alpha01-dev741"
   extra["logback_version"] = "1.2.11"

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {

        classpath("com.guardsquare:proguard-gradle:7.2.0")
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
        classpath("com.squareup.sqldelight:gradle-plugin:${project.extra["sqldelight_version"]}")
        classpath("io.insert-koin:koin-core:${project.extra["koin_version"]}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
