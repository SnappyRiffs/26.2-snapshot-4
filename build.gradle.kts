plugins {
    id("com.diffplug.spotless") version "8.10.4"
}

spotless {
    java {
        // Change this to match your CFR output folder
        target("decompiled/**/*.java")

        googleJavaFormat("1.17.0")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}