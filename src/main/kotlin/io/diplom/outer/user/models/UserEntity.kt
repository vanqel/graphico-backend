package io.diplom.outer.user.models

import io.diplom.common.security.models.Authority
import io.diplom.common.security.models.User
import io.diplom.exception.AuthException
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.eclipse.microprofile.jwt.Claims
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity(

    /**
     * Логин пользователя
     */
    @Column(name = "username", nullable = false)
    var username: String? = null,

    /**
     * Имя пользователя
     */
    @Column(name = "name")
    var name: String? = null,

    /**
     * Пароль пользователя
     */
    @Column(name = "password")
    var password: String? = null,

    /**
     * Электронная почта пользователя
     */
    @Column(name = "email")
    var email: String? = null,

    /**
     * О пользователе
     */
    @Column(name = "about", columnDefinition = "text")
    var about: String? = null,

    /**
     * Скиллы пользователя
     */
    @Column(name = "skills", columnDefinition = "text")
    var skills: String? = null,

    /**
     * Скиллы пользователя
     */
    @Column(name = "directions")
    @ElementCollection(fetch = FetchType.EAGER)
    var directions: List<String> = emptyList(),

    /**
     * Дата регистрации пользователя
     */
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    /**
     * Признак блокировки пользователя
     */
    @Column(name = "is_approved")
    var isApproved: Boolean = false,


    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "uid_id", referencedColumnName = "id")
    val avatar: List<UserPhotos>? = null,

    /**
     * Признак блокировки пользователя
     */
    @Column(name = "is_blocked")
    var isBlocked: Boolean = false,


) : PanacheEntity() {



    /**
     * Роли пользователя
     */
    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "uid_id", referencedColumnName = "id")
    val roles: List<UserRoles> = emptyList()

    fun toUser(validate: Boolean = true): User {

        if (validate && (isBlocked || !isApproved)) throw AuthException("Вход не возможен")

        return User(
            id = id!!,
            username = username!!,
            email = email,
            roles = roles.map { Authority(it.role!!) },
            createdAt = createdAt,
            about = about,
            skills = skills,
            directions = directions,
            isApproved = isApproved,
            isBlocked = isBlocked,
            photoFileName = avatar?.firstOrNull()?.filename,
            photoUri = avatar?.firstOrNull()?.uri
        )
    }

    fun toUser(uri: String?, validate: Boolean = true): User {

        if (validate && (isBlocked || !isApproved)) throw AuthException("Вход не возможен")

        return User(
            id = id!!,
            username = username!!,
            email = email,
            roles = roles.map { Authority(it.role!!) },
            createdAt = createdAt,
            about = about,
            skills = skills,
            directions = directions,
            isApproved = isApproved,
            isBlocked = isBlocked,
            photoFileName = avatar?.firstOrNull()?.filename,
            photoUri = uri
        )
    }


}
