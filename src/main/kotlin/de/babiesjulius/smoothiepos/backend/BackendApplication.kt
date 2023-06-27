package de.babiesjulius.smoothiepos.backend

import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.system.EnvironmentVariables
import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.sql.SQLException
import kotlin.system.exitProcess

@SpringBootApplication
class BackendApplication

fun main(args: Array<String>) {

    // get database connection
    val logger = LogManager.getLogger()
    logger.info("Starting SmoothiePOS Backend")
    if (EnvironmentVariables.values().any { System.getenv(it.name) == null }) {
        logger.error("Missing environment variables, exiting. Please provide the following variables:\n" +
                EnvironmentVariables.values().joinToString("\n") { it.name })
        exitProcess(1)
    }
    try {
        LogManager.getLogger().warn("CREATING NEW DATABASE CONNECTION")
        Database.getConnection() // creates a connection instance
        Database.getDatabase() // creates a database instance
    } catch (e: SQLException) {
        logger.error("Could not connect to database, exiting (${e.message})")
        exitProcess(1)
    }

    logger.info("Database created")

    runApplication<BackendApplication>(*args)
}
