package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Ingredient
import de.babiesjulius.smoothiepos.backend.objects.Product

class ProductTable : Table<Product>(
    Tables.PRODUCT, listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("name", "varchar(255)", unique = true),
        Column("price", "int"),
        Column("description", "varchar(255)"),
        Column("category", "varchar(255)"),
        Column("available", "boolean DEFAULT 0")
    )
) {
    override fun create(item: Product): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val uuid = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.PRODUCT} (id, name, price, description, category, available) VALUES ('$uuid', '${item.name}', ${item.price}, '${item.description}', '${item.category}', ${item.available})")
        statement.close()
        item.ingredients.forEach { ingredient ->
            Database.getDatabase().ingredientsTable.create(item.apply { id = uuid }, Ingredient(ingredient, ""))
        }
        return uuid
    }

    override fun delete(item: Product) {
        TODO("Not yet implemented")
    }

    override fun update(item: Product) {
        TODO("Not yet implemented")
    }

    fun changeAvailability(item: Product) {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("UPDATE ${Tables.PRODUCT} SET available = ${item.available} WHERE id = '${item.id}'")
        statement.close()
    }

    override fun read(): Array<Product> {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.PRODUCT} ORDER BY name")
        val products = arrayListOf<Product>()

        while (resultSet.next()) {
            products.add(
                Product(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("price"),
                    resultSet.getString("description"),
                    resultSet.getString("category"),
                    Database.getDatabase().ingredientsTable.filter(listOf(Triple("product_id", "=", resultSet.getString("id")))).map { it.ingredientId },
                    resultSet.getBoolean("available")
                )
            )
        }

        resultSet.close()
        statement.close()

        return products.toTypedArray()
    }

    override fun find(id: String): Product? {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT * FROM ${Tables.PRODUCT} WHERE id = '$id'")
        val product: Product? = if (resultSet.next()) Product(resultSet.getString("id"), resultSet.getString("name"), resultSet.getInt("price"), resultSet.getString("description"), resultSet.getString("category"), listOf(), resultSet.getBoolean("available")) else null
        resultSet.close()
        statement.close()
        return product
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<Product> {
        TODO("Not yet implemented")
    }
}