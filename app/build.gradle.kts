plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.today"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.today"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.mikhaellopez.circularimageview)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.core)
    implementation(libs.navigation.runtime)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    implementation(libs.google.services)
    androidTestImplementation(libs.espresso.core)
     implementation(libs.design)
    implementation(libs.anrspy)
    implementation(libs.play.services.auth)
    implementation(libs.picasso)
    implementation(platform(libs.firebase.bom))

}