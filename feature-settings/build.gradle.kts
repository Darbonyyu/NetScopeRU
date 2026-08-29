plugins { id("com.android.library"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose"); id("org.jetbrains.kotlin.kapt"); id("com.google.dagger.hilt.android") }
android {
    namespace = "ru.netscope.feature.settings"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}
dependencies {
    implementation(project(":core-data")); implementation(project(":core-telephony")); implementation("androidx.compose.ui:ui:1.7.8"); implementation("androidx.compose.material3:material3:1.3.1"); implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"); implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7"); implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.core:core:1.15.0"); implementation("com.google.dagger:hilt-android:2.55"); kapt("com.google.dagger:hilt-compiler:2.55")
}
