plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}

android {
    namespace = "com.example.tmk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tmk"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Для FileProvider
        vectorDrawables.useSupportLibrary = true

        // Разрешаем большие PDF файлы
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Для отладки PDF генерации
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        // dataBinding = true // раскомментировать если понадобится
    }

    // Улучшенная упаковка ресурсов
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // UI Components
    implementation("com.google.android.material:material:1.11.0") // Обновлено до 1.11.0
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Для работы с разрешениями
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Для PDF генерации
    implementation("com.itextpdf:itextpdf:5.5.13.3")

    // Для графиков (MPAndroidChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Для загрузки изображений (опционально)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Для работы с SharedPreferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Для анимаций
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    // Для работы с файлами
    implementation("androidx.documentfile:documentfile:1.0.1")

    // MultiDex для больших приложений
    implementation("androidx.multidex:multidex:2.0.1")

    // Тестирование
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // УДАЛИТЕ лишние зависимости:
    // Удалите дублирующийся material:1.9.0 (оставьте только 1.11.0)
    // Удалите Apache POI если не используете - он очень большой и может вызвать проблемы
    implementation("com.itextpdf:itext7-core:7.2.5") // для продвинутой работы с PDF
}