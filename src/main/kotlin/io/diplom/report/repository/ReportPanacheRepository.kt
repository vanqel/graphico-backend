package io.diplom.report.repository

import io.diplom.report.model.ReportEntity
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ReportPanacheRepository : PanacheRepository<ReportEntity>
