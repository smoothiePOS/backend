package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Order
import de.babiesjulius.smoothiepos.backend.objects.OrderDetail

class OrderDetailTable : Table<OrderDetail>(
    Tables.ORDER_DETAIL,
    listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("order_id", "UUID"),
        Column("product_id", "UUID"),
        Column("amount", "int")
    ),
    hashMapOf(
        "order_id" to "orders(id)",
        "product_id" to "product(id)"
    )
) {
    fun getOrderDetails(order: Order): Array<OrderDetail> {
        return getOrderDetails(order.id!!)
    }

    fun getOrderDetails(id: String): Array<OrderDetail> {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.ORDER_DETAIL} LEFT JOIN ${Tables.ORDER} ON order_id = ${Tables.ORDER}.id WHERE order_id = '$id'")
        val products = mutableListOf<OrderDetail>()
        while (resultSet.next()) {
            products.add(OrderDetail(
                ProductTable().find(resultSet.getString("product_id"))!!,
                resultSet.getInt("amount")))
        }
        statement.close()
        return products.toTypedArray()
    }

    fun create(item: OrderDetail, orderId: String): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val id = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.ORDER_DETAIL} (id, order_id, product_id, amount) VALUES ('$id', '$orderId', '${item.product.id}', ${item.amount})")
        statement.close()
        return id
    }

    override fun create(item: OrderDetail): String {
        throw Exception("Use create(item: OrderDetail, orderId: String) instead")
    }

    override fun delete(item: OrderDetail) {
        TODO("Not yet implemented")
    }

    override fun update(item: OrderDetail) {
        TODO("Not yet implemented")
    }

    override fun read(): Array<OrderDetail> {
        TODO("Not yet implemented")
    }

    override fun find(id: String): OrderDetail? {
        TODO("Not yet implemented")
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<OrderDetail> {
        TODO("Not yet implemented")
    }
}