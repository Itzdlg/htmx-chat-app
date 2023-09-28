package app.etc

import io.javalin.http.Context

fun Context.renderWithContext(location: String, vararg parameters: Pair<String, Any>) {
    val map = mapOf("ctx" to this, "context" to this, *parameters)
    this.render(location, map)
}