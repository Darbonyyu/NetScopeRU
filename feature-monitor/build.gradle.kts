plugins { id("com.android.library"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose"); id("org.jetbrains.kotlin.kapt"); id("com.google.dagger.hilt.android") }
android {
    namespace = "ru.netscope.feature.monitor"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}
dependencies {
    implementation(project(":core-data")); implementation(project(":core-telephony"))
    implementation("androidx.activity:activity-compose:1.10.1"); implementation("androidx.compose.ui:ui:1.7.8"); implementation("androidx.compose.ui:ui-graphics:1.7.8"); implementation("androidx.compose.ui:ui-tooling-preview:1.7.8"); implementation("androidx.compose.material3:material3:1.3.1"); implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"); implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7"); implementation("androidx.datastore:datastore-preferences:1.1.2"); implementation("com.google.dagger:hilt-android:2.55"); kapt("com.google.dagger:hilt-compiler:2.55")
}
