plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tariapp.scancare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tariapp.scancare"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"https://api-scancare1-173910592123.asia-southeast2.run.app/\"")
        buildConfigField("String", "BASE_URL_OCR", "\"https://api-scancare-model-ml2-173910592123.asia-southeast2.run.app/\"")

    }

    buildTypes {
//        debug {
//            buildConfigField("String", "BASE_URL", "\"https://api-scancare1-173910592123.asia-southeast2.run.app/\"")
//            buildConfigField("String", "BASE_URL_OCR", "\"https://api-scancare-model-ml2-173910592123.asia-southeast2.run.app/\"")
//        }
        release {
//            buildConfigField("String", "BASE_URL", "\"https://api-scancare1-173910592123.asia-southeast2.run.app/\"")
//            buildConfigField("String", "BASE_URL_OCR", "\"https://api-scancare-model-ml2-173910592123.asia-southeast2.run.app/\"")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.fragment.ktx)

    //circleImageView
    implementation(libs.circleimageview)

    //library  ML Kit
    implementation(libs.text.recognition)

    implementation(libs.androidx.datastore.preferences)

    // Retrofit core library
    implementation (libs.retrofit)

    // Converter untuk parsing JSON (gunakan GSON sebagai contoh)
    implementation (libs.converter.gson)

    // (Opsional) OkHttp untuk logging (menambahkan interceptor log jaringan)
    implementation (libs.logging.interceptor)

    // Lifecycle components
    implementation (libs.androidx.lifecycle.runtime.ktx)

    //mengubah rotasi file piksel
    implementation(libs.androidx.exifinterface)

    implementation (libs.ucrop)

    //room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.glide)

    implementation (libs.kotlinx.coroutines.android)


}