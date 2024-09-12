plugins {
    id("libs")
    //id("compose.conventions")
}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.datetime)
    //implementation(libs.kotlin.coroutinesCore)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.bundles.feature)
//    implementation(project(":ui:theme:"))
//
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
    testImplementation(libs.junit)
    testImplementation(libs.truth)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)

}