package io.diplom.works.api.http

import com.cronutils.model.field.value.SpecialChar
import io.diplom.works.models.WorkEntity
import io.diplom.works.usecase.WorkFetchUsecase
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "public/work")
class PublicWorkApi(
    private val workFetchUsecase: WorkFetchUsecase
) {
    @Route(
        path = "work",
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun findWork(
        @Param("id") id: Long?,
    ): Uni<WorkEntity> = workFetchUsecase.findByIdWrapped(id!!)

    @Route(
        path = "all",
        methods = [Route.HttpMethod.GET],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun getAllWorks(): Uni<List<WorkEntity>> = workFetchUsecase.findByAll()

    @Route(
        path = "user",
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun findByUser(
        @Param("id") id: Long?,
    ): Uni<List<WorkEntity>> = workFetchUsecase.findByUser(id!!)

    @Route(
        path = "category",
        methods = [Route.HttpMethod.GET],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun getAllWorksByCategory(
        @Param("search") category: String?
    ): Uni<List<WorkEntity>> = workFetchUsecase.findByCategory(WorkEntity.Category.valueOf(category!!))



    @Route(
        path = "style",
        methods = [Route.HttpMethod.GET],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun getAllWorksByStyle(
        @Param("search") style: String?
    ): Uni<List<WorkEntity>> = workFetchUsecase.findByStyle(style!!)

}
