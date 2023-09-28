package jte.components.messages

import org.jetbrains.exposed.sql.ResultRow
import sh.dominick.htmxpracticeapp.shared.sql.MessagesTable
import sh.dominick.htmxpracticeapp.shared.sql.UsersTable

class MessageModel(
    val id: Int,

    val username: String,
    val displayName: String,

    val messageContent: String,
    val messageSentAt: Long
) {
    companion object {
        fun from(it: ResultRow) = MessageModel(
            id = it[MessagesTable.id].value,
            username = it[UsersTable.username],
            displayName = it[UsersTable.displayName],
            messageContent = it[MessagesTable.message],
            messageSentAt = it[MessagesTable.sentAt]
        )
    }
}