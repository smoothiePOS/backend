package de.babiesjulius.smoothiepos.backend.database

import de.babiesjulius.smoothiepos.backend.database.tables.*
import de.babiesjulius.smoothiepos.backend.system.EnvironmentVariables
import org.apache.logging.log4j.LogManager
import java.sql.Connection
import java.sql.DriverManager
import java.util.Objects

class Database {

    val productTable = ProductTable()
    val ingredientTable = IngredientTable()
    val ingredientsTable = IngredientsTable()
    val orderTable = OrderTable()
    val orderDetailTable = OrderDetailTable()


    companion object {
        @JvmStatic private var connection: Connection? = null
        @JvmStatic private var db: Database? = null
        @JvmStatic fun getDatabase(): Database {
            if (db == null) db = Database()
            return db!!
        }
        @JvmStatic fun getConnection(): Connection {
            if (connection == null) {
                LogManager.getLogger().warn("CREATING NEW DATABASE CONNECTION")
                connection = DriverManager.getConnection(
                    "jdbc:mysql://${System.getenv(EnvironmentVariables.DATABASE_HOST.name)}:" +
                            "${System.getenv(EnvironmentVariables.DATABASE_PORT.name)}/" +
                            System.getenv(EnvironmentVariables.DATABASE_NAME.name),
                    System.getenv(EnvironmentVariables.DATABASE_USER.name),
                    System.getenv(EnvironmentVariables.DATABASE_PASSWORD.name))
            }
            return connection!!
        }
    }

    init {
        val rebuild = false // true will drop all tables and recreate them
        productTable.createTable(rebuild)
        ingredientTable.createTable(rebuild)
        ingredientsTable.createTable(rebuild)
        orderTable.createTable(rebuild)
        orderDetailTable.createTable(rebuild)
    }
}