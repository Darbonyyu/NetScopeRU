plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "ru.netscope.feature.map"; compileSdk = 35; defaultConfig { minSdk = 26 } }
dependencies { implementation(project(":core-telephony")) }
