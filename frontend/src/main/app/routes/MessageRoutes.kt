package app.routes

import app.etc.session
import io.javalin.community.routing.annotations.Get
import io.javalin.community.routing.annotations.Post
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import jte.components.messages.MessageModel
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import sh.dominick.htmxpracticeapp.shared.sql.MessagesTable
import sh.dominick.htmxpracticeapp.shared.sql.UsersTable

object MessageRoutes {
    @Get("/messages/before")
    fun renderMessagesBefore(ctx: Context) {
        val time = ctx.queryParam("time")?.toLongOrNull()
            ?: throw BadRequestResponse()

        val messages = transaction {
            MessagesTable
                .innerJoin(UsersTable)
                .select { MessagesTable.sentAt less time }
                .orderBy(MessagesTable.sentAt to SortOrder.DESC)
                .limit(50)
                .map(MessageModel::from)
                .reversed()
        }

        ctx.render("components/messages/prependMessageList.kte", mapOf("messages" to messages))
    }

    @Get("/messages/after")
    fun renderMessagesAfter(ctx: Context) {
        val time = ctx.queryParam("time")?.toLongOrNull()
            ?: throw BadRequestResponse()

        val messages = transaction {
            MessagesTable
                .innerJoin(UsersTable)
                .select { MessagesTable.sentAt greater time }
                .orderBy(MessagesTable.sentAt to SortOrder.ASC)
                .limit(50)
                .map(MessageModel::from)
        }

        ctx.render("components/messages/appendMessageList.kte", mapOf(
            "messages" to messages,
            "previous" to messages.lastOrNull()
        ))
    }

    @Post("/messages/send")
    fun sendMessage(ctx: Context) {
        if (ctx.session == null) return
        ctx.render("components/messages/sendMessage.kte")

        val messageContent = ctx.formParam("messageContent")
        if (messageContent.isNullOrBlank()) return

        transaction {
            MessagesTable.insert {
                it[MessagesTable.user] = ctx.session!![UsersTable.id]
                it[MessagesTable.message] = messageContent
                it[MessagesTable.sentAt] = System.currentTimeMillis()
            }
        }
    }
}