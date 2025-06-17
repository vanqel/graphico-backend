package io.diplom.report.usecase

import io.diplom.common.security.configurator.getUser
import io.diplom.report.dto.inp.ReportInput
import io.diplom.report.dto.out.ReportOutput
import io.diplom.report.model.ReportEntity
import io.diplom.report.repository.ReportPanacheRepository
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class ReportAddUsecase(
    private val reportRepository: ReportPanacheRepository,
    private val reportFetchUsecase: ReportFetchUsecase,
    private val securityIdentity: SecurityIdentity
) {

    fun add(input: ReportInput): Uni<ReportOutput> {

        val entity = ReportEntity(
            securityIdentity.getUser().id,
            input.workId,
            input.text,
            input.rate
        )

        return reportRepository.persistAndFlush(entity).flatMap (
            reportFetchUsecase::wrap
        )
    }
}
