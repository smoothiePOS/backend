package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
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
        "order_id" to "${Tables.ORDER}(id)",
        "product_id" to "${Tables.PRODUCT}(id)"
    )
) {

    fun create(item: OrderDetail, orderId: String): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val id = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.ORDER_DETAIL} (id, order_id, product_id, amount) VALUES ('$id', '${orderId}', '${item.productId}', ${item.amount})")
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
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        var sql = "SELECT * FROM ${Tables.ORDER_DETAIL}"
        sql += " WHERE "
        filter.forEach {
            sql += "${it.first} ${it.second} '${it.third}' AND "
        }
        sql += "1=1"
        val resultSet = statement.executeQuery(sql)
        val list = mutableListOf<OrderDetail>()
        while (resultSet.next()) {
            list.add(
                OrderDetail(
                    resultSet.getString("product_id"),
                    resultSet.getInt("amount")
                )
            )
        }
        statement.close()
        return list.toTypedArray()
    }
}