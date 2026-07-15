# Publishing `mvvm-base` as an AAR to GitHub Packages

This documents the full workflow for turning the `mvvm-base` module into a
reusable library, publishing it to a remote Maven repository (GitHub
Packages), and consuming it back in the `app` module. Use this as a reference
next time you need to publish a new version, or create a similar library
module from scratch.

## 0. Creating the module (if starting from scratch)

`mvvm-base` already existed in this project, created via Android Studio's
module wizard:

1. **File → New → New Module...**
2. Choose **Android Library**
3. Set the module name (`mvvm-base`) and package name (`com.example.mvvmbase`)
4. Android Studio adds `include(":mvvm-base")` to `settings.gradle.kts`
   automatically and scaffolds `build.gradle.kts`, `AndroidManifest.xml`,
   `consumer-rules.pro`, etc.

An Android Library module differs from an app module mainly in that it
applies `com.android.library` instead of `com.android.application`, and has
no `applicationId` — it produces an **AAR** instead of an installable APK.

## 1. Concepts

- **AAR (Android Archive)** — the Android equivalent of a `.jar`. Bundles
  compiled classes, resources, the manifest, and consumer ProGuard rules.
  Running `./gradlew :mvvm-base:assembleRelease` already produces one at
  `mvvm-base/build/outputs/aar/`, but it just sits there locally.
- **Publishing** — uploading that AAR + metadata (group/artifact/version) to
  a **Maven repository** so other projects can declare it as a normal
  dependency (`implementation("group:artifact:version")`) instead of copying
  files by hand.
- **`maven-publish`** — the built-in Gradle plugin that does the publishing.
  It wraps the AAR into a "publication" and uploads it to a repository you
  configure, using credentials you provide.
- This mechanism is identical across GitHub Packages, GitLab Package
  Registry, and Nexus/Artifactory — only the repository URL and credential
  type change.

## 2. `mvvm-base/build.gradle.kts` — plugin, variant, and publication

```kotlin
plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

android {
    // ...existing android {} config...

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // ...existing dependencies...
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.dwightperello"
                artifactId = "mvvm-base"
                version = "1.0.0"
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/dwightperello/CineFind")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USER")
                    password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
```

Notes:
- `singleVariant("release")` tells AGP to expose the `release` build variant
  as a component (`components["release"]`) that `maven-publish` can consume.
  `withSourcesJar()` bundles a `-sources.jar` alongside the AAR.
- The whole `publishing {}` block is wrapped in `afterEvaluate` because
  `components["release"]` doesn't exist until AGP finishes configuring the
  variant — a quirk specific to Android library modules (not needed for
  plain Kotlin/Java libraries).
- `groupId:artifactId:version` (`com.github.dwightperello:mvvm-base:1.0.0`)
  is the coordinate consumers will use.

## 3. Credentials — GitHub Personal Access Token

Never put real credentials in a committed `build.gradle.kts` or the
project's `gradle.properties`. Store them in `~/.gradle/gradle.properties`
(a file in your home directory, global to all Gradle projects on the
machine, never checked into git).

**Generate the token** (github.com → profile photo → Settings → Developer
settings → Personal access tokens → Tokens (classic) → Generate new token
(classic)):
- Scope: **`write:packages`** (also grants `read:packages`)
- Copy it immediately — GitHub only shows it once.

**Store it:**

```bash
touch ~/.gradle/gradle.properties
open -e ~/.gradle/gradle.properties   # or any editor
```

Add:

```properties
gpr.user=dwightperello
gpr.token=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

Gradle automatically merges this file with the project's `gradle.properties`
at build time, so no project file needs to reference the token value
directly.

## 4. Running the publish task

The task name follows the pattern
`publish<PublicationName>PublicationTo<RepositoryName>Repository`:

```bash
./gradlew :mvvm-base:publishReleasePublicationToGitHubPackagesRepository
```

Or via Android Studio's Gradle tool window: **mvvm-base → Tasks →
publishing → publishReleasePublicationToGitHubPackagesRepository**.

> If the `publishing` task group isn't visible in the Gradle tool window,
> enable **Settings → Experimental → "Configure all Gradle tasks during
> Gradle Sync"**, then **File → Sync Project with Gradle Files**.

**Versions are immutable** — publishing `1.0.0` a second time fails with
`409 Conflict`. To publish a new version of `mvvm-base`, bump the `version`
in step 2's `publications` block (e.g. `1.0.0` → `1.0.1`) before re-running
the publish task.

Verify the upload on GitHub: repo → **Packages** (right sidebar) →
`mvvm-base`.

## 5. Consuming it (including from this same repo's `app` module)

**Add the repository** — in `settings.gradle.kts`, inside
`dependencyResolutionManagement { repositories { ... } }`:

```kotlin
maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/dwightperello/CineFind")
    credentials {
        username = providers.gradleProperty("gpr.user").getOrElse(System.getenv("GITHUB_USER") ?: "")
        password = providers.gradleProperty("gpr.token").getOrElse(System.getenv("GITHUB_TOKEN") ?: "")
    }
}
```

This makes the repository available to every module in the project.
GitHub Packages requires authentication even to *read* public packages
(unlike Maven Central), so any consuming project needs `gpr.user`/
`gpr.token` configured the same way as step 3.

**Add the dependency** — in the consuming module's `build.gradle.kts`:

```kotlin
implementation("com.github.dwightperello:mvvm-base:1.0.0")
```

In this repo, `app/build.gradle.kts` previously referenced a manually
copied local file (`implementation(files("libs/mvvm-base-release.aar"))`).
That line and the local `app/libs/mvvm-base-release.aar` file were removed
once the remote dependency was confirmed working — the whole point of
publishing is to stop hand-copying AARs between projects.

## 6. Why keep the `mvvm-base` module around

Even though `app` now consumes the *published* artifact, the module itself
stays in the repo as the editable source of truth. Deleting it would mean
the only copy of `BaseActivity`/`BaseViewModel`/`SingleLiveEvent`/
`NavigationRequest` is a compiled AAR on GitHub Packages — any future change
would require recreating a source module first. The normal pattern is:
keep developing inside the module, and publish a new version (bump +
re-run the publish task) whenever you want to share a new revision.

## Summary of the full loop

1. Android Library module (`mvvm-base`) contains the source.
2. `maven-publish` + `singleVariant("release")` + `MavenPublication`
   package it as an AAR with metadata.
3. Credentials in `~/.gradle/gradle.properties` authenticate the upload.
4. `publishReleasePublicationToGitHubPackagesRepository` uploads it to
   GitHub Packages.
5. Any project (including this one's `app` module) adds the
   `GitHubPackages` repository + the `implementation(...)` coordinate to
   consume it like any other library.
6. New changes to `mvvm-base` → bump `version` → re-publish → bump the
   consumer's dependency version.
