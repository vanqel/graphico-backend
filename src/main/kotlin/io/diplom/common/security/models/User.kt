package io.diplom.common.security.models

import java.security.Principal
import java.time.LocalDateTime


class User(

    var id: Long,
    /**
     * Логин пользователя
     */
    var username: String,
    /**
     * Электронная почта пользователя
     */
    var email: String? = null,

    /**
     * О пользователе
     */
    var about: String? = null,

    /**
     * Скиллы пользователя
     */
    var skills: String? = null,

    /**
     * Скиллы пользователя
     */
    var directions: List<String>,

    /**
     * Признак блокировки пользователя
     */
    var isApproved: Boolean = false,

    /**
     * Дата регистрации пользователя
     */
    var createdAt: LocalDateTime,

    /**
     * Признак блокировки пользователя
     */
    var isBlocked: Boolean? = false,

    val roles: List<Authority>,

    val photoFileName: String? = null,

    var photoUri: String? = null
    ) : Principal {

    fun hasAuthority(
        authorityName: AuthorityName
    ): Boolean {
        return this.roles
            .any { a: Authority -> authorityName == a.name }
    }

    fun hasAuthority(
        vararg authorityName: AuthorityName
    ): Boolean {
        return this.roles
            .any { a: Authority -> a.name in authorityName }
    }


    val orgOidRestriction: String?
        get() = null

    fun hasAnyAuthority(authorityNames: Collection<AuthorityName>): Boolean {
        return true
    }

    override fun getName(): String = username!!
}
