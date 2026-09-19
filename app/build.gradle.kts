plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

// The version lives in gradle.properties, where the release workflow and F-Droid's
// checkupdates both read it. See the comment there before bumping it.
val appVersionName: String = providers.gradleProperty("VERSION_NAME").get()
val appVersionCode: Int = providers.gradleProperty("VERSION_CODE").get().toInt()

// Optional release signing — supplied via env in CI; absent locally so debug still builds.
val ksFile = System.getenv("KEYSTORE_PATH")?.takeIf { it.isNotBlank() }
    ?.let { rootProject.file(it).absoluteFile }?.takeIf { it.isFile }
val ksPassword = System.getenv("KEYSTORE_PASSWORD")?.takeIf { it.isNotBlank() }
val ksAlias = System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }
val ksKeyPassword = System.getenv("KEY_PASSWORD")?.takeIf { it.isNotBlank() }
val hasSigning = ksFile != null && ksPassword != null && ksAlias != null && ksKeyPassword != null

android {
    namespace = "com.cocode.calendar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cocode.calendar"
        minSdk = 26
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    signingConfigs {
        if (hasSigning) {
            create("release") {
                storeFile = ksFile
                storePassword = ksPassword
                keyAlias = ksAlias
                keyPassword = ksKeyPassword
            }
        }
    }

    buildTypes {
        release {
            // F-Droid's reviewer rejects a release build with minification off for no reason
            // (fdroiddata !49432); it also shrinks unused resources pulled in by the AndroidX
            // and Compose dependencies.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            setProperty("archivesBaseName", "Calendar-v${defaultConfig.versionName}")
            if (hasSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs {
            // The only .so file in the APK is a prebuilt from AndroidX (graphics-path). AGP
            // strips it with whatever NDK it finds, so a rebuild without that exact NDK
            // produces different bytes — F-Droid's builder has none unless its recipe pins one.
            // Keeping the symbols leaves the library exactly as its AAR ships it, which
            // rebuilds identically anywhere, and costs a few kB.
            keepDebugSymbols += "**/*.so"
        }
    }
    lint {
        lintConfig = file("lint.xml")
    }

    // AGP otherwise adds a "Dependency metadata" block to the APK signing block,
    // encrypted with a key only Google Play holds. F-Droid rejects APKs that carry
    // it, and it lands in the published release APK that F-Droid verifies against.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}
