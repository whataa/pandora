package tech.linjiang.pandora.gradle

import com.android.build.api.transform.*
import com.android.build.api.transform.Status.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.compress.archivers.jar.JarArchiveEntry
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.parallel.InputStreamSupplier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.concurrent.Executors
import java.util.jar.JarFile
import java.util.zip.ZipFile

internal interface Transformer {
    // call N times.
    fun transform(clazz: ClassNode)
}

private const val CLAZZ = "class"

class PandoraTransform : Transform() {
    override fun getName() = "pandora"
    override fun isIncremental() = true
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    private val transformers = listOf<Transformer>(OkHttpTransform())

    override fun transform(context: TransformInvocation) {
        println("PandoraTransform: incremental=${context.isIncremental}")

        if (!context.isIncremental) {
            context.outputProvider.deleteAll()
            doFullTransform(context)
        } else {
            doIncrementalTransform(context)
        }
    }

    private fun doIncrementalTransform(context: TransformInvocation) {
        context.inputs
                .map { it.jarInputs }
                .flatten()
                .filter{ filterIncrementalJar(context, it) }
                .map { JarFile(it.file) to it.outputFile(context, Format.JAR) }
                .parallelStream()
                .forEach { it.transform(::asmTransform) }

        context.inputs
                .map { it.directoryInputs }
                .flatten()
                .map { filterIncrementalDirectory(context, it) }
                .flatten()
                .search()
                .parallelStream()
                .forEach { it.transform(::asmTransform) }
    }

    private fun doFullTransform(context: TransformInvocation) {
        context.inputs
                .map { it.directoryInputs }
                .flatten()
                .map { Entry(it.file, it.outputFile(context, Format.DIRECTORY)) }
                .search()
                .parallelStream()
                .forEach { it.transform(::asmTransform) }

        context.inputs
                .map { it.jarInputs }
                .flatten()
                .map { JarFile(it.file) to it.outputFile(context, Format.JAR) }
                .parallelStream()
                .forEach { it.transform(::asmTransform) }
    }


    private fun asmTransform(bytes: ByteArray): ByteArray {
        return ClassWriter(ClassWriter.COMPUTE_MAXS).also { writer ->
            val node = ClassNode().apply { ClassReader(bytes).accept(this, 0) }
            transform(node)
            node.accept(writer)
        }.toByteArray()
    }

    private fun transform(clazz: ClassNode) {
        transformers.forEach { it.transform(clazz) }
    }

}

private fun filterIncrementalJar(context: TransformInvocation, jar: JarInput) = when (jar.status!!) {
    REMOVED -> {
        println("delete jarInputs: ${jar.file}")
        jar.outputFile(context, Format.JAR).delete()
        false
    }
    NOTCHANGED -> false
    else -> {
        println("new/Changed jarInputs: ${jar.file}")
        true
    }
}

private fun filterIncrementalDirectory(context: TransformInvocation, dic: DirectoryInput) = mutableListOf<Entry>().apply {
    dic.changedFiles.forEach { (file, status) ->
        val output = File(dic.outputFile(context, Format.DIRECTORY), file.relativeTo(dic.file))
        when (status) {
            REMOVED -> {
                println("delete directoryInputs: $output")
                output.deleteRecursively()
            }
            CHANGED, ADDED -> {
                println("new/Changed directoryInputs: $file")
                add(Entry(file, output))
            }
            else -> Unit
        }
    }
}

private fun Entry.transform(transform: (ByteArray) -> ByteArray) {
    output.touch()
    when (output.extension.toLowerCase()) {
        CLAZZ -> {
            input.inputStream().use {
                transform(it.toBytes()).inputStream().copyTo(output.outputStream())
            }
        }
        else -> {
            input.copyTo(output, true)
        }
    }

}

private fun Pair<ZipFile, File>.transform(transform: (ByteArray) -> ByteArray) {
    val creator = ParallelScatterZipCreator(Executors.newWorkStealingPool())
    first.entries().asSequence().distinctBy { it.name }.forEach { entry ->
        val zae = JarArchiveEntry(entry)
        val stream = InputStreamSupplier {
            when (entry.name.substringAfterLast('.', "")) {
                CLAZZ -> first.getInputStream(entry).use { src ->
                    transform(src.toBytes()).inputStream()
                }
                else -> first.getInputStream(entry)
            }
        }

        creator.addArchiveEntry(zae, stream)
    }

    ZipArchiveOutputStream(second.touch()).use {
        creator.writeTo(it)
    }
}


private fun QualifiedContent.outputFile(invocation: TransformInvocation, format: Format) = invocation.outputProvider.getContentLocation(name, contentTypes, scopes, format)




