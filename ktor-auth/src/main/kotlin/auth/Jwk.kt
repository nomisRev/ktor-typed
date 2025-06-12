package auth

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UnauthorizedResponse
import io.ktor.server.auth.jwt.JWTAuthChallengeFunction
import io.ktor.server.auth.jwt.JWTConfigureFunction
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import java.net.URL
import java.util.concurrent.TimeUnit

private val defaultChallenge: JWTAuthChallengeFunction = { scheme, realm ->
    call.respond(
        UnauthorizedResponse(
            HttpAuthHeader.Parameterized(
                scheme,
                mapOf(HttpAuthHeader.Parameters.Realm to realm)
            )
        )
    )
}

// TODO: turn this into a proper DSL.
fun Application.configureJwk(
    issuer: String,
    jwksUri: String? = null,
    name: String? = null,
    authHeaders: ((ApplicationCall) -> HttpAuthHeader?)? = null,
    challenge: JWTAuthChallengeFunction = defaultChallenge,
    configureVerifier: JWTConfigureFunction = {},
    configureJwkProvider: JwkProviderBuilder.() -> Unit = {
        cached(10, 24, TimeUnit.HOURS)
        rateLimited(10, 1, TimeUnit.MINUTES)
    },
    validate: suspend ApplicationCall.(JWTCredential) -> Any? = { JWTPrincipal(it.payload) },
) {
    install(Authentication) {
        jwt(name) {
            val jwksUri =
                jwksUri ?: TODO("Implement reaching issuer endpoint, and getting jwksUri from it. This needs suspend.")

            @Suppress("DEPRECATION")
            val jwkProvider = JwkProviderBuilder(URL(jwksUri))
                .apply(configureJwkProvider)
                .build()
            if (authHeaders != null) authHeader(authHeaders)
            verifier(jwkProvider, issuer, configureVerifier)
            validate(validate)
            challenge(challenge)
        }
    }
}
