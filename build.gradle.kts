// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("org.sonarqube") version "4.3.0.3225"
    id("com.google.devtools.ksp") version "1.8.22-1.0.11" apply false
}

buildscript {
    extra.apply {
        set("kotlin_version", "1.9.0")
    }

    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")

        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:4.3.0.3225")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.create<Delete>("clean") {
    delete {
        rootProject.buildDir
    }
}

/*
task clean(type: Delete) {
    delete rootProject.buildDir
}
*/

//apply plugin: "org.sonarqube"

/*
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    ext.kotlin_version = '1.9.0'

    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0"

        classpath "org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:3.3"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}

apply plugin: "org.sonarqube"
 */