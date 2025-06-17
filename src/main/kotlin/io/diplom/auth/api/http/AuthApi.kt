package io.diplom.auth.api.http

import io.diplom.auth.dto.inp.LoginInput
import io.diplom.auth.dto.inp.UserRegisterInput
import io.diplom.auth.usecase.UserAuthUsecase
import io.diplom.auth.usecase.UserRegisterUsecase
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "/auth")
class AuthApi(
    val authUsecase: UserAuthUsecase,
    val registerUsecase: UserRegisterUsecase
) {

    /**
     * Аутентификация пользователя
     */
    @Route(
        path = "/auth",
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun auth(
        @Body authRequest: LoginInput,
        ex: RoutingContext
    ) = authUsecase.login(authRequest, ex)

    /**
     * Регистрация пользователя
     */
    @Route(
        path = "/register",
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun register(
        @Body authRequest: UserRegisterInput,
        ex: RoutingContext
    ) = registerUsecase.registerUser(authRequest)


}
