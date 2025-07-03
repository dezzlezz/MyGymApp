plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // KAPT-Plugin hier ohne Version
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.mygymapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mygymapp"
        minSdk = 21          // <-- hier mindestens 14, besser aber 21+
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        // Wenn du kein custom BuildConfig brauchst, kannst du das BuildConfig-Feature wegnehmen:
        // buildConfig = false  // (optional)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")

    // Room
    implementation("androidx.room:room-runtime:2.5.0")
    kapt        ("androidx.room:room-compiler:2.5.0")
    implementation("androidx.room:room-ktx:2.5.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
}
