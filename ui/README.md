# PHE Better Health UI Components for Android

## Requirements

- minimum sdk version: 21

## Installation

```groovy
// /build.gradle
allprojects {
    repositories {
        // local repo
        mavenLocal()
    }
}

// /app/build.gradle
dependencies {
    implementation "com.phe.betterhealth:ui-components:1.0.0-SNAPSHOT"
}
```

## Publishing

To maven local repository:

```sh
$ ./gradlew :lib:publishDebugPublicationToMavenLocal      // debug
$ ./gradlew :lib:publishReleasePublicationToMavenLocal    // release
$ ./gradlew :lib:publishToMavenLocal                      // both
```

