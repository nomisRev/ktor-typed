package io.ktor.route.simple.auth

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkException
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.Verification
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.application
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.net.URL
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

data class Jwt<T : Any>(
    override val name: String? = null,
    val realm: String = "Ktor Server",
    val schemes: Schemes = Schemes(),
    val authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
    val verifier: (HttpAuthHeader) -> JWTVerifier?,
    val validate: suspend ApplicationCall.(JWTCredential) -> T?
) : TypedAuth<T> {

    companion object {
        fun <T : Any> hmaC256(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> T?
        ): Jwt<T> = of(Algorithm.HMAC256(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun <T : Any> hmaC384(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> T?
        ): Jwt<T> = of(Algorithm.HMAC384(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun <T : Any> hmaC512(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> T?
        ): Jwt<T> = of(Algorithm.HMAC512(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun <T : Any> of(
            algorithm: Algorithm,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> T?
        ): Jwt<T> = Jwt(
            name = name,
            realm = realm,
            schemes = schemes,
            authHeader = authHeader,
            verifier = {
                JWT.require(algorithm)
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            },
            validate
        )

        /** Automatically checks audience and issuer */
        fun <T : Any> jwk(
            certUrl: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> T
        ): Jwt<T> {
            val provider = JwkProviderBuilder(URL(certUrl))
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build()

            return Jwt(
                name = name,
                realm = realm,
                schemes = schemes,
                authHeader = authHeader,
                verifier = { token ->
                    // TODO patch in Verification.() -> Unit
                    getVerifier(provider, issuer, token, schemes) { }
                },
            ) { credential ->
                val isCorrect = credential.audience.contains(audience) && credential.issuer == issuer
                if (isCorrect) validate(credential) else null
            }
        }
    }
}

class Schemes(val defaultScheme: String = "Bearer", val additionalSchemes: List<String> = emptyList()) {
    val schemes = (arrayOf(defaultScheme) + additionalSchemes).toSet()
    val schemesLowerCase = schemes.map { it.lowercase(Locale.getDefault()) }.toSet()

    operator fun contains(scheme: String): Boolean = scheme.lowercase(Locale.getDefault()) in schemesLowerCase
}

private fun ApplicationCall.parseRequestAuthorizationHeaderOrNull() = try {
    request.parseAuthorizationHeader()
} catch (cause: IllegalArgumentException) {
    JWTLogger.trace("Illegal HTTP auth header", cause)
    null
}

private val JWTLogger: Logger = LoggerFactory.getLogger("io.ktor.auth.jwt")

private fun getVerifier(
    jwkProvider: JwkProvider,
    issuer: String?,
    token: HttpAuthHeader,
    schemes: Schemes,
    jwtConfigure: Verification.() -> Unit
): JWTVerifier? {
    val jwk = token.getBlob(schemes)?.let { blob ->
        try {
            jwkProvider.get(JWT.decode(blob).keyId)
        } catch (cause: JwkException) {
            JWTLogger.trace("Failed to get JWK", cause)
            null
        } catch (cause: JWTDecodeException) {
            JWTLogger.trace("Illegal JWT", cause)
            null
        }
    } ?: return null

    val algorithm = try {
        jwk.makeAlgorithm()
    } catch (cause: Throwable) {
        JWTLogger.trace("Failed to create algorithm {}: {}", jwk.algorithm, cause.message ?: cause.javaClass.simpleName)
        return null
    }

    return when (issuer) {
        null -> JWT.require(algorithm)
        else -> JWT.require(algorithm).withIssuer(issuer)
    }.apply(jwtConfigure).build()
}

private fun Jwk.makeAlgorithm(): Algorithm = when (algorithm) {
    "RS256" -> Algorithm.RSA256(publicKey as RSAPublicKey, null)
    "RS384" -> Algorithm.RSA384(publicKey as RSAPublicKey, null)
    "RS512" -> Algorithm.RSA512(publicKey as RSAPublicKey, null)
    "ES256" -> Algorithm.ECDSA256(publicKey as ECPublicKey, null)
    "ES384" -> Algorithm.ECDSA384(publicKey as ECPublicKey, null)
    "ES512" -> Algorithm.ECDSA512(publicKey as ECPublicKey, null)
    null -> Algorithm.RSA256(publicKey as RSAPublicKey, null)
    else -> throw IllegalArgumentException("Unsupported algorithm $algorithm")
}


private fun HttpAuthHeader.getBlob(schemes: Schemes) = when {
    this is HttpAuthHeader.Single && authScheme in schemes -> blob
    else -> null
}