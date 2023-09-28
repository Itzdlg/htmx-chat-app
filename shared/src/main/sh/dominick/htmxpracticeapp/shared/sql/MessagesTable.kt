package sh.dominick.htmxpracticeapp.shared.sql

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object MessagesTable : IntIdTable("messages") {
    val message = text("message")
    val user = optReference("user", UsersTable, onDelete = ReferenceOption.SET_NULL)

    val sentAt = long("sent_at")
}