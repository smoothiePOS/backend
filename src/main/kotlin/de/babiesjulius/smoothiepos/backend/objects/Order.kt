package de.babiesjulius.smoothiepos.backend.objects

data class Order(val id: String?, var products: List<OrderDetail>, val cashpoint: String?, val date: Long?)
data class OrderDetail(val product: Product, val amount: Int)