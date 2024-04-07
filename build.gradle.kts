val exposed_version: String by project
plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.22"
    kotlin("plugin.noarg") version "1.9.22"
    application
}

group = "org.coliver.enterprise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

noArg {
    annotation("org.coliver.enterprise.model.NoArg")
}

application {
    mainClass.set("org.coliver.enterprise.MainKt")
}

dependencies {
    implementation("dev.inmo:tgbotapi:11.0.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.jetbrains.exposed:exposed-core:0.49.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.49.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.49.0")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:0.49.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
            sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
