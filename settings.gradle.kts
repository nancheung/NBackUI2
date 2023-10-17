pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://api.xposed.info/"){
            mavenContent {
                includeGroup("de.robv.android.xposed")
            }
        }
    }
}

rootProject.name = "NBackUI"
include(":app")
 