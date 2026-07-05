pluginManagement {
    // NOTE:
    // pluginManagement is evaluated in a special early settings-script scope,
    // so required property lookup is intentionally duplicated here.
    fun requiredProperty(name: String): String =
        providers.gradleProperty(name).orNull
            ?: throw GradleException("Missing required Gradle property: $name")

    val sresApiKey = requiredProperty("sresApiKey")
    val sresGradlePluginUrl = requiredProperty("sresGradlePluginUrl")
    val sresUser = requiredProperty("sresUser")

    repositories {
        maven {
            name = "sres-gradle-plugins-remote"
            url = uri(sresGradlePluginUrl)

            credentials {
                username = sresUser
                password = sresApiKey
            }
        }
    }
}

rootProject.name = "jemmy2"

fun requiredProperty(name: String): String =
    providers.gradleProperty(name).orNull
        ?: throw GradleException("Missing required Gradle property: $name")

val sresApiKey = requiredProperty("sresApiKey")
val sresRepoUrl = requiredProperty("sresRepoUrl")
val sresUser = requiredProperty("sresUser")

dependencyResolutionManagement {
    repositories {
        maven {
            name = "sres"
            url = uri(sresRepoUrl)

            credentials {
                username = sresUser
                password = sresApiKey
            }
        }
    }
}
