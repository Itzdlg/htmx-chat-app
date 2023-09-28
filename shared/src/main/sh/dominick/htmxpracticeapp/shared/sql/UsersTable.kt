package sh.dominick.htmxpracticeapp.shared.sql

import org.jetbrains.exposed.dao.id.IntIdTable

object UsersTable : IntIdTable("users") {
    val username = varchar("username", 32)
    val displayName = varchar("display_name", 32)
    val argon2 = text("argon2")
}