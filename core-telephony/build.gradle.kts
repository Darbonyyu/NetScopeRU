plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "ru.netscope.core.telephony"; compileSdk = 35; defaultConfig { minSdk = 26; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" } }
dependencies {
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")
    testImplementation(kotlin("test"))
}
