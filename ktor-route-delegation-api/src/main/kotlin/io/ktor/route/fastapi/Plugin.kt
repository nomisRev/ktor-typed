package io.ktor.route.fastapi

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.util.*

val ErrorsKey = AttributeKey<MutableList<String>>("Errors")

class ErrorAccumulatorConfig {
    internal var onError: suspend ApplicationCall.(List<String>) -> Unit = { errors ->
        respond(HttpStatusCode.BadRequest, errors.joinToString())
    }

    fun onError(handler: suspend ApplicationCall.(List<String>) -> Unit) {
        onError = handler
    }
}

val ErrorAccumulatorPlugin = createRouteScopedPlugin(
    name = "ErrorAccumulatorPlugin",
    createConfiguration = ::ErrorAccumulatorConfig
) {
    val errorHandler = pluginConfig.onError

    onCall { call ->
        call.attributes.put(ErrorsKey, mutableListOf())
    }

    onCallRespond { call ->
        call.attributes.remove(ErrorsKey)
    }
}

