package io.diplom.cart.usecase

import io.diplom.cart.dto.inp.CartInput
import io.diplom.cart.model.CartEntity
import io.diplom.cart.repository.CartPanacheRepository
import io.diplom.common.security.configurator.getUser
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class CartAddUsecase(
    private val cartRepository: CartPanacheRepository,
    private val securityIdentity: SecurityIdentity
) {

    fun add(input: CartInput): Uni<CartEntity> {

        val entity = CartEntity(
            securityIdentity.getUser().id,
            input.workId,
            CartEntity.Status.IN_CART,
            input.donat
        )

        return cartRepository.persistAndFlush(entity)
    }
}
