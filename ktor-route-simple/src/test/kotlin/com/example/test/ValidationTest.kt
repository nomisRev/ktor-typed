package com.example.test

import io.ktor.route.simple.validate
import kotlinx.serialization.Serializable
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationTest {

    @Serializable
    data class User(
        val id: String,
        val name: String,
        val age: Int,
        val tags: List<String>
    )

    @Test
    fun `test fluent validation DSL syntax`() {
        // Define validation rules using the new fluent syntax
        val userValidation = validate<User> {
            User::id.notBlank().minLength(3)
            User::name.notBlank()
            User::age.min(18).max(120)
            User::tags.notEmpty()
        }

        // Create a valid user
        val validUser = User(
            id = "user123",
            name = "John Doe",
            age = 30,
            tags = listOf("customer", "premium")
        )

        // Validate the user - should not throw an exception
        val result = userValidation(validUser)
        assertEquals(emptyList(), result)

        // Create an invalid user
        val invalidUser = User(
            id = "u", // Too short
            name = "", // Blank
            age = 15, // Too young
            tags = emptyList() // Empty
        )

        val errors = userValidation(invalidUser)

        assertEquals(4, errors.size)
        assertTrue(errors.any { it.contains("at least 3 characters") })
        assertTrue(errors.any { it.contains("must not be blank") })
        assertTrue(errors.any { it.contains("at least 18") })
        assertTrue(errors.any { it.contains("must not be empty") })
    }
}
