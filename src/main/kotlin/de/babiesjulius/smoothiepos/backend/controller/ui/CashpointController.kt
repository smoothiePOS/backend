package de.babiesjulius.smoothiepos.backend.controller.ui

import com.google.gson.Gson
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.objects.LiveOrder
import de.babiesjulius.smoothiepos.backend.objects.Order
import de.babiesjulius.smoothiepos.backend.objects.OrderDetail
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.valueextraction.ExtractedValue
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Cashpoint", description = "Cashpoint related endpoints")
class CashpointController {

    data class CashpointProduct(
        val id: String,
        val name: String,
        val price: Int,
        val deposit: Int,
        val available: Boolean
    )

    data class CashpointCashpointsResponseCashpoint(val name: String, val available: Boolean)

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Get all products for cashpoint staff view (Contains only necessary data to products (id, name, price, deposit, availability))",
            content = [Content(
                mediaType = "application/json", schema = Schema(
                    implementation = Array<CashpointProduct>::class
                )
            )]
        )
    )
    @GetMapping("/cashpoint/products")
    fun getProducts(): ResponseEntity<Array<CashpointProduct>> {
        val database = Database.getDatabase()
        val products = database.productTable.read()
        val cashpointProducts = arrayListOf<CashpointProduct>()
        products.forEach { product ->
            val filter = arrayListOf<Triple<String, String, String>>()
            product.ingredients.forEach {
                filter.add(Triple("ingredient_id", "=", it))
            }
            cashpointProducts.add(
                CashpointProduct(
                    product.id!!,
                    product.name,
                    product.price,
                    100,
                    product.available && database.ingredientTable.read().filter { it.id in product.ingredients }
                        .all { it.available })
            ) // TODO deposit to database
        }
        return ResponseEntity.ok().body(cashpointProducts.toTypedArray())
    }

    data class LiveOrderResponse(val productName: String, val amount: Int, val price: Int, val deposit: Int)

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Get current order for customer view (Contains only necessary data to ordered products (productName, amount, price, deposit))",
            content = [Content(
                mediaType = "application/json", schema = Schema(
                    implementation = Array<LiveOrderResponse>::class
                )
            )]
        )
    )
    @GetMapping("/cashpoint/{cashpointId}/order/rt/customer")
    fun getCustomerOrders(@PathVariable("cashpointId") cashpointId: String): ResponseEntity<Array<LiveOrderResponse>> {
        val order = Database.getDatabase().liveOrderTable.filter(listOf(Triple("cashpoint_id", "=", cashpointId)))
        val liveOrderResponse = arrayListOf<LiveOrderResponse>()
        order.forEach { liveOrder ->
            val product = Database.getDatabase().productTable.find(liveOrder.productId)
            liveOrderResponse.add(
                LiveOrderResponse(
                    product?.name ?: "Unknown",
                    liveOrder.amount!!,
                    product?.price ?: 0,
                    100
                )
            ) // TODO transfer deposit to product
        }
        return ResponseEntity.ok().body(liveOrderResponse.toTypedArray())
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Get all cashpoints",
            content = [Content(
                mediaType = "application/json"
            )]
        )
    )
    @GetMapping("/cashpoints")
    fun getCashpoints(): ResponseEntity<HashMap<String, CashpointCashpointsResponseCashpoint>> {
        val cashpoints = Database.getDatabase().cashpointTable.read()
        val cashpointCashpoints = hashMapOf<String, CashpointCashpointsResponseCashpoint>()
        cashpoints.forEach { cashpoint ->
            cashpointCashpoints[cashpoint.id!!] =
                CashpointCashpointsResponseCashpoint(cashpoint.name, cashpoint.available)
        }
        return ResponseEntity.ok().body(cashpointCashpoints)
    }

    private data class LiveOrderAdd(val productId: String, val amount: Int?)

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Add product to running order at cashpoint",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(
                    implementation = String::class
                )
            )]
        )
    )
    @PostMapping("/cashpoint/{cashpointId}/order/rt")
    fun addOrder(
        httpEntity: HttpEntity<String>,
        @PathVariable("cashpointId") cashpointId: String
    ): ResponseEntity<String> {
        val body = httpEntity.body ?: return ResponseEntity.badRequest().body("No body provided")
        val liveOrder = Gson().fromJson(body, LiveOrderAdd::class.java)
        return ResponseEntity.ok().body(
            Database.getDatabase().liveOrderTable.create(
                LiveOrder(
                    null,
                    cashpointId,
                    liveOrder.productId,
                    liveOrder.amount
                )
            )
        )
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Clear running order at cashpoint",
            content = [Content(
                mediaType = "text/plain",
                schema = Schema(
                    implementation = String::class
                )
            )]
        )
    )
    @GetMapping("/cashpoint/{cashpointId}/order/rt/clear")
    fun clearOrder(@PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        Database.getDatabase().liveOrderTable.clear(listOf(Triple("cashpoint_id", "=", cashpointId)))
        return ResponseEntity.ok().build()
    }

    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "Confirm running order at cashpoint (send to kitchen)",
            content = [Content(
                mediaType = "text/plain",
                schema = Schema(
                    implementation = String::class
                )
            )]
        )
    )
    @PostMapping("/cashpoint/{cashpointId}/order/rt/confirm")
    fun confirmOrder(httpEntity: HttpEntity<String>, @PathVariable("cashpointId") cashpointId: String): ResponseEntity<String> {
        var extra: String? = null
        if (httpEntity.body != null) {
            extra = Gson().fromJson(httpEntity.body, ConfirmOrderRequest::class.java).extra
        }
        val database = Database.getDatabase()
        val detail = arrayListOf<OrderDetail>()
        val liveOrder = database.liveOrderTable.filter(listOf(Triple("cashpoint_id", "=", cashpointId)))
        liveOrder.forEach { lo ->
            detail.add(OrderDetail(lo.productId, lo.amount!!))
        }
        val order = Order(null, detail, cashpointId, null, 0, extra)
        database.orderTable.create(order)
        database.liveOrderTable.clear(listOf(Triple("cashpoint_id", "=", cashpointId)))
        return ResponseEntity.ok().build()
    }

    data class ConfirmOrderRequest(val extra: String?)
}