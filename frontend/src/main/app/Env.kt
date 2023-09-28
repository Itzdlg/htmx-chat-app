package app

import io.github.cdimascio.dotenv.dotenv

object Env {
    private val dotenv = dotenv()

    val PORT = dotenv["PORT"]?.toIntOrNull() ?: 80

    val DATABASE_DRIVER = dotenv["DATABASE_DRIVER"] ?: "com.mysql.cj.jdbc.Driver"
    val DATABASE_URL = dotenv["DATABASE_URL"] ?: "jdbc:mysql://localhost:3306/htmx_practice_app"
    val DATABASE_USERNAME = dotenv["DATABASE_USERNAME"] ?: "root"
    val DATABASE_PASSWORD = dotenv["DATABASE_PASSWORD"] ?: "passwd"
    val DATABASE_POOL_SIZE = dotenv["DATABASE_POOL_SIZE"]?.toIntOrNull() ?: 10

    val PASSWORD_SALT_LENGTH = dotenv["PASSWORD_SALT_LENGTH"]?.toIntOrNull() ?: 32
    val PASSWORD_HASH_ITERATIONS = dotenv["PASSWORD_HASH_ITERATIONS"]?.toIntOrNull() ?: 2
    val ARGON2_MEMORY = dotenv["ARGON2_MEMORY"]?.toIntOrNull() ?: 65535
    val ARGON2_PARALLELISM = dotenv["ARGON2_PARALLELISM"]?.toIntOrNull() ?: 1

    val HOT_RELOAD_JTE_TEMPLATES = dotenv["HOT_RELOAD_JTE_TEMPLATES"]?.toBooleanStrictOrNull() ?: true
}