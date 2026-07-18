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
    compileOnly(libs.jetbrains.annotations)
    testFixturesCompileOnly(libs.jetbrains.annotations)
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
                runtimeOnly(libs.logback.classic)
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
                        // one JVM per test class, mirroring the jtreg othervm isolation
                        // these tests relied on (UIManager L&F and JemmyProperties are global)
                        maxParallelForks = 1
                        forkEvery = 1
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
