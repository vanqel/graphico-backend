package io.diplom.auth.dto.inp

data class UserUpdateInput(
    val id: Long,
    val password: String?,
    val name: String?,
    val username: String?,
    val about: String?,
    val skills: String?,
    val directions: List<String>?,
    val email: String?,
)
