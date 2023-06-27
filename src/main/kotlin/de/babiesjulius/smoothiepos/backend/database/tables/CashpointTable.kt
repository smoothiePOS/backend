package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.objects.Cashpoint

class CashpointTable : Table<Cashpoint>(
    "cashpoint",
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
        TODO("Not yet implemented")
    }

    override fun find(id: String): Cashpoint? {
        TODO("Not yet implemented")
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Cashpoint> {
        TODO("Not yet implemented")
    }
}