import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers
import org.jmailen.gradle.kotlinter.tasks.InstallHookTask

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinter) apply false
}

val hook = "installKotlinterPrePushHook"

abstract class InstallCustomPrePushHookTask : InstallHookTask("pre-push") {
    override val hookContent =
        """
        if ! ${'$'}GRADLEW lintKotlin; then
            echo 1>&2 "\nlintKotlin found problems, running formatKotlin; commit the result and re-push"
            ${'$'}GRADLEW formatKotlin
            exit 1
        fi
        """.trimIndent()
}

tasks {
    register(hook, InstallCustomPrePushHookTask::class.java) {
        group = "build setup"
        description = "Installs Kotlinter Git pre-push hook"
    }
}

// https://stackoverflow.com/a/61285609
idea.project.settings {
    taskTriggers {
        afterSync(tasks.getByName(hook))
    }
}
