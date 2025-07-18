package io.github.nomisrev.typedapi

/**
 * Annotation to mark a class as an endpoint.
 * This annotation is used to identify classes that represent API endpoints.
 * 
 * @param path The URL path for this endpoint. Can be a string value.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Endpoint(val path: String)