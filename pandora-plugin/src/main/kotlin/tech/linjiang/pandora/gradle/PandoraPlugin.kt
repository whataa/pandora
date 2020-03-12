package tech.linjiang.pandora.gradle

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PandoraPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        if (project.plugins.hasPlugin("com.android.application")) {
            val android = project.extensions.getByType(AppExtension::class.java)
            android.registerTransform(PandoraTransform())
        } else {
            println("need be applied in 'com.android.application' module")
        }
    }

}