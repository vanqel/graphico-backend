package io.diplom.auth.dto.out

import io.diplom.works.models.WorkEntity

data class UserWorkOut(
    val user: UserOutput,
    val works: List<WorkEntity>
)
