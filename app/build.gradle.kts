import java.util.Properties // <--- ДОДАНО ЦЕЙ РЯДОК

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.weather_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weather_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // --- ВИПРАВЛЕНИЙ БЛОК ЧИТАННЯ КЛЮЧА ---
        val keystoreProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            keystoreProperties.load(localPropertiesFile.inputStream())
        }

        // Створюємо константу BuildConfig.API_KEY
        // Якщо ключ не знайдено, підставиться порожній рядок ""
        val apiKey = keystoreProperties.getProperty("WEATHER_API_KEY") ?: ""
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    // Вмикаємо можливість використання BuildConfig та ViewBinding
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- НАШІ ДОДАНІ БІБЛІОТЕКИ ---

    // Архітектура
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")

    // Мережа
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Логування
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Картинки
    implementation("com.github.bumptech.glide:glide:4.16.0")
}