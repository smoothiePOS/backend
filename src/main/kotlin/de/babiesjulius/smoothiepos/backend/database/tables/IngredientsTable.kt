package de.babiesjulius.smoothiepos.backend.database.tables

import de.babiesjulius.smoothiepos.backend.database.Column
import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.database.Table
import de.babiesjulius.smoothiepos.backend.database.Tables
import de.babiesjulius.smoothiepos.backend.objects.Ingredient
import de.babiesjulius.smoothiepos.backend.objects.Product
import de.babiesjulius.smoothiepos.backend.objects.ProductIngredient

class IngredientsTable : Table<ProductIngredient>(
    Tables.INGREDIENTS, listOf(
        Column("id", "UUID PRIMARY KEY DEFAULT UUID()"),
        Column("product_id", "UUID"),
        Column("ingredient_id", "UUID")
    ),
    hashMapOf(
        "product_id" to "product(id)",
        "ingredient_id" to "ingredient(id)"
    )
) {

    fun create(product: Product, ingredient: Ingredient): String {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val uuid = this.createUUID()
        statement.executeUpdate("INSERT INTO ${Tables.INGREDIENTS} (id, product_id, ingredient_id) VALUES ('$uuid', '${product.id}', '${ingredient.id}')")
        statement.close()
        return uuid
    }

    override fun create(item: ProductIngredient): String {
        throw Exception("Use create(product: Product, ingredient: Ingredient) instead")
    }

    override fun delete(item: ProductIngredient) {
        TODO("Not yet implemented")
    }

    override fun update(item: ProductIngredient) {
        TODO("Not yet implemented")
    }

    override fun read(): Array<ProductIngredient> {
        TODO("Not yet implemented")
    }

    override fun find(id: String): ProductIngredient? {
        TODO("Not yet implemented")
    }

    override fun filter(filter: List<Triple<String, String, String>>): Array<ProductIngredient> {
        var sql = "SELECT * FROM ${Tables.INGREDIENTS}"
        sql += " WHERE "
        filter.forEach {
            sql += "${it.first} ${it.second} '${it.third}' AND "
        }
        sql += "1=1"
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(sql)
        val ingredients = arrayListOf<ProductIngredient>()
        while (resultSet.next()) {
            ingredients.add(
                ProductIngredient(
                    resultSet.getString("id"),
                    resultSet.getString("product_id"),
                    resultSet.getString("ingredient_id")
                )
            )
        }
        return ingredients.toTypedArray()
    }
}