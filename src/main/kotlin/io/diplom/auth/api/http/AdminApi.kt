package io.diplom.auth.api.http

import io.diplom.auth.usecase.UserStateUsecase
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@RouteBase(path = "/admin-api")
class AdminApi(
    val accessUsecase: UserStateUsecase
) {

    @Route(
        path = "block",
        methods = [Route.HttpMethod.POST],
    )
    fun blockUser(
        @Param("id") id: String?
    ) = accessUsecase.blockUser(id!!.toLong())


    @Route(
        path = "access",
        methods = [Route.HttpMethod.POST],
    )
    fun approveUser(
        @Param("id") id: String?
    ) = accessUsecase.approvePerson(id!!.toLong())


}
