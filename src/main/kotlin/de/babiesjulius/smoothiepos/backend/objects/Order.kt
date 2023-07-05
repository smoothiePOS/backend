package de.babiesjulius.smoothiepos.backend.objects

data class Order(val id: String?, var products: List<OrderDetail>, val cashpoint: String?, val date: Long?, var status: Int?, var extra: String?)
data class OrderDetail(val productId: String, val amount: Int)