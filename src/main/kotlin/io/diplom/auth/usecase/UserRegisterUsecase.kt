package io.diplom.auth.usecase

import io.diplom.auth.dto.inp.UserRegisterInput
import io.diplom.auth.dto.out.UserOutput
import io.diplom.common.security.models.AuthorityName
import io.diplom.exception.ExistException
import io.diplom.exception.GeneralException
import io.diplom.outer.user.models.UserEntity
import io.diplom.outer.user.models.UserRoles
import io.diplom.outer.user.repository.UserRepository
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

@WithTransaction
@ApplicationScoped
class UserRegisterUsecase(
    private val repository: UserRepository
) {

    /**
     * Регистрация пользователя
     */
    @Transactional
    @WithTransaction
    fun registerUser(user: UserRegisterInput): Uni<UserOutput> =
        repository.checkExistsUsername(user.email, user.username).flatMap {
            if (it) {
                Uni.createFrom().failure(ExistException())
            } else {
                Uni.createFrom().item(user)
            }
        }.flatMap { p ->
            user.toUserEntity().apply {
                this.password = BcryptUtil.bcryptHash(this.password!!)
            }.persist<UserEntity>()
        }.flatMap { us ->
            if (user.role == AuthorityName.ADMIN)
                Uni.createFrom().failure(GeneralException("Нельзя создать администратора", 500))
            else UserRoles(us, user.role)
                .persistAndFlush<UserRoles>()
                .map { us }
        }.map {
            UserOutput.fromEntity(it)
        }

}
