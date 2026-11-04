plugins {
    id("com.diffplug.spotless") version "8.10.4"
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target("decompiled-26.2 snapshot 4/**/*.java")

        googleJavaFormat("1.30.0")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}