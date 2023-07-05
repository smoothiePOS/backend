package de.babiesjulius.smoothiepos.backend.controller.ui

import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Order
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
@Tag(name = "Statistic", description = "Statistic API")
class StatisticController {

    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Returns the statistic of the orders", content = [
            Content(mediaType = "application/json", array = ArraySchema(schema = Schema(implementation =  Order::class)))
        ]),
        ApiResponse(responseCode = "500", description = "Internal server error (probably database error)")
    )
    @GetMapping("/statistic/{from}/{to}/orders")
    fun getOrders(@PathVariable from: String, @PathVariable to: String): ResponseEntity<Array<Order>> {
        return try {
            val database = Database.getDatabase()
            val orders = database.orderTable.filter(listOf(
                Triple("status", "=", "1"),
                if (from != "0") Triple("create_at", ">=", from) else Triple("", "", ""),
                if (to != "0") Triple("create_at", "<=", to) else Triple("", "", "")
            ))
            ResponseEntity.ok(orders)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/statistic/{from}/{to}/products/sold")
    fun soldProducts(@PathVariable from: String, @PathVariable to: String): ResponseEntity<Array<StatisticsProductSoldResponseProduct>> {
        return try {
            val database = Database.getDatabase()
            val orders = database.orderTable.filter(listOf(
                Triple("status", "=", "1"),
                if (from != "0") Triple("create_at", ">=", from) else Triple("", "", ""),
                if (to != "0") Triple("create_at", "<=", to) else Triple("", "", "")
            ))
            val products = database.productTable.read()
            val soldProducts = arrayListOf<StatisticsProductSoldResponseProduct>()
            products.forEach { product ->
                var amount = 0
                orders.forEach { order ->
                    order.products.forEach { orderDetail ->
                        if (orderDetail.productId == product.id) {
                            amount += orderDetail.amount
                        }
                    }
                }
                soldProducts.add(StatisticsProductSoldResponseProduct(product.name, amount, product.price))
            }
            ResponseEntity.ok(soldProducts.toTypedArray())
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    data class StatisticsProductSoldResponseProduct(val name: String, val amount: Int, val price: Int)
}