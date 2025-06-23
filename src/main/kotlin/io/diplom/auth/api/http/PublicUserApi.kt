package io.diplom.auth.api.http

import io.diplom.auth.usecase.UserFetchUsecase
import io.diplom.works.usecase.WorkFetchUsecase
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@RouteBase(path = "public/user")
class PublicUserApi(
    private val userFetchUsecase: UserFetchUsecase
) {

    @Route(
        path = "list",
        methods = [Route.HttpMethod.GET],
    )
    fun listUser(
    ) = userFetchUsecase.allUsers()


    @Route(
        path = "search",
        methods = [Route.HttpMethod.GET],
    )
    fun findById(
        @Param id: Long?
    ) = userFetchUsecase.findById(id!!)
}
