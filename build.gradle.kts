import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    `java-test-fixtures`
    id("com.diffplug.spotless") version "8.8.0"
    id("net.ltgt.errorprone") version "4.0.1"
}

group = "io.github.danlewis783"
version = "3.0.14-SNAPSHOT"

java {
    // Compile with 17 (Error Prone/NullAway need a >=11 compiler to run) but keep
    // emitting Java 8 bytecode via release below, so the artifact stays Java 8.
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jspecify)
    implementation(libs.slf4j)
    testFixturesImplementation(libs.assertj.core)

    // Null-checking: NullAway runs as an Error Prone plugin (configured below, on every source set).
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            useJUnitJupiter(libs.versions.junit.jupiter.get())
            dependencies {
                implementation(libs.assertj.core)
                implementation(testFixtures(project()))
                runtimeOnly(libs.logback.classic)
            }

            targets {
                all {
                    testTask.configure {
                        systemProperty("logback.configurationFile", "logback-automated-test.xml")
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
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("userInterfaceTest"))
}

// NullAway only (not the rest of Error Prone), over the project's packages, on
// every source set - this is a nullness audit, not a general lint pass.
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(8)
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000", "-Xlint:rawtypes,unchecked"))
    options.errorprone {
        isEnabled.set(true)
        disableAllChecks.set(true)
        check("NullAway", CheckSeverity.ERROR)
        option("NullAway:AnnotatedPackages", "org.netbeans.jemmy")
        // assertThat(x).isNotNull() counts as a null check in test code
        option("NullAway:HandleTestAssertionLibraries", "true")
        // JUnit injects @TempDir fields before any initializer runs
        option("NullAway:ExcludedFieldAnnotations", "org.junit.jupiter.api.io.TempDir")
    }
}

spotless {
    java {
        target("src/**/*.java")
        removeUnusedImports()
        palantirJavaFormat()
    }
}
