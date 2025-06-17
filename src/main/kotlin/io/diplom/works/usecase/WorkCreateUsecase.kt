package io.diplom.works.usecase

import io.diplom.common.security.configurator.getUser
import io.diplom.common.security.models.AuthorityName
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.exception.AuthException
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.works.dto.inp.AttachImagesWorkDTO
import io.diplom.works.dto.inp.CreateWorkDTO
import io.diplom.works.models.WorkEntity
import io.diplom.works.models.WorkPhotoEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class WorkCreateUsecase(
    private val jpqlEntityManager: JpqlEntityManager,
    private val securityIdentity: SecurityIdentity,
    private val fetchUsecase: WorkFetchUsecase,
    private val fileService: MinioService
) {

    fun create(input: CreateWorkDTO): Uni<WorkEntity> {
        val entity = WorkEntity(
            securityIdentity.getUser().id,
            input.title,
            input.about,
            mutableListOf(),
            WorkEntity.Status.IN_WORK,
            input.category,
            input.price,
            input.style
        )

        return jpqlEntityManager.save(entity)
    }


    fun attachImages(input: AttachImagesWorkDTO): Uni<WorkEntity> {

        val entity = fetchUsecase.findById(input.workId)
        val user = securityIdentity.getUser()

        return entity.flatMap {
            if (it.userId != user.id || !user.hasAuthority(AuthorityName.ADMIN))
                Uni.createFrom().failure(AuthException())
            else uni { it }
        }.flatMap { workEntity ->

            val unis = input.images.map { ph ->
                fileService.addObject(ph)
            }

            Uni.combine()
                .all()
                .unis<FileOutput>(unis)
                .with { it as List<FileOutput> }
                .map {
                    it.map {

                        WorkPhotoEntity(
                            workId = workEntity.id!!,
                            filename = it.filename,
                        ).apply {
                            this.uri = it.uri
                        }

                    }
                }.flatMap { list ->
                    val filenameToUri = list.associate { it.filename!! to it.uri }
                    val unis = list.map {
                        jpqlEntityManager.save(it).map {
                            it.uri = filenameToUri[it.filename]
                        }
                    }

                    Uni.combine()
                        .all()
                        .unis<FileOutput>(unis)
                        .with { it as List<WorkPhotoEntity> }
                }.flatMap {

                    val filenameToUri = it.associate { it.filename!! to it.uri }

                    workEntity.images.addAll(it)

                    jpqlEntityManager.save(workEntity).map { entity ->
                        entity.images.forEach { photoEntity ->
                            photoEntity.uri = filenameToUri[photoEntity.filename]!!
                        }
                        entity
                    }
                }
        }
    }
}
