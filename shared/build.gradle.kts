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

    android {
        namespace = "com.charan.yourday.shared"
        compileSdk {
            version = release(libs.versions.android.compileSdk.get().toInt()){
                minorApiLevel = 0
            }
        }
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
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Ktor BOM & clients
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.encoding)

            // Kotlinx Coroutines BOM
            implementation(project.dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.kotlinx.coroutines.core)

            // Koin BOM
            api(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.core)

            api(libs.resources)
            api(libs.kotlinx.datetime)
            api(libs.graphics)
            api(libs.datastore.preferences)
            api(libs.datastore)
            api(libs.decompose)
            api(libs.essenty.lifecycle)
            api(libs.essenty.stateKeeper)
            api(libs.essenty.instanceKeeper)

            api(libs.kaluga.permissions.base)
            api(libs.kaluga.permissions.calendar)
            api(libs.kaluga.permissions.location)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.play.services.location)
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
