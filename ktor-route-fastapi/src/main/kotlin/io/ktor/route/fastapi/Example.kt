package io.ktor.route.fastapi

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Item(val name: String, val price: Double)

@Serializable
data class UpdateItemResponse(
    val item_id: Int,
    val item: Item,
    val q: String? = null,
    val user_agent: String? = null,
    val x_token: String? = null
)

/**
 * Example demonstrating FastAPI-style routing in Ktor.
 * 
 * This example shows how to create a route similar to this FastAPI code:
 * 
 * ```python
 * @app.put("/items/{item_id}")
 * async def update_item(
 *     *,
 *     item_id: int = Path(..., title="The ID of the item to get", ge=1),
 *     q: Optional[str] = Query(None, min_length=3, max_length=50, description="A query string"),
 *     item: Item,
 *     user_agent: Optional[str] = Header(None, description="The user agent of the client"),
 *     x_token: Optional[str] = Header(None, description="A custom header token")
 * ):
 *     results = {"item_id": item_id, "item": item}
 *     if q:
 *         results.update({"q": q})
 *     if user_agent:
 *         results.update({"user_agent": user_agent})
 *     if x_token:
 *         results.update({"x_token": x_token})
 *     return results
 * ```
 */
fun Application.configureFastAPIExample() {
    install(ContentNegotiation) {
        json()
    }
    
    routing {
        // FastAPI-style PUT route with parameter metadata and validation
        put("/items/{item_id}",
            p1 = Path.required<Int>(title = "The ID of the item to get", ge = 1),
            p2 = Query<String?>(default = null, minLength = 3, maxLength = 50, description = "A query string"),
            p3 = Body<Item>(), // Item will be parsed from body automatically
            p4 = Header<String?>(default = null, description = "The user agent of the client"),
            p5 = Header<String?>(default = null, description = "A custom header token")
        ) { itemId: Int, q: String?, item: Item, userAgent: String?, xToken: String? ->
            
            val results = UpdateItemResponse(
                item_id = itemId,
                item = item,
                q = q,
                user_agent = userAgent,
                x_token = xToken
            )
            call.respond(results)
        }
        
        // Simple GET route example
        get("/users/{userId}",
            p1 = Path.required<String>(description = "User ID"),
            p2 = Query<String?>(default = "Unknown", description = "User name")
        ) { userId: String, name: String? ->
            call.respond(mapOf(
                "userId" to userId,
                "name" to name
            ))
        }
    }
}