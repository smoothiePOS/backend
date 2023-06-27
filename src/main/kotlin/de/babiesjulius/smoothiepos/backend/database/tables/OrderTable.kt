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
        Column("cashpoint", "UUID"), // TODO add foreign key
        Column("create_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"),
    ),
) {

    override fun create(item: Order): String {
        val connection = getConnection()
        val statement = connection.createStatement()
        val id = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.ORDER} (id) VALUES ('$id')")

        item.products.forEach { orderDetail ->
            OrderDetailTable().create(orderDetail, id)
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
                    resultSet.getTimestamp("create_at").time
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