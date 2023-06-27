package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database.Companion.getConnection
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Order
import java.util.*

class OrderTable : Table<Order>(
    Tables.ORDER,
    listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("cashpoint_id", "UUID"),
        Column("status", "INT DEFAULT 0"),
        Column("create_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"),
    ),
    hashMapOf(
        "cashpoint_id" to "cashpoint(id)"
    )
) {

    override fun create(item: Order): String {
        val connection = getConnection()
        val statement = connection.createStatement()
        val id = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.ORDER} (id, cashpoint_id) VALUES ('$id', '${item.cashpoint}')")

        item.products.forEach { orderDetail ->
            OrderDetailTable().
            create(orderDetail, id)
        }
        statement.close()
        return id
    }

    override fun delete(item: Order) {
        TODO("Not yet implemented")
    }

    override fun update(item: Order) {
        TODO("Not yet implemented")
    }

    override fun read(): Array<Order> {
        val connection = getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.ORDER}")
        val orders = arrayListOf<Order>()

        while (resultSet.next()) {
            orders.add(
                Order(
                    resultSet.getString("id"),
                    listOf(),
                    resultSet.getString("cashpoint"),
                    resultSet.getTimestamp("create_at").time,
                    resultSet.getInt("status")
                )
            )
        }
        resultSet.close()
        statement.close()

        return orders.toTypedArray()
    }

    override fun find(id: String): Order? {
        TODO("Not yet implemented")
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Order> {
        TODO("Not yet implemented")
    }
}