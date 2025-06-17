package io.diplom.cart.model

import io.diplom.models.LongEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "cart")
@Entity
class CartEntity(
    val userId: Long? = null,
    val workId: Long? = null,
    var status: Status? = null,
): LongEntity() {
    enum class Status {
        IN_CART, PAYMENT, DECLINE
    }
}
