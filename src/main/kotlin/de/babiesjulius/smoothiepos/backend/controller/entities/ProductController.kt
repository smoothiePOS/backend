package de.babiesjulius.smoothiepos.backend.controller.entities

import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Product
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.SQLIntegrityConstraintViolationException

@RestController
@Tag(name = "Product", description = "The Product API")
class ProductController {

    data class ProductPostRequest(
        val name: String,
        val price: Int,
        val description: String,
        val ingredients: List<String>,
        val available: Boolean = false
    )

    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully created product", content = [
                Content(
                    mediaType = "text/plain",
                    schema = Schema(
                        implementation = String::class,
                        description = "ID of the created product",
                        example = "86c3d35e-13d1-4755-9f29-3ab552e109cb"
                    )
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Product already exists",
            content = [Content(mediaType = "text/plain")]
        ),
    )
    @PostMapping("/product")
    fun createProduct(product: ProductPostRequest): ResponseEntity<String> {
        return try {
            ResponseEntity.status(200).body(
                Database.getDatabase().productTable.create(
                    Product(
                        null,
                        product.name,
                        product.price,
                        "",
                        null,
                        product.ingredients,
                        product.available
                    )
                )
            )
        } catch (e: SQLIntegrityConstraintViolationException) {
            ResponseEntity.status(400).body("Product ${product.name} already exists")
        }
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Get all products",
            content = [Content(
                mediaType = "application/json",
                array = ArraySchema(arraySchema = Schema(implementation = Product::class))
            )]
        ),
    )
    @GetMapping("/product", produces = ["application/json"])
    fun getProducts(): ResponseEntity<Array<Product>> {
        return ResponseEntity.status(200).body(Database.getDatabase().productTable.read())
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Successfully changed availability of product",
            content = [Content(mediaType = "text/plain")]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Product not found",
            content = [Content(mediaType = "text/plain")]
        ),
    )
    @GetMapping("/product/{id}/availability/{available}")
    fun setProductAvailable(@PathVariable id: String, @PathVariable available: Boolean): ResponseEntity<String> {
        val product =
            Database.getDatabase().productTable.find(id) ?: return ResponseEntity.status(400).body("Product not found")
        Database.getDatabase().productTable.changeAvailability(product.copy(available = available))
        return ResponseEntity.status(200).build()
    }
}