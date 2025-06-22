package io.diplom.works.usecase

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.auth.usecase.UserFetchUsecase
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.exception.GeneralException
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.works.models.WorkEntity
import io.diplom.works.repository.WorkRepository
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@WithTransaction
class WorkFetchUsecase(
    private val workRepository: WorkRepository,
    private val fileService: MinioService,
    private val jpqlEntityManager: JpqlEntityManager,
    private val userFetchUsecase: UserFetchUsecase
) {

    companion object {
        private val entity = entity(WorkEntity::class)
    }

    fun findById(id: Long) = workRepository.findById(id)

    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findByIds(ids: List<Long>): Uni<List<WorkEntity>> {
        val query = jpql {
            selectDistinct(entity).from(entity).where(
                entity
                    .path(WorkEntity::id)
                    .`in`(ids)
            )
        }

        return jpqlEntityManager.JpqlQuery()
            .getResultData(query, PaginationInput(0, 100))
            .flatMap(this::wrap)
    }

    fun findByIdWrapped(id: Long) = workRepository.findById(id).flatMap(this::wrap)

    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findByAll() = workRepository.findAll().list().flatMap(this::wrap)


    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findByCategory(category: WorkEntity.Category): Uni<List<WorkEntity>> {
        val query = jpql {
            selectDistinct(entity).from(entity).where(
                entity
                    .path(WorkEntity::category)
                    .eq(value(category))
            )
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 100)).flatMap(this::wrap)
    }


    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findByStyle(style: String): Uni<List<WorkEntity>> {
        val query = jpql {
            selectDistinct(entity).from(entity).where(
                entity
                    .path(WorkEntity::style)
                    .eq(value(style))
            )
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 100)).flatMap(this::wrap)
    }

    fun findByUser(userId: Long): Uni<List<WorkEntity>> {
        val query = jpql {
            selectDistinct(entity).from(entity).where(
                entity
                    .path(WorkEntity::userId)
                    .eq(value(userId))
            )
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 100))
            .flatMap(this::wrap)
    }

    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findByUsers(userIds: List<Long>): Uni<List<WorkEntity>> {
        val query = jpql {
            selectDistinct(entity).from(entity).where(
                entity
                    .path(WorkEntity::userId)
                    .`in`(userIds)
            )
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 100))
            .flatMap(this::wrap)
    }

    fun wrap(entity: WorkEntity?): Uni<WorkEntity> {

        entity ?: run {
            return Uni.createFrom().failure(GeneralException("Не найдена сущность"))
        }

        val unis = entity.images.map { ph ->
            fileService.getObject(ph.filename!!).map {
                ph.id!! to it
            }
        }


        return if (unis.isEmpty()) uni { entity }
        else Uni.combine().all().unis<Pair<Long, FileOutput>>(unis)
            .with { (it as List<Pair<Long, FileOutput>>).toMap() }
            .map { maps ->
                entity.images.forEach {
                    it.apply {
                        it.uri = maps[it.id]!!.uri
                    }
                }
                entity
            }
    }

    fun wrap(entity: List<WorkEntity>): Uni<List<WorkEntity>> {


        val userMapUni = userFetchUsecase.findByIds(entity.mapNotNull(WorkEntity::userId))
            .map {
                it.associateBy { it.id }
            }.map { userMap ->
                entity.forEach { e ->
                    e.user = userMap[e.userId!!]
                }
                entity
            }


        val unis = entity.flatMap { it.images }.map { ph ->
            fileService.getObject(ph.filename!!).map {
                ph.id!! to it
            }
        }


        val fillImagesUni = if (unis.isEmpty().not()) Uni.combine().all().unis<Pair<Long, FileOutput>>(unis)
            .with { (it as List<Pair<Long, FileOutput>>).toMap() }
            .map { maps ->
                entity.forEach { e ->
                    e.images.forEach {
                        it.apply {
                            it.uri = maps[it.id]!!.uri
                        }
                    }
                }
                entity
            }
        else uni { emptyList() }


        return userMapUni.call { e ->
            fillImagesUni
        }

    }

}
