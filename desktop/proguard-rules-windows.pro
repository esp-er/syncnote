-dontobfuscate

-dontwarn kotlinx.**
-dontwarn kotlin.**

-dontwarn org.slf4j.**


#-keep class kotlin.** { *; }
#-keep class kotlinx.coroutines.** { *; }
#-keep class kotlinx.** { *; }
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }

-keep class com.patriker.syncnote.MainKt { *; }
-keep class com.patriker.syncnote.MainViewModel { *; }

-keep class com.squareup.sqldelight.** { *; }
-keep class com.patriker.sqldelight.sqlite.** { *; }
-keep class org.sqlite.native.Linux.x86_64.**{ *; }

