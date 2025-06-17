package io.diplom.auth.api.http

import io.diplom.auth.dto.inp.UserUpdateInput
import io.diplom.auth.usecase.UserUpdateUsecase
import io.diplom.common.security.configurator.getUser
import io.diplom.common.security.models.AuthorityName
import io.diplom.exception.AuthException
import io.diplom.outer.images.FileOutput
import io.diplom.outer.user.models.UserEntity
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "/user")
class UserApi(
    val updateUsecase: UserUpdateUsecase
) {


    /**
     * Security context, инициализируется на уровне запроса.
     */
    @Inject
    private lateinit var securityIdentity: SecurityIdentity

    /**
     * Аутентификация пользователя
     */
    @Route(
        path = "/me",
        methods = [Route.HttpMethod.GET],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun auth(
        ex: RoutingExchange
    ) = securityIdentity.getUser()

    /**
     * Аутентификация пользователя
     */
    @Route(
        path = "/me/update",
        methods = [Route.HttpMethod.PUT],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun upd(
        @Body personRequest: UserUpdateInput
    ): Uni<UserEntity> {
        val user = securityIdentity.getUser()
        if (personRequest.id != user.id && !user.hasAuthority(AuthorityName.ADMIN))
            throw AuthException()

        return updateUsecase.updatePerson(personRequest)
    }

    /**
     * Аутентификация пользователя
     */
    @Route(
        path = "/me/update/avatar",
        methods = [Route.HttpMethod.PUT],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun updAvatar(
        ex: RoutingExchange
    ): Uni<FileOutput> =
        updateUsecase.updateAvatarUser(ex.context().fileUploads().first())




}
