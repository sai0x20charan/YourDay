# ProGuard / R8 configuration file for composeApp

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Kotlin Multiplatform and Serialization
-keepattributes *Annotation*,InnerClasses,EnclosingMethod

# Koin
-dontwarn org.koin.**

# Decompose
-keep class com.arkivanov.decompose.** { *; }
