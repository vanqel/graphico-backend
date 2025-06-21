package io.diplom.outer.images

import io.diplom.config.minio.MinioClient
import io.minio.GetPresignedObjectUrlArgs
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.http.Method
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.FileUpload
import io.vertx.mutiny.core.file.FileSystem
import java.io.ByteArrayInputStream
import java.util.*
import java.util.concurrent.TimeUnit

class MinioService(
    val client: MinioClient,
    val bucketName: String
) {
    val fs: FileSystem = client.vertx.fileSystem()

    fun getObject(name: String): Uni<FileOutput> {
        return client.getUri(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .`object`(name)
                .expiry(1, TimeUnit.HOURS)
                .build()
        ).map {
            FileOutput(it, name)
        }
    }


    fun <T> getObject(key: T, name: String): Uni<Pair<T,FileOutput>> {
        return client.getUri(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .`object`(name)
                .expiry(1, TimeUnit.HOURS)
                .build()
        ).map {
            key to FileOutput(it, name)
        }
    }



    fun delObject(name: String): Uni<Void> {
        return client.remObject(
            RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(name)
                .build(),
        )
    }

    fun addObject(name: String, obj: FileUpload): Uni<FileOutput> {

        return fs.readFile(obj.uploadedFileName()).map {
            val bytea = it.bytes
            val bais = ByteArrayInputStream(bytea)

            client.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(name)
                    .stream(bais, bytea.size.toLong(), -1)
                    .contentType(obj.contentType())
                    .build()
            )
        }.flatMap {
            getObject(name)
        }
    }

    fun addObject(obj: FileUpload): Uni<FileOutput> {
        val name = "${UUID.randomUUID()}_${obj.fileName()}"
        return addObject(name, obj)
    }
}
