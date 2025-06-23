package io.diplom.cart.api.http

import io.diplom.cart.dto.inp.CartInput
import io.diplom.cart.model.CartEntity
import io.diplom.cart.usecase.CartAddUsecase
import io.diplom.cart.usecase.CartFetchUsecase
import io.diplom.cart.usecase.CartPaymentUsecase
import io.diplom.cart.usecase.CartRemoveUsecase
import io.diplom.works.models.WorkEntity
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@RouteBase(path = "/cart")
@ApplicationScoped
class CartApi(
    val cartAddUsecase: CartAddUsecase,
    val cartFetchUsecase: CartFetchUsecase,
    val cartPaymentUsecase: CartPaymentUsecase,
    val cartRemoveUsecase: CartRemoveUsecase
) {

    @Route(
        methods = [Route.HttpMethod.POST],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun add(
        @Body input: CartInput
    ): Uni<CartEntity> = cartAddUsecase.add(input)

    @Route(
        path = "remove",
        methods = [Route.HttpMethod.DELETE],
    )
    fun remove(
        @Param id: Long?
    ): Uni<List<CartEntity>> = cartRemoveUsecase.delete(id!!)

    @Deprecated("лютый хард код, осуждаю.. стыдно")
    @Route(
        path = "success",
        methods = [Route.HttpMethod.POST],
    )
    fun buy(
        @Param id: Long?
    ): Uni<List<CartEntity>> = cartPaymentUsecase.success(id!!)

    @Deprecated("лютый хард код, осуждаю.. стыдно")
    @Route(
        path = "decline",
        methods = [Route.HttpMethod.POST],
    )
    fun decline(
        @Param id: Long?
    ): Uni<List<CartEntity>> = cartPaymentUsecase.decline(id!!)

    @Route(
        path = "wait-payment",
        methods = [Route.HttpMethod.GET],
    )
    fun getCart(): Uni<List<CartEntity>> = cartFetchUsecase.findForUserCart()

    @Route(
        path = "owned",
        methods = [Route.HttpMethod.GET],
    )
    fun getBuy(): Uni<List<WorkEntity>> = cartFetchUsecase.findForUserBuy()
}
