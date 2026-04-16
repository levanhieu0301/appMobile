//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("org.jetbrains.kotlin.kapt")
//}
//
//android {
//    namespace = "com.example.appcar"
//    compileSdk = 34
//
//    defaultConfig {
//        applicationId = "com.example.appcar"
//        minSdk = 24
//        targetSdk = 34
//        versionCode = 1
//        versionName = "1.0"
//    }
//}
//
////android {
////    namespace = "com.example.appcar"
////    compileSdk {
////        version = release(36) {
////            minorApiLevel = 1
////        }
////    }
////
////    defaultConfig {
////        applicationId = "com.example.appcar"
////        minSdk = 24
////        targetSdk = 36
////        versionCode = 1
////        versionName = "1.0"
////
////        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
////    }
////
////    buildTypes {
////        release {
////            isMinifyEnabled = false
////            proguardFiles(
////                getDefaultProguardFile("proguard-android-optimize.txt"),
////                "proguard-rules.pro"
////            )
////        }
////    }
////    compileOptions {
////        sourceCompatibility = JavaVersion.VERSION_11
////        targetCompatibility = JavaVersion.VERSION_11
////    }
////}
//
//
//dependencies {
//    implementation("androidx.core:core-ktx:1.12.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.11.0")
//    implementation("androidx.activity:activity-ktx:1.8.2")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//
//    // Room
//    implementation("androidx.room:room-runtime:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
//    implementation("androidx.room:room-ktx:2.6.1")
//
//}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.appcar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.appcar"
        minSdk = 24
        targetSdk = 34
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

    // 1. QUAN TRỌNG: Thêm dòng này để sửa lỗi 'layout' và 'id'
    buildFeatures {
        viewBinding = true
    }

    // 2. Cần thiết cho các thư viện Android hiện đại và Room
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}