package io.diplom.works.usecase

import io.diplom.works.dto.out.UserWorksOutputDTO
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class WorkAuthorFetchUsecase(
    val workFetchUsecase: WorkFetchUsecase
) {

    fun getWorksUser(userId: Long): Uni<UserWorksOutputDTO> = workFetchUsecase.findByUser(userId)
        .map {
            it.groupBy { it.user }.toMap().map {
                UserWorksOutputDTO(it.key!!, it.value)
            }.first()
        }
}
