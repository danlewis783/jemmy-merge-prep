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

// The platform's native look and feel, as UIManager.getSystemLookAndFeelClassName() would pick
// it. Elsewhere (Linux) this stays unset so Swing uses its cross-platform default, Metal: Swing
// only picks GTK on a GNOME desktop, which headless test runs lack.
val nativeLookAndFeel: String? = providers.systemProperty("os.name").get().let { os ->
    when {
        os.startsWith("Windows") -> "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
        os.startsWith("Mac") -> "com.apple.laf.AquaLookAndFeel"
        else -> null
    }
}

// Forward test configuration from Gradle properties into the test JVMs. -P is the only way in
// (or a gradle.properties file); a -D of the same key is ignored with a warning. For example:
// gradlew test -Pjemmy.diagnostics.enabled=false
// gradlew test -Pswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel
fun Test.forwardTestProperties() {
    nativeLookAndFeel?.let { systemProperty("swing.defaultlaf", it) }
    listOf(
        "jemmy.diagnostics.enabled", // JemmyDiagnostics.ENABLED_PROPERTY
        "jemmy.testing.window.x",
        "jemmy.testing.window.y",
        "swing.defaultlaf",
    ).forEach { key ->
        val value = providers.gradleProperty(key).orNull
        if (value != null) {
            systemProperty(key, value)
        } else if (providers.systemProperty(key).isPresent) {
            logger.warn("Ignoring -D$key for the test JVMs; pass it as -P$key")
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
                        forwardTestProperties()
                        // X11 only (XToolkit reads it; other toolkits ignore it): poll for X events
                        // at most every 10 ms. The default adaptive timeout underflows (uint32 0 - 1)
                        // into a blocking poll(), and a request another thread then issues without
                        // a flush, such as XSetInputFocus, waits unsent until some unrelated X event
                        // arrives. A fixed timeout skips that arithmetic; 10 ms keeps focus changes
                        // as quick as the default (100 ms slowed the median from 5 to 60 ms).
                        environment("_AWT_STATIC_POLL_TIMEOUT", "10")
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
