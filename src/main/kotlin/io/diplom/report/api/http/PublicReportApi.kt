package io.diplom.report.api.http

import io.diplom.report.dto.out.ReportOutput
import io.diplom.report.usecase.ReportFetchUsecase
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@RouteBase(path = "public/report")
@ApplicationScoped
class PublicReportApi(
    val reportFetchUsecase: ReportFetchUsecase,
) {

    @Route(
        path = "search",
        methods = [Route.HttpMethod.GET],
    )
    fun getReportForWork(
        @Param workId: Long?
    ): Uni<List<ReportOutput>> = reportFetchUsecase.findForWork(workId!!)

}
