package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class KitchenController {

    @GetMapping("/kitchen/orders")
    fun getOrders(): ResponseEntity<String> {
        val database = Database.getDatabase()
        return ResponseEntity.ok().body(Gson().toJson(database.orderTable.filter(listOf(Triple("status", "=", "0")))))
    }
}