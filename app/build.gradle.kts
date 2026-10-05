plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// La configuración real se descarga desde Firebase Console; nunca se inventa.
if (file("google-services.json").exists()) apply(plugin = "com.google.gms.google-services")

android {
    namespace = "cl.duoc.comunicafacil"
    compileSdk = 37

    defaultConfig {
        applicationId = "cl.duoc.comunicafacil"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    val rutaFirma = providers.environmentVariable("COMUNICAFACIL_KEYSTORE").orNull
    if (rutaFirma != null) {
        signingConfigs {
            create("entrega") {
                storeFile = file(rutaFirma)
                storePassword = providers.environmentVariable("COMUNICAFACIL_STORE_PASSWORD").get()
                keyAlias = "comunicafacil"
                keyPassword = providers.environmentVariable("COMUNICAFACIL_KEY_PASSWORD").get()
            }
        }
        buildTypes { getByName("release") { signingConfig = signingConfigs.getByName("entrega") } }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    testImplementation("junit:junit:4.13.2")
    implementation(platform("androidx.compose:compose-bom:2026.08.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
