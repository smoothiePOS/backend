package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class CashpointController {

    private data class CashpointProduct(val id: String, val name: String, val price: Int, val available: Boolean)

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
}