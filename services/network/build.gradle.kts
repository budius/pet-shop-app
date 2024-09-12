plugins {
    id("libs")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {

    implementation(libs.ktor.client)
    // implementation(libs.ktor.client.cio) // replace here if we do a real backend
    implementation(libs.ktor.client.mock) // remove mock if we do a real backend
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content)
    implementation(libs.ktor.client.json)

    implementation(libs.kotlinx.json)
    implementation(libs.kotlinx.datetime)

    implementation(libs.koin)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}