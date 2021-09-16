plugins {
    java
    kotlin("jvm") version "1.5.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
//    maven("https://dl.bintray.com/mipt-npm/scientifik")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
//    implementation("org.processing:core:3.3.7")
    implementation(fileTree("libs").matching {include("**/*.jar")})
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("space.kscience:kmath-core:0.2.1")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    jar {
        manifest {
            attributes("Main-Class" to "LauncherKt")
        }
        from(Callable { configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) } })
    }

    register("run", JavaExec::class) {
        main = "LauncherKt"
        standardInput = System.`in`
        classpath = sourceSets["main"].runtimeClasspath
    }
}

