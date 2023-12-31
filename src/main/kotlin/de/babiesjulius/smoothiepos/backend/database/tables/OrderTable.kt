package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Database.Companion.getConnection
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Order

class OrderTable : Table<Order>(
    Tables.ORDER,
    listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("cashpoint_id", "UUID"),
        Column("status", "INT DEFAULT 0"),
        Column("extra", "TEXT"),
        Column("create_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    ),
    hashMapOf(
        "cashpoint_id" to "cashpoint(id)"
    )
) {

    override fun create(item: Order): String {
        val connection = getConnection()
        val statement = connection.createStatement()
        val id = this.createUUID()
        if (item.extra == null) {
            statement.executeUpdate("INSERT INTO ${Tables.ORDER} (id, cashpoint_id) VALUES ('$id', '${item.cashpoint}')")
        } else {
            statement.executeUpdate("INSERT INTO ${Tables.ORDER} (id, cashpoint_id, extra) VALUES ('$id', '${item.cashpoint}', '${item.extra?.replace("'", "''")}')")
        }

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
        val connection = getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("UPDATE ${Tables.ORDER} SET status = ${item.status} WHERE id = '${item.id}'")
        statement.close()
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
                    Database.getDatabase().orderDetailTable.filter(listOf(Triple("order_id", "=", resultSet.getString("id")))).toList(),
                    resultSet.getString("cashpoint_id"),
                    resultSet.getTimestamp("create_at").time,
                    resultSet.getInt("status"),
                    resultSet.getString("extra")
                )
            )
        }
        resultSet.close()
        statement.close()

        return orders.toTypedArray()
    }

    override fun find(id: String): Order? {
        return this.filter(listOf(Triple("id", "=", id))).firstOrNull()
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Order> {
        val connection = getConnection()
        val statement = connection.createStatement()
        var sql = "SELECT * FROM ${Tables.ORDER}"
        if (filter.isNotEmpty()) sql += " WHERE "

        sql += filter.filter { it.first != "" }.joinToString(separator = " AND ") { triple ->
            "${triple.first} ${triple.second} '${triple.third}'"
        }

        val resultSet = statement.executeQuery("$sql ORDER BY create_at")
        val orders = arrayListOf<Order>()
        while (resultSet.next()) {
            orders.add(
                Order(
                    resultSet.getString("id"),
                    Database.getDatabase().orderDetailTable.filter(listOf(Triple("order_id", "=", resultSet.getString("id")))).toList(),
                    resultSet.getString("cashpoint_id"),
                    resultSet.getTimestamp("create_at").time,
                    resultSet.getInt("status"),
                    resultSet.getString("extra")
                )
            )
        }
        resultSet.close()
        statement.close()
        return orders.toTypedArray()
    }
}