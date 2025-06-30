package io.github.nomisrev.typedapi.ktor

import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import io.github.nomisrev.typedapi.DelegateProvider
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Info
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Validation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith

public fun <A : Any> Route.route(
    path: String,
    method: HttpMethod,
    endpoint: (EndpointAPI) -> A,
    block: suspend RoutingContext.(A) -> Unit,
) =
    route(path, method) {
        handle {
            val api = ServerEndpointAPI(this)
            val value = endpoint(api)
            api.bodyInput?.let { input ->
                api.body.completeWith(runCatching { call.receiveNullable(TypeInfo(input.kClass, input.kType)) })
            }
            block(value)
        }
    }

private class ServerEndpointAPI(private val context: RoutingContext) : EndpointAPI {
    val body: CompletableDeferred<Any?> = CompletableDeferred()
    var bodyInput: Input.Body<*>? = null

    override fun <A> input(input: Input<A>): DelegateProvider<A> =
        DelegateProvider { _, _ ->
            when (input) {
                is Input.Body -> InputDelegate.Body<A>(
                    input.kClass,
                    input.kType,
                    body as CompletableDeferred<A>,
                    input.info as Info<A>?
                ).also { bodyInput = input }

                is Input.Header<*> -> InputDelegate.Header<A>(
                    input.name,
                    input.casing,
                    input.validation as Validation<A>?,
                    input.kClass,
                    input.kType,
                    input.info as Info<A>?,
                    context
                )

                is Input.Path<*> -> InputDelegate.Path<A>(
                    input.name,
                    input.validation as Validation<A>?,
                    input.kClass,
                    input.kType,
                    input.info as Info<A>?,
                    context
                )

                is Input.Query<*> -> InputDelegate.Query<A>(
                    input.name,
                    input.validation as Validation<A>?,
                    input.kClass,
                    input.kType,
                    input.info as Info<A>?,
                    context
                )
            }
        }
}
