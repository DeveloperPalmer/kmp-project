pluginManagement {
  includeBuild("build-conventions")
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
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "kmp-base-project"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
  ":android:app",
  ":shared",
  ":shared:resources",
  ":shared:core:domain",
  ":shared:core:data",
  ":shared:core:ui",
  ":shared:core:uikit",
  ":shared:core:routing",
  ":shared:core:component",
  ":shared:feature:app:routing",
  ":shared:feature:home-tabs:domain",
  ":shared:feature:home-tabs:ui",
  ":shared:feature:home-tabs:routing",
  ":shared:feature:cities:domain",
  ":shared:feature:cities:data",
  ":shared:feature:cities:ui",
  ":shared:feature:cities:ui-views",
  ":shared:feature:cities:routing",
  ":shared:feature:map:domain",
  ":shared:feature:map:data",
  ":shared:feature:map:ui",
  ":shared:feature:map:routing",
  ":shared:lib:annotation",
  ":lib:generator",
)
