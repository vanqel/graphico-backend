package io.diplom.report.dto.inp

import io.diplom.exception.GeneralException

data class ReportInput(
    val workId: Long,
    val rate: Int,
    val text: String,
) {
    init {
        require(rate < 10) { throw GeneralException("Некорректный рейтинг") }
    }
}
