package de.babiesjulius.smoothiepos.backend.controller.entities

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Product
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import java.sql.SQLIntegrityConstraintViolationException

@Controller
class ProductController {

    @PostMapping("/product")
    fun createProduct(httpEntity: HttpEntity<String>): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.status(400).body("No body provided")
        val product = Gson().fromJson(body, Product::class.java)
        return try {
            ResponseEntity.status(200).body(Database.getDatabase().productTable.create(product))
        } catch (e: SQLIntegrityConstraintViolationException) {
            ResponseEntity.status(400).body("Product ${product.name} already exists")
        }
    }

    @GetMapping("/product")
    fun getProducts(): ResponseEntity<String> {
        return ResponseEntity.status(200).body(Gson().toJson(Database.getDatabase().productTable.read()))
    }

    @GetMapping("/product/{id}/availability/{available}")
    fun setProductAvailable(@PathVariable id: String, @PathVariable available: Boolean): ResponseEntity<String> {
        val product = Database.getDatabase().productTable.find(id) ?: return ResponseEntity.status(400).body("Product not found")
        Database.getDatabase().productTable.changeAvailability(product.copy(available = available))
        return ResponseEntity.status(200).build()
    }
}