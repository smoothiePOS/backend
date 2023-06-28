package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Cashpoint

class CashpointTable : Table<Cashpoint>(
    Tables.CASHPOINT,
    listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("name", "VARCHAR(255)"),
        Column("ip", "VARCHAR(255)"),
        Column("port", "INT"),
        Column("available", "BOOLEAN"),
    ),
    hashMapOf()
) {
    override fun create(item: Cashpoint): String {
        TODO("Not yet implemented")
    }

    override fun delete(item: Cashpoint) {
        TODO("Not yet implemented")
    }

    override fun update(item: Cashpoint) {
        TODO("Not yet implemented")
    }

    override fun read(): Array<Cashpoint> {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.CASHPOINT} ORDER BY name")
        val cashpoints = arrayListOf<Cashpoint>()
        while (resultSet.next()) {
            cashpoints.add(Cashpoint(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("ip"), resultSet.getInt("port"), resultSet.getBoolean("available")))
        }
        resultSet.close()
        statement.close()
        return cashpoints.toTypedArray()
    }

    override fun find(id: String): Cashpoint? {
        TODO("Not yet implemented")
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Cashpoint> {
        TODO("Not yet implemented")
    }
}