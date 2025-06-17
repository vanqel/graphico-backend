package io.diplom.works.dto.inp

import io.diplom.works.models.WorkEntity

data class CreateWorkDTO(
    val title: String,
    val about: String,
    val category: WorkEntity.Category,
    val price: Double?,
    val style: String?
)
