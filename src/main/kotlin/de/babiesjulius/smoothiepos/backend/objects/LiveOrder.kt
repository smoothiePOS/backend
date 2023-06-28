package de.babiesjulius.smoothiepos.backend.objects

data class LiveOrder(var id: Int?, val cashpointId: String, val productId: String, var amount: Int?)