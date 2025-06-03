package io.ktor.route.simple.auth

import io.ktor.server.sessions.CookieIdSessionBuilder
import io.ktor.server.sessions.CookieSessionBuilder
import io.ktor.server.sessions.SessionStorage

fun <T : Any> Session(
    name: String,
    builder: CookieSessionBuilder<T>.() -> Unit
): Session<T> = DefaultSession(name, builder)

fun <T : Any> Session(
    name: String,
    sessionStorage: SessionStorage,
    builder: CookieSessionBuilder<T>.() -> Unit
): Session<T> = IdSession(name, sessionStorage, builder)

sealed interface Session<T : Any> : TypedAuth<T> {
    override val name: String
}

class DefaultSession<T : Any>(
    override val name: String,
    val builder: CookieSessionBuilder<T>.() -> Unit
) : Session<T>

class IdSession<T : Any>(
    override val name: String,
    val sessionStorage: SessionStorage,
    val builder: CookieIdSessionBuilder<T>.() -> Unit
) : Session<T>
