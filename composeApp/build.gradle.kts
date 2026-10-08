import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.utils.getLocalProperty
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.mikepenz.aboutlibrary.android)
    alias(libs.plugins.koin.compiler)
}

kotlin {
    
    sourceSets {

        dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material)
            implementation(libs.material.icons.extended)
            implementation(libs.ui)
            implementation(libs.components.resources)
            implementation(libs.ui.tooling.preview)
            implementation(projects.shared)
            api(libs.resources.compose)

            // Firebase BOM
            implementation(platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics)

            // Koin BOM
            implementation(platform(libs.koin.bom))
            implementation(libs.koin.android)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.koin.compose)
            implementation(libs.koin.androidx.compose)

            implementation(libs.accompanist.permissions)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.multiplatform.markdown.renderer.m3)
            implementation(libs.richeditor.compose)
            implementation(libs.decompose)
            implementation(libs.decompose.jetbrains)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.material3)
            debugImplementation(libs.ui.tooling)
            implementation(libs.aboutlibraries.core)
            implementation(libs.aboutlibraries.compose.m3)
        }
    }
}


android {
    namespace = "com.charan.yourday"
    compileSdk {
        version = release(libs.versions.android.compileSdk.get().toInt()){
            minorApiLevel = 0
        }
    }


    defaultConfig {
        applicationId = "com.charan.yourday"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        resValues = true
        compose = true
    }

    signingConfigs {
        create("release") {
            keyAlias = getLocalProperty("KEY_ALIAS")
            keyPassword = getLocalProperty("KEY_PASSWORD")
            storeFile = getLocalProperty("KEY_LOCATION")?.takeIf { it.isNotBlank() }?.let { file(it) }
            storePassword = getLocalProperty("KEY_STORE_PASSWORD")
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "YourDay-Debug")

        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        androidComponents {
            onVariants { variant ->
                variant.outputs.forEach { output ->
                    if (output is com.android.build.api.variant.impl.VariantOutputImpl) {
                        val name = variant.name
                        val versionName = android.defaultConfig.versionName ?: "0.0.1"
                        output.outputFileName.set("YourDay-$name-$versionName.apk")
                    }
                }
            }
        }
    }
}
