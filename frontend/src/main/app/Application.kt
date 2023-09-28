package app

import app.routes.UserRoutes
import app.etc.hxRedirect
import app.etc.renderWithContext
import app.etc.session
import app.routes.MessageRoutes
import com.zaxxer.hikari.HikariDataSource
import gg.jte.TemplateEngine
import gg.jte.ContentType
import gg.jte.resolve.DirectoryCodeResolver
import io.javalin.Javalin
import io.javalin.community.routing.annotations.AnnotatedRoutingPlugin
import io.javalin.http.staticfiles.Location
import io.javalin.rendering.template.JavalinJte
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import sh.dominick.htmxpracticeapp.shared.sql.MessagesTable
import sh.dominick.htmxpracticeapp.shared.sql.SessionsTable
import sh.dominick.htmxpracticeapp.shared.sql.UsersTable
import java.nio.file.Path

fun main() {
    val dataSource = HikariDataSource().apply {
        maximumPoolSize = Env.DATABASE_POOL_SIZE
        driverClassName = Env.DATABASE_DRIVER
        jdbcUrl = Env.DATABASE_URL
        username = Env.DATABASE_USERNAME
        password = Env.DATABASE_PASSWORD
        isAutoCommit = false
    }

    val database = Database.connect(dataSource, databaseConfig = DatabaseConfig {
        keepLoadedReferencesOutOfTransaction = true
    })

    transaction {
        SchemaUtils.createMissingTablesAndColumns(
            UsersTable,
            MessagesTable,
            SessionsTable
        )
    }

    val templateEngine = if (Env.HOT_RELOAD_JTE_TEMPLATES) {
        val codeResolver = DirectoryCodeResolver(Path.of("src", "main", "jte"))
        TemplateEngine.create(codeResolver, ContentType.Html)
    } else TemplateEngine.createPrecompiled(Path.of("jte-classes"), ContentType.Html)

    JavalinJte.init(templateEngine)

    val app = Javalin.create { config ->
        config.plugins.enableCors { cors ->
            cors.add {
                it.anyHost()
            }
        }

        val routingPlugin = AnnotatedRoutingPlugin()
        routingPlugin.registerEndpoints(
            UserRoutes,
            MessageRoutes
        )

        config.plugins.register(routingPlugin)

        config.staticFiles.add("/", Location.CLASSPATH)
    }.start(Env.PORT)

    app.get("/") { ctx ->
        if (ctx.session == null)
            ctx.hxRedirect("/login")
        else ctx.hxRedirect("/dashboard")
    }

    app.get("/dashboard") { ctx ->
        if (ctx.session == null)
            ctx.hxRedirect("/")
        else ctx.renderWithContext("pages/dashboard.kte")
    }
}