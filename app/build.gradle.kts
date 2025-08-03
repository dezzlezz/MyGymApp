plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")


    // Falls du wirklich noch Navigation SafeArgs brauchst (meist nein bei Compose-only):
    // id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.mygymapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mygymapp"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        // Nur falls du noch XML verwendest, sonst kann das weg:
        // viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {
    // --- COMPOSE BOM (kontrolliert Compose-Versionen) ---
    implementation(platform("androidx.compose:compose-bom:2025.07.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation("androidx.compose.ui:ui:<version>")
    implementation("androidx.compose.animation:animation-core:1.8.3")
    // DEBUG/DEV
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // --- ROOM (Version einheitlich steuern) ---
    val roomVersion = "2.7.2"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // --- SONSTIGE ---
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:<version>")

}

kapt {
    javacOptions {
        option("-J--add-modules=jdk.compiler")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
        option("-J--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
        option("-J--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED")
    }
}
