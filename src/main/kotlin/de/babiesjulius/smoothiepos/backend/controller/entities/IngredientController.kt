package de.babiesjulius.smoothiepos.backend.controller.entities

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.Ingredient
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import java.sql.SQLIntegrityConstraintViolationException

@Controller
class IngredientController {

    @PostMapping("/ingredient")
    fun createIngredient(httpEntity: HttpEntity<String>): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.status(400).body("No body provided")
        val ingredient = Gson().fromJson(body, Ingredient::class.java)
        return try {
            ResponseEntity.status(200).body(Database.getDatabase().ingredientTable.create(ingredient))
        } catch (e: SQLIntegrityConstraintViolationException) {
            ResponseEntity.status(400).body("Ingredient already exists")
        }
    }

    @GetMapping("/ingredient")
    fun getIngredients(): ResponseEntity<String> {
        return ResponseEntity.status(200).body(Gson().toJson(Database.getDatabase().ingredientTable.read()))
    }

    @PutMapping("/ingredient")
    fun updateIngredient(httpEntity: HttpEntity<String>): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.status(400).body("No body provided")
        val ingredient = Gson().fromJson(body, Ingredient::class.java)
        Database.getDatabase().ingredientTable.update(ingredient)
        return ResponseEntity.status(200).build()
    }
}