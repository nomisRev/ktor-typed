package io.ktor.route.simple.auth

import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.application
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlin.reflect.KClass

sealed interface TypedAuth<T : Any> {
    val name: String?
}

inline fun <reified T : Any> Route.install(vararg auths: TypedAuth<T>) {
    application.authentication {
        auths.forEach { auth ->
            when (auth) {
                // Inside Ktor we can verify if it already exists or not.
                // Here we swallow the already existing error
                is Jwt<*> -> runCatching {
                    jwt(auth.name) {
                        realm = auth.realm
                        verifier(auth.verifier)
                        authHeader(auth.authHeader)
                        authSchemes(auth.schemes.defaultScheme, *auth.schemes.additionalSchemes.toTypedArray())
                        validate { credential ->
                            if (auth.validate(this, credential)) auth.transform(this, credential)
                            else null
                        }
                    }
                }

                is Session<T> -> install(Sessions) {
                    when (auth) {
                        is DefaultSession<T> -> cookie<T>(auth.name, auth.builder)
                        is IdSession<T> -> cookie<T>(auth.name, auth.sessionStorage, auth.builder)
                    }
                }
            }
        }
    }
}

inline fun <reified T : Any> Route.authenticate(
    vararg auths: TypedAuth<T>,
    crossinline build: SecuredRoute<T>.() -> Unit
): Route =
    authenticate(*auths.map { it.name }.toTypedArray()) {
        SecuredRoute(auths, this, T::class).build()
    }

class SecuredRoute<T : Any>(
    private val auth: Array<out TypedAuth<T>>,
    private val route: Route,
    private val kClass: KClass<T>
) : Route by route {
    fun RoutingContext.principal(): T = auth.firstNotNullOfOrNull { auth ->
        call.authentication.principal(auth.name, kClass)
    } ?: throw IllegalStateException("We cannot reach this point without having a valid principal.")
}
