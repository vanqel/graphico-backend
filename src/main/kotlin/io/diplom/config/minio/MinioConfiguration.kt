package io.diplom.config.minio

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioAsyncClient
import io.quarkus.runtime.Startup
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.mutiny.core.Vertx
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty

@ApplicationScoped
class MinioConfiguration(
    private val minioClient: MinioAsyncClient,

    @ConfigProperty(name = "quarkus.minio.host")
    private val host: String,

    @ConfigProperty(name = "quarkus.minio.port")
    private val port: String,

    private val vertx: Vertx
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ApplicationScoped
    fun client() = MinioClient.Builder()
        .setVertx(vertx)
        .setUrl(host, port)
        .setDelegate(minioClient)
        .build()

    fun createIfNotExists(name: String): Uni<Void?>? {
        val existsReports = minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build())

        return Uni.createFrom().future(existsReports).flatMap {
            if (!it) {
                logger.info("Create bucket $name")
                Uni.createFrom().future(minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build()))
            } else {
                logger.info("Bucket $name validated")
                Uni.createFrom().nullItem()
            }
        }
    }

        @Startup
    @Suppress("unused")
    fun start() {
         createIfNotExists(DOCS)?.await()?.indefinitely()
    }

    companion object {
        const val DOCS = "graphico"
    }

}
