package tech.linjiang.pandora.gradle

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.RecursiveTask
import kotlin.math.max

internal fun File.touch(): File {
    if (!exists()) {
        parentFile?.mkdirs()
        val fileTrue = createNewFile()
        if (!fileTrue) {
            println("Warn! create transformed file failed: $this")
        }
    } else {
        println("Warn! file exists: $this")
    }
    return this
}

internal fun File.relativeTo(root: File) = this.toURI().relativize(root.toURI()).path

internal fun Collection<Entry>.search(): Collection<Entry> {
    val pool = ForkJoinPool()
    val result = pool.invoke(SearchTask(this))
    pool.shutdown()
    return result
}

data class Entry(val input: File, val output: File)

// 1. 全量时，输入是目录，因此首先走的是isDirectory；
// 2. 增量时，输入有可能是单个file，因此output需要拼接
internal class SearchTask(private val entries: Collection<Entry>) : RecursiveTask<Collection<Entry>>() {
    private val tasks = mutableListOf<RecursiveTask<Collection<Entry>>>()
    private val result = mutableSetOf<Entry>()

    override fun compute(): Collection<Entry> {
        entries.forEach { entry ->
            when {
                entry.input.isDirectory -> {
                    entry.input.listFiles()?.map { child ->
                        Entry(child, File(entry.output, entry.input.relativeTo(child)))
                    }?.let {
                        SearchTask(it).also { task ->
                            tasks.add(task)
                            task.fork()
                        }
                    }
                }
                entry.input.isFile -> {
//                    println("isFile: $entry")
                    result.add(entry)
                }
            }
        }

        return result + tasks.flatMap { it.join() }
    }
}

internal fun InputStream.toBytes(bufferSize: Int = 8 * 1024): ByteArray {
    val out = ByteArrayOutputStream(max(bufferSize, this.available()))
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytes = read(buffer)
    }
    return out.toByteArray()
}