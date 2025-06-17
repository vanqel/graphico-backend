package io.diplom.works.usecase

import io.diplom.auth.usecase.UserFetchUsecase
import io.diplom.common.security.models.User
import io.diplom.works.dto.out.UserWorksOutputDTO
import io.diplom.works.models.WorkEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class WorkAuthorFetchUsecase(
    val workFetchUsecase: WorkFetchUsecase,
    val userFetchUsecase: UserFetchUsecase
) {

    fun findUsersAndWorks(direction: String) = userFetchUsecase.allUsersByDirection(direction)
        .flatMap { users ->
            val ids = users.map(User::id)
            workFetchUsecase.findByUsers(ids)
                .map { works -> wrapDto(works, users) }
        }


    fun findUsersAndWorks() = userFetchUsecase.allUsers()
        .flatMap { users ->
            val ids = users.map(User::id)
            workFetchUsecase.findByUsers(ids)
                .map { works -> wrapDto(works, users) }
        }


    private fun wrapDto(works: List<WorkEntity>, users: List<User>): List<UserWorksOutputDTO> {

        val userMap = users.associateBy(User::id)

        return works.groupBy(WorkEntity::userId)
            .map { (userId, works) ->
                UserWorksOutputDTO(userMap.getValue(userId!!), works)
            }
    }
}
