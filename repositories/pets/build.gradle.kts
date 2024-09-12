plugins {
    id("libs")
}

dependencies {

    implementation(project(":services:network:"))
    implementation(project(":services:database:"))
    implementation(project(":repositories:domain:"))

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.koin)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}