import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
}

group = "io.github.helmy2"
version = "0.0.2"

kotlin {
    jvm()
    androidLibrary {
        namespace = "io.github.helmy2"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilations.configureEach {
            compileTaskProvider.configure{
                compilerOptions {
                    jvmTarget.set(
                        JvmTarget.JVM_11
                    )
                }
            }
        }
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "hijri-date-picker", version.toString())

    pom {
        name = "hijri-date-picker"
        description = "a compose multiplatform library for selecting hijri dates"
        inceptionYear = "2025"
        url = "https://github.com/Helmy2/hijri-date-picker"
        licenses {
            license {
                name = "pache License 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
            }
        }
        developers {
            developer {
                id = "mo-helmy"
                name = "Mohamed Helmy"
                email = "mohelmy.eng@gmail.com"
            }
        }
        scm {
            url = "https://github.com/Helmy2//hijri-date-picker"
        }
    }
}
