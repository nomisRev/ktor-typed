val propertiesKotlinVersion: String by settings

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))

            val kotlinVersion = gradle.startParameter.projectProperties["kotlinVersion"]
                ?: System.getenv("KOTLIN_VERSION")

            if (kotlinVersion != null) {
                version("kotlin", kotlinVersion)
            }

            val kotlinCompilerVersion =
                gradle.startParameter.projectProperties["kotlinCompilerVersion"]
                    ?: System.getenv("KOTLIN_COMPILER_VERSION")
                    ?: kotlinVersion

            version("kotlin-compiler", kotlinCompilerVersion)
        }
    }
}