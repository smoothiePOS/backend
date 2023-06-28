package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.LiveOrder
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping

@Controller
class LiveOrderController {

    private data class LiveOrderAdd(val cashpointId: String, val productId: String, val amount: Int?)

    @PostMapping("/liveorder")
    fun addOrder(httpEntity: HttpEntity<String>): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.badRequest().body("No body provided")
        val liveOrder = Gson().fromJson(body, LiveOrderAdd::class.java)
        return ResponseEntity.ok().body(Database.getDatabase().liveOrderTable.create(LiveOrder(null, liveOrder.cashpointId, liveOrder.productId, liveOrder.amount)))
    }
}
