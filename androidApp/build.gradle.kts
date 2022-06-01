
plugins {
    id("com.android.application")
    id("com.squareup.sqldelight")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.raywenderlich.android.jetnotes"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=androidx.compose.ui.ExperimentalComposeUiApi" +
                "-Xopt-in=kotlin.RequiresOptIn"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(project(":shared"))
    //implementation(project(":shared-ui"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.activity:activity-compose:1.4.0")

    // Material
    implementation("com.google.android.material:material:1.6.0")

    // Compose
    implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.runtime:runtime-livedata:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.foundation:foundation-layout:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.foundation:foundation:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.animation:animation:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.0-rc01")

    // Kotlinx datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")

    //Database
    implementation("com.squareup.sqldelight:android-driver:${rootProject.extra["sqldelight_version"]}")

    //Koin dependency injection
    implementation("io.insert-koin:koin-android:${rootProject.extra["koin_version"]}")
    implementation("io.insert-koin:koin-androidx-compose:${rootProject.extra["koin_version"]}")

    // Zxing (QR)
    implementation("com.google.zxing:core:3.3.3")

    //CameraX (for QR)
    implementation( "androidx.camera:camera-camera2:1.0.2")
    implementation("androidx.camera:camera-lifecycle:1.0.2")
    implementation("androidx.camera:camera-view:1.0.0-alpha31")


}
/*
sqldelight{
    database("OpenNotesDb"){
        packageName = "com.raywenderlich.jetnotes"
        sourceFolders = listOf("notesdb")
        //schemaOutputDirectory = file("src/main/sqldelight/com/raywenderlich/android/jetnotes/db")
    }
}*/