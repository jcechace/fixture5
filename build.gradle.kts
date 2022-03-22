import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val versionMockk: String by project

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("signing")
    id("maven-publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "io.github.jcechace"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:$versionMockk")
}

java {
    withSourcesJar()
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-doc")
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("Fixture5") {
            from(components["java"])

            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)

            pom {
                name.set("${project.group}:${project.name}")
                description.set("Declarative fixtures for JUnit 5")
                url.set("http://github.com/jcechace/fixture5")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("jcechace")
                        name.set("Jakub Cechacek")
                        email.set("jcechace@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/jcechace/fixture5.git")
                    developerConnection.set("scm:git:ssh://github.com/jcechace/fixture5.git")
                    url.set("http://github.com/jcechace/fixture5")
                }
            }
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGN_KEY_ID")
    val signingKey: String? = System.getenv("SIGN_KEY")
    val signingKeyPassphrase: String? = System.getenv("SIGN_KEY_PASSPHRASE")

    if (!signingKey.isNullOrBlank()) {
        if (signingKeyId?.isNotBlank() == true) {
            useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassphrase)
        } else {
            useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
        }

        publishing.publications.forEach { sign(it) }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}