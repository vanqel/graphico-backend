package io.diplom.works.api.http

import io.diplom.works.dto.out.UserWorksOutputDTO
import io.diplom.works.models.WorkEntity
import io.diplom.works.usecase.WorkAuthorFetchUsecase
import io.diplom.works.usecase.WorkFetchUsecase
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
@RouteBase(path = "public/work")
class PublicWorkApi(
    private val workFetchUsecase: WorkFetchUsecase,
    private val workAuthorFetchUsecase: WorkAuthorFetchUsecase
) {
    @Route(
        path = "/search",
        methods = [Route.HttpMethod.GET]
    )
    fun findWork(
        @Param("id") id: Long?,
    ): Uni<WorkEntity> = workFetchUsecase.findByIdWrapped(id!!)

    @Route(
        path = "all",
        methods = [Route.HttpMethod.GET],
    )
    fun getAllWorks(): Uni<List<WorkEntity>> = workFetchUsecase.findByAll()

    @Route(
        path = "user",
        methods = [Route.HttpMethod.GET],
    )
    fun findByUser(
        @Param("id") id: Long?,
    ): Uni<UserWorksOutputDTO> = workAuthorFetchUsecase.getWorksUser(id!!)

    @Route(
        path = "category",
        methods = [Route.HttpMethod.GET],
    )
    fun getAllWorksByCategory(
        @Param("search") category: String?
    ): Uni<List<WorkEntity>> = workFetchUsecase.findByCategory(WorkEntity.Category.valueOf(category!!))



    @Route(
        path = "style",
        methods = [Route.HttpMethod.GET],
    )
    fun getAllWorksByStyle(
        @Param("search") style: String?
    ): Uni<List<WorkEntity>> = workFetchUsecase.findByStyle(style!!)

}
