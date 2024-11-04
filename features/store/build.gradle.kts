plugins {
    id("libs")
    id("compose")
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.feature)

    implementation(project(":ui:theme:"))
    implementation(project(":features:common:"))
    implementation(project(":repositories:domain:"))
    implementation(project(":repositories:pets:"))

    implementation(libs.kotlinx.datetime)
    implementation(libs.koin)
    implementation(libs.timber)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}