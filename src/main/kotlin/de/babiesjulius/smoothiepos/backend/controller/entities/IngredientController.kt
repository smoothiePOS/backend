package de.babiesjulius.smoothiepos.backend.controller.entities

import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Ingredient
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.sql.SQLIntegrityConstraintViolationException

@RestController
@Tag(name = "Ingredient API", description = "API for managing ingredients")
class IngredientController {

    data class IngredientPostRequest(val name: String, val available: Boolean = false)

    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully created ingredient", content = [
                Content(
                    mediaType = "application/json",
                    schema = io.swagger.v3.oas.annotations.media.Schema(
                        implementation = String::class,
                        description = "ID of the created ingredient",
                        example = "86c3d35e-13d1-4755-9f29-3ab552e109cb"
                    )
                )
            ]
        ),
        ApiResponse(responseCode = "400", description = "Ingredient with this name already exists")
    )
    @PostMapping("/ingredient")
    fun createIngredient(@RequestBody ingredient: IngredientPostRequest): ResponseEntity<String> {
        return try {
            ResponseEntity.status(200).body(
                Database.getDatabase().ingredientTable.create(
                    Ingredient(
                        null,
                        ingredient.name,
                        ingredient.available
                    )
                )
            )
        } catch (e: SQLIntegrityConstraintViolationException) {
            ResponseEntity.status(400).body("Ingredient already exists")
        }
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully fetched ingredients", content = [
                Content(
                    mediaType = "application/json",
                    array = ArraySchema(
                        arraySchema = Schema(
                            implementation = Ingredient::class
                        )
                    )
                )
            ]
        )
    )
    @GetMapping("/ingredient")
    fun getIngredients(): ResponseEntity<Array<Ingredient>> {
        return ResponseEntity.status(200).body(Database.getDatabase().ingredientTable.read())
    }

    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully changed availability of ingredient"),
        ApiResponse(responseCode = "400", description = "Ingredient not found")
    )
    @GetMapping("/ingredient/{id}/availability/{available}")
    fun setIngredientAvailable(@PathVariable id: String, @PathVariable available: Boolean): ResponseEntity<String> {
        val ingredient = Database.getDatabase().ingredientTable.find(id) ?: return ResponseEntity.status(400)
            .body("Ingredient not found")
        Database.getDatabase().ingredientTable.update(ingredient.copy(available = available))
        return ResponseEntity.status(200).build()
    }
}