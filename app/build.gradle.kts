plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

// Version: the git tag (vMAJOR.MINOR.PATCH) is the single source of truth; the release
// workflow passes it in as VERSION_NAME. Local/debug builds fall back to a dev version.
val appVersionName: String =
    (System.getenv("VERSION_NAME")?.takeIf { it.isNotBlank() } ?: "0.1.0").removePrefix("v")
val semver: List<String> = appVersionName.split(".")
val appVersionCode: Int = (semver.getOrNull(0)?.toIntOrNull() ?: 0) * 1_000_000 +
    (semver.getOrNull(1)?.toIntOrNull() ?: 0) * 1_000 +
    (semver.getOrNull(2)?.toIntOrNull() ?: 0)

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

    kotlin {
        jvmToolchain(17)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
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
            isMinifyEnabled = false
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
    }
    lint {
        lintConfig = file("lint.xml")
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
