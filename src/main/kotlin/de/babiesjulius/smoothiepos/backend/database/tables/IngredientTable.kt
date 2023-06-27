package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Ingredient
import java.sql.SQLIntegrityConstraintViolationException
import kotlin.jvm.Throws

class IngredientTable : Table<Ingredient>(
    Tables.INGREDIENT, listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("name", "varchar(255)", unique = true),
        Column("available", "boolean")
    ),
    hashMapOf<String, String>()
) {

    @Throws(SQLIntegrityConstraintViolationException::class)
    override fun create(item: Ingredient): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val uuid = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.INGREDIENT} (id, name, available) VALUES ('$uuid', '${item.name}', ${item.available})")
        statement.close()
        return uuid
    }

    override fun delete(item: Ingredient) {
        TODO("Not yet implemented")
    }

    override fun update(item: Ingredient) {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("UPDATE ${Tables.INGREDIENT} SET name = '${item.name}', available = ${item.available} WHERE id = '${item.id}'")
        statement.close()
    }

    override fun read(): Array<Ingredient> {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.INGREDIENT} ORDER BY name")
        val ingredients = arrayListOf<Ingredient>()
        while (resultSet.next()) {
            ingredients.add(Ingredient(resultSet.getString("id"), resultSet.getString("name"), resultSet.getBoolean("available")))
        }
        resultSet.close()
        statement.close()
        return ingredients.toTypedArray()
    }

    override fun find(id: String): Ingredient? {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.INGREDIENT} WHERE id = '$id' ORDER BY name")
        val ingredient = if (resultSet.next()) Ingredient(resultSet.getString("id"), resultSet.getString("name"), resultSet.getBoolean("available")) else null
        resultSet.close()
        statement.close()
        return ingredient
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Ingredient> {
        TODO("Not yet implemented")
    }
}