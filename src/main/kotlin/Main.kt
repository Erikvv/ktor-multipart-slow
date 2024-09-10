package com.zenmo.slow

import io.ktor.http.cio.CIOMultipartDataBase
import io.ktor.http.content.PartData
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.jvm.javaio.toByteReadChannel
import io.ktor.utils.io.readBuffer
import io.ktor.utils.io.readFully
import kotlinx.coroutines.runBlocking
import kotlinx.io.readString
import java.nio.ByteBuffer
import kotlin.random.Random
import kotlin.time.measureTime

@OptIn(InternalAPI::class, ExperimentalStdlibApi::class)
fun main() {
    val content = Random.nextBytes(3_000_000).toHexString()
    val boundary = "---------------------------190618344623159620833068499961"

    val payload: String = """
        --$boundary
        Content-Disposition: form-data; name="file"; filename="file.bin"
        Content-Type: text/plain

        $content
        --$boundary--
    """.trimIndent().replace("\n", "\r\n")

    runBlocking {
        val readTime = measureTime {
            val channel = payload.byteInputStream().toByteReadChannel(this.coroutineContext)
            val content = channel.readBuffer(20_000_000).readString()
            println(content)
        }
        println("Read time: $readTime")

        val parseTime = measureTime {
            val multipartData = CIOMultipartDataBase(
                coroutineContext = this.coroutineContext,
                channel = payload.byteInputStream().toByteReadChannel(this.coroutineContext),
                contentType = "multipart/form-data; boundary=$boundary",
                contentLength = null,
                formFieldLimit = 20_000_000,
            )

            val part = multipartData.readPart()
            if (part !is PartData.FileItem) {
                println("Not a file")
                return@measureTime
            }

            val buffer = ByteBuffer.allocate(20_000_000)
            val content = part.provider().readFully(buffer)
        }

        println("parse time: $parseTime")
    }
}