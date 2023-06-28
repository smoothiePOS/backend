package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
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

    @PostMapping("/cashpoint/order")
    fun addOrder(httpEntity: HttpEntity<String>): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.badRequest().body("No body provided")
        val order = Gson().fromJson(body, Order::class.java)
        return ResponseEntity.ok().body(Database.getDatabase().orderTable.create(order))
    }

    @GetMapping("/cashpoint/customer/order/{cashpointId}")
    fun getCustomerOrders(@PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        val order = Database.getDatabase().orderTable.filter(listOf(Triple("cashpoint_id", "=", cashpointId), Triple("status", "=", "0")))
        return ResponseEntity.ok().body(Gson().toJson(order))
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
}