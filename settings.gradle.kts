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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "The Pet Shop App"
include(":app")

include(":ui:theme:")
include(":features:common:")
include(":features:store:")

include(":repositories:domain:")
include(":repositories:pets:")
include(":repositories:cart:")

include(":services:network:")
include(":services:database:")