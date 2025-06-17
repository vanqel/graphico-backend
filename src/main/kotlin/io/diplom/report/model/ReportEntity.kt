package io.diplom.report.model

import io.diplom.models.LongEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "report")
@Entity
class ReportEntity(
    val userId: Long? = null,
    val workId: Long? = null,
    val text: String? = null,
    val rate: Int? = null
) : LongEntity()
