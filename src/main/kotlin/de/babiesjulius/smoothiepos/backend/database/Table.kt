package de.babiesjulius.smoothiepos.backend.database

import org.apache.logging.log4j.LogManager
import java.sql.SQLIntegrityConstraintViolationException
import java.util.UUID
import kotlin.jvm.Throws

abstract class Table<T>(
    private val name: String,
    private val columns: List<Column>,
    private val foreignKeys: HashMap<String, String> = HashMap(),
) {

    fun createTable(rebuild: Boolean) {
        val connection = Database.getConnection()
        val statement = connection.createStatement()
        if (rebuild) {
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0")
            statement.executeUpdate("DROP TABLE IF EXISTS $name")
            statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1")
        }
        LogManager.getLogger().debug("CREATE TABLE IF NOT EXISTS $name (${columns.joinToString(", ") { "${it.name} ${it.type}" }})")
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS $name (${columns.joinToString(", ") { "${it.name} ${it.type}" }})")
        foreignKeys.forEach { (key, value) ->
            statement.executeUpdate("ALTER TABLE $name ADD FOREIGN KEY ($key) REFERENCES $value")
        }
        columns.forEach { column ->
            if (column.unique) {
                statement.executeUpdate("ALTER TABLE $name ADD UNIQUE (${column.name})")
            }
        }
        statement.close()
    }

    fun createUUID(): String {
        while (true) {
            val uuid = UUID.randomUUID().toString()
            val connection = Database.getConnection()
            val statement = connection.createStatement()
            val result = statement.executeQuery("SELECT * FROM $name WHERE id = '$uuid'")
            if (!result.next()) {
                return uuid
            }
        }
    }

    @Throws(SQLIntegrityConstraintViolationException::class)
    abstract fun create(item: T): String
    abstract fun delete(item: T)
    abstract fun update(item: T)
    abstract fun read(): Array<T>
    abstract fun find(id: String): T?
    abstract fun filter(filter: List<Triple<String, String, String>>) : Array<T>
}

class Column(val name: String, val type: String, val unique: Boolean = false)