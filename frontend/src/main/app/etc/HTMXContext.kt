package app.etc

import io.javalin.http.Context

val Context.isHtmxRequest
    get() = this.header("HX-Request") == "true"

fun Context.hxRedirect(location: String) {
    if (this.isHtmxRequest) {
        this.header("HX-Redirect", location)
        return
    }

    this.redirect(location)
}