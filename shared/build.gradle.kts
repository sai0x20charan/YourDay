import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKMP)
    alias(libs.plugins.multiplatformResources)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.skie)
//    alias(libs.plugins.mikepenz.aboutlibrary)
}

kotlin {

    androidLibrary {
        namespace = "com.charan.yourday.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            export(libs.resources)
            export(libs.graphics)
            export(libs.decompose)
            export(libs.essenty.lifecycle)
            linkerOpts("-framework", "CoreML")

        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.encoding)
            api(libs.resources)
            api(libs.kotlinx.datetime)
            api(libs.koin.core)
            api(libs.graphics)
            implementation(libs.koin.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.napier)
            api(libs.permissions)
            api(libs.permissions.compose)
            implementation(libs.koin.compose.viewmodel)
            api(libs.datastore.preferences)
            api(libs.datastore)
            api(libs.decompose)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.essenty.lifecycle)
            api(libs.essenty.stateKeeper)
            api(libs.essenty.instanceKeeper)
            api("com.splendo.kaluga:base-permissions:1.6.0")
            api("com.splendo.kaluga:calendar-permissions:1.6.0")
            api("com.splendo.kaluga:location-permissions:1.6.0")
            api("com.cactuscompute:cactus:1.4.1-beta")




            // put your Multiplatform dependencies here
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation (libs.play.services.location)
            implementation (libs.accompanist.permissions)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

        }
    }
}
multiplatformResources {
    resourcesPackage.set("com.charan.yourday")

}
buildkonfig {
    packageName = "com.charan.yourday"

    defaultConfigs {
        val apiKey: String = gradleLocalProperties(rootDir, providers).getProperty("API_KEY")
        buildConfigField(FieldSpec.Type.STRING, "API_KEY", apiKey)
        val todoistClientID: String = gradleLocalProperties(rootDir, providers).getProperty("TODOIST_CLIENT_ID")
        buildConfigField(FieldSpec.Type.STRING, "TODOIST_CLIENT_ID", todoistClientID)
        val todoistClientSecret: String = gradleLocalProperties(rootDir, providers).getProperty("TODOIST_CLIENT_SECRET")
        buildConfigField(FieldSpec.Type.STRING, "TODOIST_CLIENT_SECRET", todoistClientSecret)
        val keyStorePassword: String = gradleLocalProperties(rootDir, providers).getProperty("KEY_STORE_PASSWORD")
        buildConfigField(FieldSpec.Type.STRING, "KEY_STORE_PASSWORD", keyStorePassword)
        val keyPassword: String = gradleLocalProperties(rootDir, providers).getProperty("KEY_PASSWORD")
        buildConfigField(FieldSpec.Type.STRING, "KEY_PASSWORD", keyPassword)
        val keyAlias: String = gradleLocalProperties(rootDir, providers).getProperty("KEY_ALIAS")
        buildConfigField(FieldSpec.Type.STRING, "KEY_ALIAS", keyAlias)
        val keyLocation: String = gradleLocalProperties(rootDir, providers).getProperty("KEY_LOCATION")
        buildConfigField(FieldSpec.Type.STRING, "KEY_LOCATION", keyLocation)




    }
}


