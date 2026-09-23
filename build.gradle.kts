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

// Forward test configuration from the Gradle invocation into the test JVMs, e.g.
// gradlew test -Djemmy.diagnostics.enabled=false
fun Test.forwardTestProperties() {
    listOf(
        "jemmy.diagnostics.enabled", // JemmyDiagnostics.ENABLED_PROPERTY
        "jemmy.testing.window.x",
        "jemmy.testing.window.y",
    ).forEach { key ->
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
                // DumpOnFailureTest drives the Launcher API directly; useJUnitJupiter only adds it at runtime
                implementation(libs.junit.platform.launcher)
                compileOnly(libs.jetbrains.annotations)
            }

            targets {
                all {
                    testTask.configure {
                        systemProperty("logback.configurationFile", "logback-automated-test.xml")
                        systemProperty("swing.defaultlaf", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
                        forwardTestProperties()
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
                // SaveScreenshotOnFailureExtensionTest uses the Launcher API at compile time
                implementation(libs.junit.platform.launcher)
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
                        forwardTestProperties()
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("userInterfaceTest"))
    dependsOn("checkLicenseHeaders")
}

tasks.register("compileAll") {
    description = "Compiles every source set (main, test, testFixtures, userInterfaceTest)."
    group = "build"
    dependsOn(tasks.withType<JavaCompile>())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// Every Java source must open with the GPLv2 + Classpath header. The 19e70d2 sweep applied it
// once; this check keeps files added afterwards from drifting.
tasks.register("checkLicenseHeaders") {
    description = "Fails when a Java source under src/ lacks the GPLv2 license header."
    group = "verification"
    val sources = fileTree("src") { include("**/*.java") }
    val baseDir = layout.projectDirectory.asFile
    inputs.files(sources)
    doLast {
        val missing = sources.filter { file ->
            file.useLines { lines -> lines.take(20).none { it.contains("GNU General Public License version 2 only") } }
        }.map { it.relativeTo(baseDir).path }
        if (missing.isNotEmpty()) {
            throw GradleException("Java sources missing the license header:\n  " + missing.joinToString("\n  "))
        }
    }
}
