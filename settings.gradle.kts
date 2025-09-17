pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // add
        maven { // add
            url = uri("https://jitpack.io")
            credentials {
                username = "Get the password key from your TL/PM."
                password = "Get the password key from your TL/PM."
            }
        }
    }
}

rootProject.name = "Caller_ID"
include(":sample")
