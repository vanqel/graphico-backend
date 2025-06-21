package io.diplom.outer.user.repository

import io.diplom.outer.user.models.UserEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.jwt.Claims
import org.hibernate.query.Page
import org.hibernate.reactive.mutiny.Mutiny

@ApplicationScoped
class UserRepository(
    val entityManager: Mutiny.SessionFactory,
    val personRepo: UserPhotoRepositoryPanache,
) {

    /**
     * Поиск пользователя по параметрам
     */
    fun findAll(page: Page): Uni<List<UserEntity>> = entityManager.withSession { session ->
        session.createQuery(
            "select u from UserEntity u",
            UserEntity::class.java
        ).setPage(page).resultList
    }


    /**
     * Поиск пользователя по параметрам
     */
    fun findByIds(ids: List<Long>): Uni<List<UserEntity>> = entityManager.withSession { session ->
        session.createQuery(
            "select u from UserEntity u where id in (:ids)",
            UserEntity::class.java
        ).setParameter("ids", ids).resultList
    }


    /**
     * Поиск пользователя по параметрам
     */
    fun findById(id: Long): Uni<UserEntity> = entityManager.withSession { session ->
        session.createQuery(
            "select u from UserEntity u where id = :id",
            UserEntity::class.java
        ).setParameter("id", id).singleResult
    }

    /**
     * Поиск пользователя по параметрам
     */
    fun findAllByDirection(direction: String, page: Page): Uni<List<UserEntity>> =
        entityManager.withSession { session ->
            session.createQuery(
                "select u from UserEntity u where :direction in elements(directions) ",
                UserEntity::class.java
            ).setPage(page)
                .setParameter("direction", direction)
                .resultList
        }

    /**
     * Поиск пользователя по параметрам
     */
    fun findByUsername(username: String): Uni<UserEntity> = entityManager.withSession { session ->
        session.createQuery(
            "select u from UserEntity u join fetch u.roles r where u.username = :username",
            UserEntity::class.java
        ).setParameter("username", username)
            .singleResultOrNull
    }


    /**
     * Поиск пользователя по параметрам
     */
    @WithTransaction
    fun findByParams(payload: String): Uni<UserEntity?> =
        entityManager.withSession { session ->
            session.createQuery(
                "select u from UserEntity u join fetch u.roles r where u.username = :username or u.email = :email",
                UserEntity::class.java
            ).setParameter("username", payload)
                .setParameter("email", payload)
                .singleResultOrNull
        }

    /**
     * Проверка на существование пользователя по параметрам
     */
    @WithTransaction
    fun checkExistsUsername(email: String?, username: String?): Uni<Boolean> =
        entityManager.withSession { session ->
            session.createQuery(
                "select exists(select 1  from UserEntity u where email = :email or username = :username)",
                Boolean::class.java
            ).setParameter("email", email)
                .setParameter("username", username)
                .singleResultOrNull

        }


    /**
     * Проверка на существование пользователя по параметрам
     */
    @WithTransaction
    fun checkExistsUsername(username: String?): Uni<Boolean> =
        entityManager.withSession { session ->
            session.createQuery(
                "select exists(select 1 from UserEntity u where username = :username)",
                Boolean::class.java
            ).setParameter("username", username)
                .singleResultOrNull
        }


    /**
     * Проверка на существование пользователя по параметрам
     */
    @WithTransaction
    fun checkExistsEmail(email: String?): Uni<Boolean> =
        entityManager.withSession { session ->
            session.createQuery(
                "select exists(select 1 from UserEntity u where email = :email)",
                Boolean::class.java
            ).setParameter("email", email)
                .singleResultOrNull
        }


    /**
     * Проверка на существование пользователя по параметрам
     */
    @WithTransaction
    fun blockUnblockUser(id: Long): Uni<Boolean> =
        personRepo.update("set isBlocked = (not isBlocked) where id = :id", id)
            .map { true }

}
