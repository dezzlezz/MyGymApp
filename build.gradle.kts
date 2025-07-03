// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

plugins {
    id("com.android.application")     version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    // id("kotlin-kapt") version "1.8.10" apply false  ← hier raus!
}
