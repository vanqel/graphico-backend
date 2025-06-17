package io.diplom.cart.dto.out

import io.diplom.cart.model.CartEntity

data class CartOutput (
    val id: Long,
    val title: String,
    val status: CartEntity.Status,
    val price: Double
)
