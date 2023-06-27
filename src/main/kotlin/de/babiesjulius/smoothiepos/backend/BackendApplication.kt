package de.babiesjulius.smoothiepos.backend

import de.babiesjulius.smoothiepos.backend.database.Database
import de.babiesjulius.smoothiepos.backend.system.EnvironmentVariables
import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
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
        logger.error(e.stackTraceToString())
        exitProcess(1)
    }

    logger.info("Database created")

    runApplication<BackendApplication>(*args)
}


@Configuration
@EnableWebMvc
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }
}