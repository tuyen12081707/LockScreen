plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)

    id("kotlin-parcelize")
    id("maven-publish") // 1. THÊM PLUGIN NÀY
}

android {
    namespace = "com.panda.reminderlockscreen"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // 2. (Tùy chọn nhưng RẤT NÊN CÓ ở AGP mới)
    // Báo cho Android Gradle Plugin biết bạn muốn xuất bản bản 'release'
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    implementation(libs.glide)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")
}

// 3. THÊM KHỐI NÀY Ở CUỐI CÙNG CỦA FILE
// Thiết lập cấu hình đóng gói (Publication) để JitPack kéo về
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // JitPack sẽ tự động lấy User/Repo/Tag của GitLab làm GroupId, ArtifactId và Version.
                // Bạn không cần (và không nên) hardcode version ở đây.
            }
        }
    }
}