package io.diplom.works.usecase

import io.diplom.common.security.configurator.getUser
import io.diplom.common.security.models.AuthorityName
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.exception.AuthException
import io.diplom.works.dto.inp.ChangeStatusWorkDTO
import io.diplom.works.models.WorkEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class WorkStatusUsecase(
    private val jpqlEntityManager: JpqlEntityManager,
    private val securityIdentity: SecurityIdentity,
    private val fetchUsecase: WorkFetchUsecase
) {

    fun changeStatus(input: ChangeStatusWorkDTO): Uni<WorkEntity> {
        val user = securityIdentity.getUser()
        return fetchUsecase.findById(input.id).flatMap {
            if (it.userId != user.id || !user.hasAuthority(AuthorityName.ADMIN))
                Uni.createFrom().failure(AuthException())
            else uni {
                it.status = input.status
                it
            }
        }.flatMap(jpqlEntityManager::save)
            .flatMap(fetchUsecase::wrap)
    }

}
