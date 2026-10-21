plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}
android {
    namespace = "com.smartshoe.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.smartshoe.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {


            // Compose BOM
            implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.appcompat:appcompat:1.6.1")
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)

            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)

            implementation("androidx.compose.material:material-icons-extended")

            implementation(libs.retrofit)
            implementation(libs.retrofit.gson)

            implementation(libs.kotlinx.coroutines.android)

            implementation(libs.okhttp)
            implementation(libs.logging.interceptor)

            implementation("io.coil-kt:coil-compose:2.7.0")

            implementation("io.socket:socket.io-client:2.1.0") {
                exclude(group = "org.json", module = "json")
            }

            implementation("com.google.maps.android:maps-compose:6.1.0")
            implementation("com.google.android.gms:play-services-maps:19.2.0")
            implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.animation:animation:1.7.0")
    implementation("com.airbnb.android:lottie-compose:6.4.0")
            testImplementation(libs.junit)

            androidTestImplementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(libs.androidx.compose.ui.test.junit4)
            androidTestImplementation(libs.androidx.espresso.core)
            androidTestImplementation(libs.androidx.junit)

            debugImplementation(libs.androidx.compose.ui.tooling)
            debugImplementation(libs.androidx.compose.ui.test.manifest)
        }
