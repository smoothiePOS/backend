package de.babiesjulius.smoothiepos.backend.controller.ui

import de.babiesjulius.smoothiepos.backend.database.Database
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Kitchen", description = "The Kitchen API - used for single kitchen display and product management")
class KitchenController {

    data class KitchenOrder(val id: String, val products: List<KitchenOrderDetail>, val date: Long, val extra: String?)
    data class KitchenOrderDetail(val product: String, val amount: Int)

    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Returns all orders that are not finished", content = [
                Content(
                    mediaType = "application/json",
                    array = ArraySchema(
                        arraySchema = Schema(
                            implementation = KitchenOrder::class
                        )
                    )
                )
            ]
        )
    )
    @GetMapping("/kitchen/orders")
    fun getOrders(): ResponseEntity<Array<KitchenOrder>> {
        val database = Database.getDatabase()
        val orders = database.orderTable.filter(listOf(Triple("status", "=", "0"))).toList()
        val kitchenOrders = arrayListOf<KitchenOrder>()
        orders.forEach { order ->
            val kitchenOrderDetails = arrayListOf<KitchenOrderDetail>()
            order.products.forEach { product ->
                val productObject = database.productTable.find(product.productId)
                kitchenOrderDetails.add(KitchenOrderDetail(productObject?.name ?: "Unknown", product.amount))
            }
            kitchenOrders.add(KitchenOrder(order.id!!, kitchenOrderDetails, order.date!!, order.extra))
        }
        return ResponseEntity.ok().body(kitchenOrders.toTypedArray())
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Finishes the order with the given id", content = [
                Content(
                    mediaType = "text/plain",
                    schema = Schema(
                        implementation = String::class
                    )
                )
            ]
        )
    )
    @GetMapping("/kitchen/order/{orderId}/finish")
    fun finishOrder(@PathVariable orderId: String): ResponseEntity<String> {
        val database = Database.getDatabase()
        database.orderTable.update(database.orderTable.find(orderId)!!.copy(status = 1))
        return ResponseEntity.ok().build()
    }
}