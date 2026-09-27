plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKMP) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.mikepenz.aboutlibrary) apply false
    alias(libs.plugins.mikepenz.aboutlibrary.android) apply false
    alias(libs.plugins.multiplatformResources) apply false
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()

    }
    dependencies {


        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.resources.generator)

    }
}