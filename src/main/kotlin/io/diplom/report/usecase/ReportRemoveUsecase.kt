package io.diplom.report.usecase

import io.diplom.common.security.configurator.getUser
import io.diplom.exception.AuthException
import io.diplom.report.repository.ReportPanacheRepository
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class ReportRemoveUsecase(
    private val reportRepository: ReportPanacheRepository,
    private val securityIdentity: SecurityIdentity,
) {

    fun delete(id: Long): Uni<Boolean> {
        return reportRepository.findById(id).flatMap { entity ->
            if (entity.userId != securityIdentity.getUser().id)
                Uni.createFrom().failure { AuthException() }
            else reportRepository.delete(entity)
        }.map { true }
    }

}
