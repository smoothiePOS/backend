package de.babiesjulius.smoothiepos.backend.controller.entities

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Product
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
}