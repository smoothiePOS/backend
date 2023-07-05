package de.babiesjulius.smoothiepos.backend.controller.ui

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
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Advertisement", description = "Advertisement API")
class AdvertisementController {

    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Get products and check for all ingredients if available", content = [
            Content(
                mediaType = "application/json",
                array = ArraySchema(schema = Schema(implementation = Product::class))
            )
        ]),
    )
    @GetMapping("/advertisement/product")
    fun getAdvertisementProduct(): ResponseEntity<Array<Product>> {
        val database = Database.getDatabase()
        val products = database.productTable.read()
        products.filter { it.available }.forEach product@{ product: Product ->
            product.ingredients.forEach { ingredient: String ->
                if (!database.ingredientTable.find(ingredient)!!.available) {
                    product.available = false
                    return@product
                }
            }
        }
        return ResponseEntity.ok(products)
    }
}