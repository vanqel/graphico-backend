package io.diplom.auth.usecase

import io.diplom.auth.dto.inp.LoginInput
import io.diplom.auth.dto.out.AuthOutput
import io.diplom.auth.service.JwtProvider
import io.diplom.common.security.models.User
import io.diplom.exception.LoginException
import io.diplom.exception.PasswordException
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.outer.user.repository.UserRepository
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class UserAuthUsecase(
    private val jwtProvider: JwtProvider,
    private val repository: UserRepository,
    private val fileService: MinioService,
    private val userFetchUsecase: UserFetchUsecase
) {

    /**
     * Авторизация пользователя по логину
     */
    @WithTransaction
    fun loginByUsername(payload: String, ex: RoutingContext): Uni<User> =
        repository.findByUsername(payload).flatMap {
            it?.let {
                Uni.createFrom().item(it)
            } ?: Uni.createFrom().failure(LoginException())
        }.flatMap { user ->
            jwtProvider.setToken(user, ex)
        }.flatMap {

            val user = requireNotNull(it.user)

            val ph = user.avatar?.let {
                fileService.getObject(it.last().filename!!)
            } ?: uni { FileOutput.empty() }

            ph.map {
                user.toUser(it.uri)
            }
        }

    /**
     * Авторизация пользователя по логину, почте, телефону и паролю
     */
    @WithTransaction
    fun login(payload: LoginInput, ex: RoutingContext): Uni<AuthOutput> =
        repository.findByParams(payload.payload).flatMap {
            it ?: run { return@flatMap Uni.createFrom().failure(LoginException()) }

            if (BcryptUtil.matches(payload.password, it.password!!)) {
                Uni.createFrom().item(it)
            } else {
                Uni.createFrom().failure(PasswordException())
            }
        }.flatMap { user ->
            jwtProvider.setToken(user, ex)
        }

}
