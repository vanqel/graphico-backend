package io.diplom.auth.usecase

import io.diplom.common.security.configurator.getUser
import io.diplom.common.security.models.AuthorityName
import io.diplom.exception.AuthException
import io.diplom.outer.user.repository.UserRepositoryPanache
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject

@WithTransaction
@ApplicationScoped
class UserStateUsecase(
    private val repository: UserRepositoryPanache
) {

    /**
     * Security context, инициализируется на уровне запроса.
     */
    @Inject
    private lateinit var securityIdentity: SecurityIdentity

    @WithTransaction
    fun approvePerson(id: Long): Uni<Boolean> {
        if (!securityIdentity.getUser().hasAuthority(AuthorityName.ADMIN)) {
            throw AuthException("Нет прав")
        }
        return repository.update("SET isApproved = true WHERE id = ?1", id)
            .map { it > 0 }
    }

    /**
     * Блокировка / разблокировка пользователя
     */
    @WithTransaction
    fun blockUser(id: Long): Uni<Boolean> {
        if (!securityIdentity.getUser().hasAuthority(AuthorityName.ADMIN)) {
            throw AuthException("Нет прав")
        }
        return repository.update("SET isBlocked = (not isBlocked) where id = ?1", id)
            .map { it > 0 }
    }

}
