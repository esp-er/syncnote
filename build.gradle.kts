
buildscript {
   extra["ktor_version"]  = "2.1.0"
   extra["kotlin_version"] = "1.7.0"
   extra["koin_version"] = "3.1.5"
   extra["sqldelight_version"] = "1.5.3"
   extra["compose_version"] = "1.2.0"
   extra["compose_compiler_version"] = "1.2.0"
   extra["composedesktop_version"] = "1.2.0-alpha01-dev755"
   extra["logback_version"] = "1.2.11"
   extra["multi_settings_version"] = "0.9"

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        val releaseBuild = System.getenv("CUSTOMCOMPOSE").toBoolean()
        if(releaseBuild)
            classpath("org.jetbrains.compose:compose-gradle-plugin:1.2.0-alpha-custom2") //Present on mavenLocal
        else
            classpath("org.jetbrains.compose:compose-gradle-plugin:1.2.0-alpha01-dev755") //Present on mavenLocal
        //classpath("org.jetbrains.compose:org.jetbrains.compose.gradle.plugin:1.2.0-alpha-custom2")
        //classpath("org.jetbrains.compose.runtime:runtime-desktop:1.2.0-alpha01-dev750")
        //classpath("org.jetbrains.compose.runtime:runtime-android:1.2.0-alpha01-dev753")
        //classpath("org.jetbrains.compose.foundation:foundation:1.2.0-alpha01-dev753")
        //classpath("org.jetbrains.compose.material:material:1.2.0-alpha01-dev750")
        classpath("com.guardsquare:proguard-gradle:7.2.2")
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
        classpath("com.squareup.sqldelight:gradle-plugin:${project.extra["sqldelight_version"]}")
        classpath("io.insert-koin:koin-core:${project.extra["koin_version"]}")
    }
}

/*
configurations.all {
    resolutionStrategy.eachDependency { details ->
        if (details.requested.group == 'org.jetbrains.compose.desktop') {
            details.useVersion "
        }
    }
}
*/

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
