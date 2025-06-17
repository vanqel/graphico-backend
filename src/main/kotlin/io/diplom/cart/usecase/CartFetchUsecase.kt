package io.diplom.cart.usecase

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.cart.model.CartEntity
import io.diplom.common.security.configurator.getUser
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.works.models.WorkEntity
import io.diplom.works.usecase.WorkFetchUsecase
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class CartFetchUsecase(
    private val workFetchUsecase: WorkFetchUsecase,
    private val securityIdentity: SecurityIdentity,
    private val jpqlEntityManager: JpqlEntityManager
) {
    companion object {
        val entity = entity(CartEntity::class)
    }

    fun findForUserCart(): Uni<List<CartEntity>> {

        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .whereAnd(
                    entity.path(CartEntity::userId)
                        .eq(securityIdentity.getUser().id),

                    entity.path(CartEntity::status)
                        .eq(CartEntity.Status.IN_CART)
                )
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 20))
    }


    fun findForUserBuy(): Uni<List<WorkEntity>> {

        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .whereAnd(
                    entity.path(CartEntity::userId)
                        .eq(securityIdentity.getUser().id),

                    entity.path(CartEntity::status)
                        .eq(CartEntity.Status.PAYMENT)
                )
        }

        return jpqlEntityManager.JpqlQuery()
            .getResultData(query, PaginationInput(0, 20))
            .flatMap {
                val ids = it.map(CartEntity::workId)
                workFetchUsecase.findByIds(ids.filterNotNull())
            }
    }

}
