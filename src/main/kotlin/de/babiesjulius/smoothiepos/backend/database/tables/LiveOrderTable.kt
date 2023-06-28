package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.LiveOrder
import org.apache.logging.log4j.LogManager
import java.sql.Statement

class LiveOrderTable : Table<LiveOrder>(
    Tables.LIVE_ORDER,
    listOf(
        Column("id", "INT NOT NULL AUTO_INCREMENT PRIMARY KEY"),
        Column("cashpoint_id", "UUID"),
        Column("product_id", "UUID"),
        Column("amount", "INT"),
    ),
    hashMapOf(
        "cashpoint_id" to "${Tables.CASHPOINT}(id)",
        "product_id" to "${Tables.PRODUCT}(id)"
    )
) {
    override fun create(item: LiveOrder): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val existing = this.filter(
            listOf(
                Triple("cashpoint_id", "=", item.cashpointId),
                Triple("product_id", "=", item.productId)
            )
        ).firstOrNull()
        if (existing != null) {
            LogManager.getLogger("LiveOrderTable").info("New value: " + (existing.amount!! + item.amount!!).toString())
            if (item.amount == null || existing.amount!! + item.amount!! <= 0) { // delete
                this.delete(existing)
                return "0"
            } else {
                this.update(
                    LiveOrder(
                        existing.id,
                        existing.cashpointId,
                        existing.productId,
                        item.amount!! + existing.amount!!
                    )
                )
            }
            return (item.amount!! + existing.amount!!).toString()
        }
        if ((item.amount ?: 0) < 1) return "0"
        statement.executeUpdate(
            "INSERT INTO ${Tables.LIVE_ORDER} (cashpoint_id, product_id, amount) VALUES ('${item.cashpointId}', '${item.productId}', '${item.amount}')",
            Statement.RETURN_GENERATED_KEYS
        )
        val rs = statement.generatedKeys
        assert(rs.next())
        val id = rs.getInt(1)
        statement.close()
        return this.find(id.toString())?.amount.toString()
    }

    override fun delete(item: LiveOrder) {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("DELETE FROM ${Tables.LIVE_ORDER} WHERE id = ${item.id}")
        statement.close()
    }

    override fun update(item: LiveOrder) {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("UPDATE ${Tables.LIVE_ORDER} SET amount = '${item.amount}' WHERE id = ${item.id}")
        statement.close()
    }

    override fun read(): Array<LiveOrder> {
        TODO("Not yet implemented")
    }

    override fun find(id: String): LiveOrder? {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val rs = statement.executeQuery("SELECT * FROM ${Tables.LIVE_ORDER} WHERE id = ${id.toInt()}")
        if (!rs.next()) return null
        val order = LiveOrder(
            rs.getInt("id"),
            rs.getString("cashpoint_id"),
            rs.getString("product_id"),
            rs.getInt("amount")
        )
        rs.close()
        statement.close()
        return order
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<LiveOrder> {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val rs =
            statement.executeQuery("SELECT * FROM ${Tables.LIVE_ORDER} WHERE ${filter.joinToString(" AND ") { "${it.first} ${it.second} '${it.third}'" }} ORDER BY id")
        val orders = arrayListOf<LiveOrder>()
        while (rs.next()) {
            orders.add(
                LiveOrder(
                    rs.getInt("id"),
                    rs.getString("cashpoint_id"),
                    rs.getString("product_id"),
                    rs.getInt("amount")
                )
            )
        }
        rs.close()
        statement.close()
        return orders.toTypedArray()
    }
}