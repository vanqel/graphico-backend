package io.diplom.report.api.http

import io.diplom.report.dto.inp.ReportInput
import io.diplom.report.dto.out.ReportOutput
import io.diplom.report.usecase.ReportAddUsecase
import io.diplom.report.usecase.ReportFetchUsecase
import io.diplom.report.usecase.ReportRemoveUsecase
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@RouteBase(path = "/report")
@ApplicationScoped
class ReportApi(
    val reportAddUsecase: ReportAddUsecase,
    val reportRemoveUsecase: ReportRemoveUsecase
) {

    @Route(
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun add(
        @Body input: ReportInput
    ): Uni<ReportOutput> = reportAddUsecase.add(input)

    @Route(
        path = "remove",
        methods = [Route.HttpMethod.DELETE],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun remove(
        @Param id: Long?
    ): Uni<Boolean> = reportRemoveUsecase.delete(id!!)

}
