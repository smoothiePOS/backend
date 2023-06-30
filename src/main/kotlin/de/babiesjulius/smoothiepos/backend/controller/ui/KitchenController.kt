package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class KitchenController {

    private data class KitchenOrder(val id: String, val products: List<KitchenOrderDetail>, val date: Long)
    private data class KitchenOrderDetail(val product: String, val amount: Int)

    @GetMapping("/kitchen/orders")
    fun getOrders(): ResponseEntity<String> {
        val database = Database.getDatabase()
        val orders = database.orderTable.read()
        val kitchenOrders = arrayListOf<KitchenOrder>()
        orders.forEach { order ->
            val kitchenOrderDetails = arrayListOf<KitchenOrderDetail>()
            order.products.forEach { product ->
                val productObject = database.productTable.find(product.productId)
                kitchenOrderDetails.add(KitchenOrderDetail(productObject?.name ?: "Unknown", product.amount))
            }
            kitchenOrders.add(KitchenOrder(order.id!!, kitchenOrderDetails, order.date!!))
        }
        return ResponseEntity.ok().body(Gson().toJson(kitchenOrders))
    }

    @PostMapping("/kitchen/order/{orderId}/finish")
    fun finishOrder(@PathVariable orderId: String): ResponseEntity<String> {
        val database = Database.getDatabase()
        database.orderTable.update(database.orderTable.find(orderId)!!.copy(status = 1))
        return ResponseEntity.ok().build()
    }
}