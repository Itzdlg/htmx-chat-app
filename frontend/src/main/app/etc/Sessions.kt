package app.etc

import io.javalin.http.Context
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import sh.dominick.htmxpracticeapp.shared.sql.SessionsTable
import sh.dominick.htmxpracticeapp.shared.sql.UsersTable

val Context.session: ResultRow?
    get() {
        val token = this.cookie("session") ?: return null

        val session = transaction {
            SessionsTable.innerJoin(UsersTable).select {
                SessionsTable.token eq token
            }.firstOrNull()
        } ?: return null

        if (session[SessionsTable.expiresAt] <= System.currentTimeMillis()) {
            transaction {
                SessionsTable.deleteWhere { SessionsTable.token eq token }
            }

            return null
        }

        return session
    }