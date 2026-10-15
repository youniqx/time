plugins {
    alias(libs.plugins.androidApplication)
}

// ponytail: all Kotlin (MainActivity, Application, Metro graph) stays in composeApp's androidMain,
// so this module needs no Kotlin/Compose/Metro plugins. Move it here if the app module ever gets own code.
android {
    namespace = "com.youniqx.time"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.youniqx.time"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        versionCode = System.getenv("VERSION_CODE")?.toInt() ?: 1
        versionName = System.getenv("PKG_VERSION") ?: "1.0.0"
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
}

dependencies {
    implementation(projects.composeApp)
    debugImplementation(libs.compose.ui.tooling)
}
