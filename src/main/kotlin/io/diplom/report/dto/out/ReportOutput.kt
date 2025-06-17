package io.diplom.report.dto.out

import io.diplom.common.security.models.User

data class ReportOutput (
    val id: Long,
    val author: User,
    val workId: Long,
    val text: String,
)
