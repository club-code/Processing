plugins {
    java
    kotlin("jvm") version "1.3.61"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    implementation("org.processing:core:3.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "cherrytree.CherryTreeKt"
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
            attributes("Main-Class" to "cherrytree.CherryTreeKt")
        }
        from(Callable { configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) } })
    }
}

