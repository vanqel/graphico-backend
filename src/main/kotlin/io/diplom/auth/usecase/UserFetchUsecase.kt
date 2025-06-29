package io.diplom.auth.usecase

import io.diplom.common.security.models.User
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.outer.user.models.UserEntity
import io.diplom.outer.user.repository.UserRepository
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.enterprise.context.ApplicationScoped
import org.hibernate.query.Page.page

@WithTransaction
@ApplicationScoped
class UserFetchUsecase(
    private val repository: UserRepository,
    private val fileService: MinioService,
) {


    fun findById(id: Long): Uni<User> =
        repository.findById(id)
            .flatMap(this::getAvatar)
            .map(UserEntity::toUser)

    fun findByIds(ids: List<Long>): Uni<List<User>> =
        repository.findByIds(ids)
            .flatMap(this::getAvatar)
            .map { it.mapNotNull { it.toUser(false) } }


    /**
     * Поиск пользователей
     */
    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun allUsers(): Uni<List<User>> =
        repository.findAll(page(1000, 0))
            .flatMap(this::getAvatar)
            .map {
                it.mapNotNull { it.toUser(false) }
            }

    /**
     * Поиск пользователей
     */

    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun allUsersByDirection(direction: String): Uni<List<User>> =
        repository.findAllByDirection(direction, page(1000, 0))
            .flatMap(this::getAvatar)
            .map { it.map(UserEntity::toUser) }


    fun getAvatar(user: UserEntity): Uni<UserEntity> {

        val ph = user.avatar?.lastOrNull()?.let {
            fileService.getObject(it.filename!!)
        } ?: uni { FileOutput.empty() }

        return ph.map {
            user.avatar?.lastOrNull()?.uri = it.uri
            user
        }
    }


    fun getAvatar(users: List<UserEntity>): Uni<List<UserEntity>> {
        val unis = users.map { user ->

            val ph = user.avatar?.lastOrNull()?.let {
                    fileService.getObject(it.filename!!)
            } ?: uni { FileOutput.empty() }

            ph.map {
                user.avatar?.lastOrNull()?.uri = it.uri
                user
            }
        }.ifEmpty { return uni { emptyList() } }

        return Uni.combine().all().unis<UserEntity>(unis).with { it as List<UserEntity> }
    }
}
