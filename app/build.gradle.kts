import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val user: String = localProperties.getProperty("api.user") ?: ""
val pass: String = localProperties.getProperty("api.password") ?: ""
val uri: String = localProperties.getProperty("api.uri") ?: ""
val wss: String = localProperties.getProperty("api.wss") ?: ""
android {
    namespace = "com.vaalzebub.fintatechtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vaalzebub.fintatechtest"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_USER", user)
        buildConfigField("String", "API_PASS", pass)
        buildConfigField("String", "API_URI", uri)
        buildConfigField("String", "API_WSS", wss)
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
        compose = true
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Destinations
    implementation(libs.animations.core)
    ksp(libs.destinations.ksp)

    // Koin
    implementation(libs.koin.androidx.compose.navigation)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.logger.slf4j)
    ksp(libs.koin.ksp.compiler)
    // Retrofit
    implementation (libs.retrofit)
    implementation (libs.gson)
    implementation (libs.retrofit.converter.gson)
    implementation (libs.logging.interceptor)
    // Charts
    implementation (libs.compose.charts)

    //WebSocket
    implementation (libs.ktor.client.android)
    implementation (libs.ktor.client.websockets)
}