package sh.dominick.htmxpracticeapp.shared.sql

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object SessionsTable : IntIdTable("sessions") {
    val token = varchar("token", 120).uniqueIndex()

    val user = reference("user", UsersTable, onDelete = ReferenceOption.CASCADE)
    val createdAt = long("created_at")
    val expiresAt = long("expires_at")

    val userAgent = text("user_agent").nullable()
}