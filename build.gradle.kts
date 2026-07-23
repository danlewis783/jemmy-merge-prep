plugins {
    `java-library`
    `java-test-fixtures`
}

group = "io.github.danlewis783"
version = "3.0.14-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jspecify)
    implementation(libs.slf4j)
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.slf4j)
    compileOnly(libs.jetbrains.annotations)
    testFixturesCompileOnly(libs.jetbrains.annotations)
    // compileOnly: JemmyStateResetExtension needs the extension API, but only test suites that
    // actually register the extension need JUnit at runtime
    testFixturesCompileOnly(libs.junit.jupiter.api)
}

// Forward the test-window placement overrides (see TestWindows in testFixtures) from the
// gradle invocation into the test JVMs, e.g. gradlew userInterfaceTest -Djemmy.testing.window.x=900
fun Test.forwardWindowPlacementProperties() {
    listOf("jemmy.testing.window.x", "jemmy.testing.window.y").forEach { key ->
        (providers.gradleProperty(key).orNull ?: providers.systemProperty(key).orNull)?.let {
            systemProperty(key, it)
        }
    }
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter(libs.versions.junit.jupiter.get())
            dependencies {
                implementation(libs.assertj.core)
                implementation(testFixtures(project()))
                implementation(libs.logback.classic)
                compileOnly(libs.jetbrains.annotations)
            }

            targets {
                all {
                    testTask.configure {
                        systemProperty("logback.configurationFile", "logback-automated-test.xml")
                        systemProperty("swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
                        forwardWindowPlacementProperties()
                    }
                }
            }
        }

        register<JvmTestSuite>("userInterfaceTest") {
            useJUnitJupiter(libs.versions.junit.jupiter.get())
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation(libs.assertj.core)
                runtimeOnly(libs.logback.classic)
                compileOnly(libs.jetbrains.annotations)
            }

            targets {
                all {
                    testTask.configure {
                        // one sequential JVM for the whole suite; JemmyStateResetExtension
                        // (registered via @ExtendWith on every test class) restores all
                        // process-wide state between classes, replacing the former
                        // forkEvery = 1 jtreg-othervm-style isolation
                        maxParallelForks = 1
                        shouldRunAfter(tasks.test)
                        systemProperty("logback.configurationFile", "logback-automated-test.xml")
                        systemProperty("swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
                        forwardWindowPlacementProperties()
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("userInterfaceTest"))
}

tasks.register("compileAll") {
    description = "Compiles every source set (main, test, testFixtures, userInterfaceTest)."
    group = "build"
    dependsOn(tasks.withType<JavaCompile>())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
