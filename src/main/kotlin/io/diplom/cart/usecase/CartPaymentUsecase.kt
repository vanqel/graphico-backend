package io.diplom.cart.usecase

import io.diplom.cart.model.CartEntity
import io.diplom.cart.repository.CartPanacheRepository
import io.diplom.common.security.configurator.getUser
import io.diplom.exception.AuthException
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
) {

    private fun changeStatus(id: Long, status: CartEntity.Status): Uni<List<CartEntity>> =
        cartRepository.findById(id).flatMap { entity ->
            if (entity.userId != securityIdentity.getUser().id ||
                entity.status == CartEntity.Status.PAYMENT
            ) Uni.createFrom().failure { AuthException() }
            else uni {
                entity.status = status
                entity
            }
        }.flatMap(cartRepository::persistAndFlush)
            .flatMap { cartFetchUsecase.findForUserCart() }

    fun success(id: Long): Uni<List<CartEntity>> = changeStatus(id, CartEntity.Status.PAYMENT)

    fun decline(id: Long): Uni<List<CartEntity>> = changeStatus(id, CartEntity.Status.DECLINE)
}
