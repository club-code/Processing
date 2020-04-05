plugins {
    java
    kotlin("jvm") version "1.3.61"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/mipt-npm/scientifik")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    implementation("org.processing:core:3.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    api("scientifik:kmath-core:0.1.3")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
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

