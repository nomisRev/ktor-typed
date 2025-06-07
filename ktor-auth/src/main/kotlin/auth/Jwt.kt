package io.ktor.route.simple.auth

import com.auth0.jwk.Jwk
import com.auth0.jwk.JwkException
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Verification
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.http.takeFrom
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.parseAuthorizationHeader
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException
import java.net.URL
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.util.Locale
import java.util.concurrent.TimeUnit

class Jwt<T : Any>(
    override val name: String?,
    val realm: String,
    val schemes: Schemes = Schemes(),
    val authHeader: (ApplicationCall) -> HttpAuthHeader?,
    val verifier: (HttpAuthHeader) -> JWTVerifier?,
    val validate: suspend ApplicationCall.(JWTCredential) -> Boolean,
    val transform: suspend ApplicationCall.(JWTCredential) -> T
) : TypedAuth<T> {

    inline fun <B : Any> map(crossinline block: (T) -> B): Jwt<B> =
        Jwt(name, realm, schemes, authHeader, verifier, validate) { block(transform(it)) }

    companion object {
        fun hmaC256(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> =
            of(Algorithm.HMAC256(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun <T : Any> hmaC384(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> =
            of(Algorithm.HMAC384(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun hmaC512(
            secret: String,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> =
            of(Algorithm.HMAC512(secret), audience, issuer, name, realm, schemes, authHeader, validate)

        fun of(
            algorithm: Algorithm,
            audience: String,
            issuer: String,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> = Jwt(
            name = name, realm = realm, schemes = schemes, authHeader = authHeader, verifier = {
                JWT.require(algorithm).withAudience(audience).withIssuer(issuer).build()
            }, validate = validate
        ) { it }

        @Serializable
        data class OpenIdMetadata(@SerialName("jwks_uri") val jwksUri: String)

        fun discoveryJwk(
            issuerUrl: String,
            audience: String? = null,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            verification: Verification.() -> Unit = { withAudience(audience) },
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> {
            val openIdMetadata = runBlocking {
                HttpClient(CIO) { install(ContentNegotiation) { json() } }
                    .get {
                        url.takeFrom("https://accounts.google.com")
                        url.appendPathSegments(".well-known", "openid-configuration")
                    }.body<OpenIdMetadata>()
            }

            return jwk(
                issuer = issuerUrl,
                jwksUri = openIdMetadata.jwksUri,
                audience = audience,
                name = name,
                realm = realm,
                schemes = schemes,
                authHeader = authHeader,
                verification = verification,
                validate = validate,
            )
        }

        fun jwk(
            issuer: String,
            jwksUri: String,
            audience: String? = null,
            name: String? = null,
            realm: String = "Ktor Server",
            schemes: Schemes = Schemes(),
            authHeader: (ApplicationCall) -> HttpAuthHeader? = ApplicationCall::parseRequestAuthorizationHeaderOrNull,
            verification: Verification.() -> Unit = { withAudience(audience) },
            validate: suspend ApplicationCall.(JWTCredential) -> Boolean = { true }
        ): Jwt<JWTCredential> {
            val provider =
                JwkProviderBuilder(URL(jwksUri)).cached(10, 24, TimeUnit.HOURS).rateLimited(10, 1, TimeUnit.MINUTES)
                    .build()

            return Jwt(
                name = name,
                realm = realm,
                schemes = schemes,
                authHeader = authHeader,
                verifier = { token -> getVerifier(provider, issuer, token, schemes, verification) },
                validate = validate
            ) { it }
        }
    }
}

class Schemes(val defaultScheme: String = "Bearer", val additionalSchemes: List<String> = emptyList()) {
    val schemes = (arrayOf(defaultScheme) + additionalSchemes).toSet()
    val schemesLowerCase = schemes.map { it.lowercase(Locale.getDefault()) }.toSet()

    operator fun contains(scheme: String): Boolean = scheme.lowercase(Locale.getDefault()) in schemesLowerCase
}

inline fun <reified A> JWTCredential.claimOrNull(name: String): A? {
    val claim = payload.getClaim(name)
    return when {
        claim.isMissing -> null
        claim.isNull -> return null
        else -> claim.`as`(A::class.java)
    }
}

inline fun <reified A> JWTCredential.claim(name: String): A {
    val claim = payload.getClaim(name)
    return when {
        claim.isMissing -> throw JWTVerificationException("Missing claim: $name")
        claim.isNull -> throw NullPointerException("Claim $name found, but found null value.")
        else -> claim.`as`(A::class.java)
    }
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