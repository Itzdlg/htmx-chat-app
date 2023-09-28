package app.routes

import app.Env
import app.etc.hxRedirect
import app.etc.renderWithContext
import de.mkammerer.argon2.Argon2Factory
import io.javalin.community.routing.annotations.Get
import io.javalin.community.routing.annotations.Post
import io.javalin.http.Context
import io.javalin.http.Header
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import sh.dominick.htmxpracticeapp.shared.sql.SessionsTable
import sh.dominick.htmxpracticeapp.shared.sql.UsersTable
import kotlin.time.Duration.Companion.minutes

object UserRoutes {
    @Get("/register")
    fun renderRegister(ctx: Context) {
        ctx.renderWithContext("pages/register.jte")
    }

    @Post("/register")
    fun register(ctx: Context) {
        val username = ctx.formParam("username")
        val displayName = ctx.formParam("displayName")
        val password = ctx.formParam("password")

        if (setOf(username, displayName, password).any { it.isNullOrBlank() }) {
            ctx.render("components/alert.kte", mapOf("message" to "You are missing form fields!"))
            return
        }

        val exists = transaction {
            UsersTable.select { UsersTable.username.lowerCase() eq username!!.lowercase() }.count() > 0
        }

        if (exists) {
            ctx.render("components/alert.kte", mapOf("message" to "A user with that username already exists."))
            return
        }

        val argon2 = Argon2Factory.create(
            Env.PASSWORD_SALT_LENGTH,
            1024
        )

        val passwordArray = password!!.toCharArray()
        val hashedPassword = try {
            argon2.hash(
                Env.PASSWORD_HASH_ITERATIONS,
                Env.ARGON2_MEMORY,
                Env.ARGON2_PARALLELISM,
                passwordArray
            )
        } finally {
            argon2.wipeArray(passwordArray)
        }

        transaction {
            UsersTable.insert {
                it[UsersTable.username] = username!!
                it[UsersTable.displayName] = displayName!!
                it[UsersTable.argon2] = hashedPassword
            }
        }

        ctx.hxRedirect("/login")
    }

    @Get("/login")
    fun renderLogin(ctx: Context) {
        ctx.removeCookie("session")
        ctx.renderWithContext("pages/login.jte")
    }

    @Post("/login")
    fun login(ctx: Context) {
        val username = ctx.formParam("username")
        val password = ctx.formParam("password")

        if (setOf(username, password).any { it.isNullOrBlank() }) {
            ctx.render("components/alert.kte", mapOf("message" to "You are missing a username or password!"))
            return
        }

        val account = transaction {
            UsersTable.select {
                UsersTable.username.lowerCase() eq username!!.lowercase()
            }.firstOrNull()
        }

        if (account == null) {
            ctx.render("components/alert.kte", mapOf("message" to "There is no account with that username."))
            return
        }

        val argon2 = Argon2Factory.create()
        val passwordArray = password!!.toCharArray()

        val isPassword = try {
            argon2.verify(account[UsersTable.argon2], passwordArray)
        } finally {
            argon2.wipeArray(passwordArray)
        }

        if (!isPassword) {
            ctx.render("components/alert.kte", mapOf("message" to "Those credentials are incorrect."))
            return
        }

        val session = transaction {
            SessionsTable.insert {
                it[SessionsTable.token] = StringBuilder().also { str ->
                    repeat(120) { str.append("abcdefghijklmnopqrstuvwxyz".random()) }
                }.toString()

                it[SessionsTable.user] = account[UsersTable.id]
                it[SessionsTable.createdAt] = System.currentTimeMillis()
                it[SessionsTable.expiresAt] = System.currentTimeMillis() + 30.minutes.inWholeMilliseconds

                it[SessionsTable.userAgent] = ctx.header(Header.USER_AGENT)
            }
        }

        ctx.cookie("session", session[SessionsTable.token])
        ctx.hxRedirect("/")
    }
}