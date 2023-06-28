package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.LiveOrder
import de.babiesjulius.smoothiepos.backend.objects.Order
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class CashpointController {

    private data class CashpointProduct(val id: String, val name: String, val price: Int, val available: Boolean)
    private data class CashpointCashpointsResponseCashpoint(val name: String, val available: Boolean)
    private data class CashpointCashpointsResponse(val cashpoints: Map<String, CashpointCashpointsResponseCashpoint>)

    @GetMapping("/cashpoint/products")
    fun getProducts(): ResponseEntity<String> {
        val database = Database.getDatabase()
        val products = database.productTable.read()
        val cashpointProducts = arrayListOf<CashpointProduct>()
        products.forEach { product ->
            val filter = arrayListOf<Triple<String, String, String>>()
            product.ingredients.forEach {
                filter.add(Triple("ingredient_id", "=", it))
            }
            cashpointProducts.add(CashpointProduct(product.id!!, product.name, product.price, product.available && database.ingredientTable.read().filter { it.id in product.ingredients }.all { it.available }))
        }
        return ResponseEntity.ok().body(Gson().toJson(cashpointProducts))
    }

    private data class LiveOrderResponse(val productName: String, val amount: Int, val price: Int, val deposit: Int)

    @GetMapping("/cashpoint/{cashpointId}/order/rt/customer")
    fun getCustomerOrders(@PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        val order = Database.getDatabase().liveOrderTable.filter(listOf(Triple("cashpoint_id", "=", cashpointId)))
        val liveOrderResponse = arrayListOf<LiveOrderResponse>()
        order.forEach { liveOrder ->
            val product = Database.getDatabase().productTable.find(liveOrder.productId)
            liveOrderResponse.add(LiveOrderResponse(product?.name ?: "Unknown", liveOrder.amount!!, product?.price ?: 0, 100)) // TODO transfer deposit to product
        }
        return ResponseEntity.ok().body(Gson().toJson(liveOrderResponse.toList()))
    }

    @GetMapping("/cashpoints")
    fun getCashpoints(): ResponseEntity<String> {
        val cashpoints = Database.getDatabase().cashpointTable.read()
        val cashpointCashpoints = hashMapOf<String, CashpointCashpointsResponseCashpoint>()
        cashpoints.forEach { cashpoint ->
            cashpointCashpoints[cashpoint.id!!] = CashpointCashpointsResponseCashpoint(cashpoint.name, cashpoint.available)
        }
        return ResponseEntity.ok().body(Gson().toJson(CashpointCashpointsResponse(cashpointCashpoints)))
    }

    private data class LiveOrderAdd(val productId: String, val amount: Int?)

    @PostMapping("/cashpoint/{cashpointId}/order/rt")
    fun addOrder(httpEntity: HttpEntity<String>, @PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.badRequest().body("No body provided")
        val liveOrder = Gson().fromJson(body, LiveOrderAdd::class.java)
        return ResponseEntity.ok().body(
            Database.getDatabase().liveOrderTable.create(
                LiveOrder(
                    null,
                    cashpointId,
                    liveOrder.productId,
                    liveOrder.amount
                )
            )
        )
    }

    @GetMapping("/cashpoint/{cashpointId}/order/rt/clear")
    fun clearOrder(@PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        Database.getDatabase().liveOrderTable.clear(listOf(Triple("cashpoint_id", "=", cashpointId)))
        return ResponseEntity.ok().body("OK")
    }
}