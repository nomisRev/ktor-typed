import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import util.csm.ProcessCsmTemplate
import kotlin.io.path.createDirectories

val kotlin = the<KotlinJvmProjectExtension>()
val catalog = the<VersionCatalogsExtension>().named("libs")

val mainSourceSet = kotlin.sourceSets.named("main")
val templatesDir = mainSourceSet.map {
    it.kotlin.srcDirs.single { dir -> dir.name == "kotlin" }.toPath().parent.resolve("templates")
}

templatesDir.get().createDirectories()

val sourcesDir = project.layout.buildDirectory.asFile.map {
    it.toPath().resolve("generated-sources/csm")
}

val processCsmTemplates =
    tasks.register<ProcessCsmTemplate>(
        "processCsmTemplates",
        catalog.findVersion("kotlin.compiler").get().requiredVersion,
        templatesDir,
        sourcesDir,
    )

mainSourceSet.configure {
    kotlin.srcDirs(processCsmTemplates.map { it.sourcesDir })
}
