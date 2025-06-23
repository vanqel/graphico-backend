package io.diplom.cart.usecase

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.cart.model.CartEntity
import io.diplom.cart.repository.CartPanacheRepository
import io.diplom.common.security.configurator.getUser
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.exception.AuthException
import io.diplom.exception.GeneralException
import io.diplom.works.models.WorkEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class CartPaymentUsecase(
    private val cartRepository: CartPanacheRepository,
    private val cartFetchUsecase: CartFetchUsecase,
    private val securityIdentity: SecurityIdentity,
    private val jpqlEntityManager: JpqlEntityManager
) {
    companion object {
        private val cart = entity(CartEntity::class)
        private val work = entity(WorkEntity::class)
    }

    private fun changeStatus(id: Long, status: CartEntity.Status): Uni<List<CartEntity>> =
        jpql {
            selectDistinct(cart)
                .from(
                    work,
                    join(cart).on(cart.path(CartEntity::workId).eq(work.path(WorkEntity::id)))
                )
                .whereAnd(
                    cart.path(CartEntity::userId).eq(securityIdentity.getUser().id),
                    cart.path(CartEntity::workId).eq(id)
                )
        }.let {
            jpqlEntityManager.JpqlQuery().getSingleResult(it)
        }.flatMap { entity ->
            when {
                entity == null ->
                    Uni.createFrom().failure { GeneralException("Не найдена работа") }

                entity.userId != securityIdentity.getUser().id ->
                    Uni.createFrom().failure { AuthException() }

                entity.status == CartEntity.Status.PAYMENT -> uni { entity }

                else -> uni {
                    entity.status = status
                    entity
                }
            }
        }.flatMap(cartRepository::persistAndFlush)
            .flatMap { cartFetchUsecase.findForUserCart() }

    fun success(id: Long): Uni<List<CartEntity>> = changeStatus(id, CartEntity.Status.PAYMENT)

    fun decline(id: Long): Uni<List<CartEntity>> = changeStatus(id, CartEntity.Status.DECLINE)
}
