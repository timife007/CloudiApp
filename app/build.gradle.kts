import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.timife.cloudiapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.timife.cloudiapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties().apply{
            load(project.rootProject.file("local.properties").inputStream())
        }
        buildConfigField("String","CLOUD_NAME","\"${properties.getProperty("cloud-name")}\"")
        buildConfigField("String","API_KEY","\"${properties.getProperty("api-key")}\"")
        buildConfigField("String","API_SECRET","\"${properties.getProperty("api-secret")}\"")
    }
    buildFeatures{
        buildConfig = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
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
    implementation(libs.room)
    implementation(libs.roomKtx)
    implementation(libs.hilt)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.coroutinesAdapter)
    implementation(libs.coroutineCore)
    implementation(libs.coroutineAndroid)
    implementation(libs.viewModelCompose)
    implementation(libs.moshi)
    implementation(libs.paging)
    implementation(libs.paging.compose)
    implementation(libs.roomPaging)
    //cloudinary
    implementation(libs.cloudinary.cloudinary.android)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.roomCompiler)
    implementation(libs.timber)
    implementation(libs.coroutineCore)
    implementation(libs.coroutineAndroid)
    implementation(libs.coroutinesAdapter)
    implementation(libs.hiltCompose)
    implementation(libs.materialIcons)
    implementation(libs.coil)
    implementation(libs.okHttp)
    implementation(libs.moshi.converter)
    implementation(libs.moshi.kotlin)
    implementation(libs.json.serializer)
    implementation(libs.logging.interceptor)
    implementation(libs.chuckerLib)



    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.core.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(libs.runner)

    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.core.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}