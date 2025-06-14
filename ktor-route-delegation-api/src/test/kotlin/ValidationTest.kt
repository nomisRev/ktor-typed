package io.ktor.route.fastapi

import kotlin.test.*

class ValidationTest {

    // Helper function to test validation
    private fun <T> testValidation(validation: Validation<T>, value: T, shouldPass: Boolean = true): List<String> {
        val errors = mutableListOf<String>()
        with(validation) {
            errors.validate(value)
        }
        if (shouldPass) {
            assertTrue(errors.isEmpty(), "Expected validation to pass but got errors: $errors")
        } else {
            assertTrue(errors.isNotEmpty(), "Expected validation to fail but no errors were found")
        }
        return errors
    }

    @Test
    fun testIntValidations() {
        // Basic int validation
        testValidation(Validation.int(), 42)
        
        // Min validation
        testValidation(Validation.int().min(10), 15)
        testValidation(Validation.int().min(10), 5, shouldPass = false)
        
        // Max validation
        testValidation(Validation.int().max(100), 50)
        testValidation(Validation.int().max(100), 150, shouldPass = false)
        
        // Range validation
        testValidation(Validation.int().range(10, 100), 50)
        testValidation(Validation.int().range(10, 100), 5, shouldPass = false)
        testValidation(Validation.int().range(10, 100), 150, shouldPass = false)
        
        // Positive validation
        testValidation(Validation.int().positive(), 5)
        testValidation(Validation.int().positive(), 0, shouldPass = false)
        testValidation(Validation.int().positive(), -5, shouldPass = false)
        
        // Non-negative validation
        testValidation(Validation.int().nonNegative(), 0)
        testValidation(Validation.int().nonNegative(), 5)
        testValidation(Validation.int().nonNegative(), -1, shouldPass = false)
        
        // Negative validation
        testValidation(Validation.int().negative(), -5)
        testValidation(Validation.int().negative(), 0, shouldPass = false)
        testValidation(Validation.int().negative(), 5, shouldPass = false)
        
        // Chained validations
        testValidation(Validation.int().min(10).max(100), 50)
        testValidation(Validation.int().min(10).max(100), 5, shouldPass = false)
    }

    @Test
    fun testLongValidations() {
        testValidation(Validation.long(), 42L)
        testValidation(Validation.long().min(10L), 15L)
        testValidation(Validation.long().min(10L), 5L, shouldPass = false)
        testValidation(Validation.long().max(100L), 50L)
        testValidation(Validation.long().max(100L), 150L, shouldPass = false)
        testValidation(Validation.long().range(10L, 100L), 50L)
        testValidation(Validation.long().positive(), 5L)
        testValidation(Validation.long().positive(), 0L, shouldPass = false)
        testValidation(Validation.long().nonNegative(), 0L)
        testValidation(Validation.long().negative(), -5L)
        testValidation(Validation.long().negative(), 0L, shouldPass = false)
    }

    @Test
    fun testDoubleValidations() {
        testValidation(Validation.double(), 42.5)
        testValidation(Validation.double().min(10.0), 15.5)
        testValidation(Validation.double().min(10.0), 5.5, shouldPass = false)
        testValidation(Validation.double().max(100.0), 50.5)
        testValidation(Validation.double().max(100.0), 150.5, shouldPass = false)
        testValidation(Validation.double().range(10.0, 100.0), 50.5)
        testValidation(Validation.double().positive(), 5.5)
        testValidation(Validation.double().positive(), 0.0, shouldPass = false)
        testValidation(Validation.double().positive(), -5.5, shouldPass = false)
        testValidation(Validation.double().nonNegative(), 0.0)
        testValidation(Validation.double().negative(), -5.5)
        testValidation(Validation.double().negative(), 0.0, shouldPass = false)
    }

    @Test
    fun testFloatValidations() {
        testValidation(Validation.float(), 42.5f)
        testValidation(Validation.float().min(10.0f), 15.5f)
        testValidation(Validation.float().min(10.0f), 5.5f, shouldPass = false)
        testValidation(Validation.float().max(100.0f), 50.5f)
        testValidation(Validation.float().max(100.0f), 150.5f, shouldPass = false)
        testValidation(Validation.float().range(10.0f, 100.0f), 50.5f)
        testValidation(Validation.float().positive(), 5.5f)
        testValidation(Validation.float().positive(), 0.0f, shouldPass = false)
        testValidation(Validation.float().nonNegative(), 0.0f)
        testValidation(Validation.float().negative(), -5.5f)
        testValidation(Validation.float().negative(), 0.0f, shouldPass = false)
    }

    @Test
    fun testShortValidations() {
        testValidation(Validation.short(), 42.toShort())
        testValidation(Validation.short().min(10.toShort()), 15.toShort())
        testValidation(Validation.short().min(10.toShort()), 5.toShort(), shouldPass = false)
        testValidation(Validation.short().max(100.toShort()), 50.toShort())
        testValidation(Validation.short().max(100.toShort()), 150.toShort(), shouldPass = false)
        testValidation(Validation.short().range(10.toShort(), 100.toShort()), 50.toShort())
        testValidation(Validation.short().positive(), 5.toShort())
        testValidation(Validation.short().positive(), 0.toShort(), shouldPass = false)
        testValidation(Validation.short().nonNegative(), 0.toShort())
        testValidation(Validation.short().negative(), (-5).toShort())
        testValidation(Validation.short().negative(), 0.toShort(), shouldPass = false)
    }

    @Test
    fun testByteValidations() {
        testValidation(Validation.byte(), 42.toByte())
        testValidation(Validation.byte().min(10.toByte()), 15.toByte())
        testValidation(Validation.byte().min(10.toByte()), 5.toByte(), shouldPass = false)
        testValidation(Validation.byte().max(100.toByte()), 50.toByte())
        testValidation(Validation.byte().max(100.toByte()), 127.toByte(), shouldPass = false)
        testValidation(Validation.byte().range(10.toByte(), 100.toByte()), 50.toByte())
        testValidation(Validation.byte().positive(), 5.toByte())
        testValidation(Validation.byte().positive(), 0.toByte(), shouldPass = false)
        testValidation(Validation.byte().nonNegative(), 0.toByte())
        testValidation(Validation.byte().negative(), (-5).toByte())
        testValidation(Validation.byte().negative(), 0.toByte(), shouldPass = false)
    }

    @Test
    fun testStringValidations() {
        // Basic string validation
        testValidation(Validation.string(), "hello")
        
        // Length validations
        testValidation(Validation.string().minLength(3), "hello")
        testValidation(Validation.string().minLength(3), "hi", shouldPass = false)
        testValidation(Validation.string().maxLength(10), "hello")
        testValidation(Validation.string().maxLength(3), "hello", shouldPass = false)
        testValidation(Validation.string().length(3, 10), "hello")
        testValidation(Validation.string().length(3, 10), "hi", shouldPass = false)
        testValidation(Validation.string().length(3, 10), "this is too long", shouldPass = false)
        testValidation(Validation.string().exactLength(5), "hello")
        testValidation(Validation.string().exactLength(5), "hi", shouldPass = false)
        
        // Empty/blank validations
        testValidation(Validation.string().notEmpty(), "hello")
        testValidation(Validation.string().notEmpty(), "", shouldPass = false)
        testValidation(Validation.string().notBlank(), "hello")
        testValidation(Validation.string().notBlank(), "   ", shouldPass = false)
        testValidation(Validation.string().notBlank(), "", shouldPass = false)
        
        // Regex validation
        testValidation(Validation.string().regex("[a-z]+"), "hello")
        testValidation(Validation.string().regex("[a-z]+"), "Hello", shouldPass = false)
        testValidation(Validation.string().regex("[a-z]+"), "hello123", shouldPass = false)
        
        // Contains validation
        testValidation(Validation.string().contains("ell"), "hello")
        testValidation(Validation.string().contains("xyz"), "hello", shouldPass = false)
        testValidation(Validation.string().contains("ELL", ignoreCase = true), "hello")
        
        // Starts/ends with validation
        testValidation(Validation.string().startsWith("hel"), "hello")
        testValidation(Validation.string().startsWith("xyz"), "hello", shouldPass = false)
        testValidation(Validation.string().endsWith("llo"), "hello")
        testValidation(Validation.string().endsWith("xyz"), "hello", shouldPass = false)
        
        // One of validation
        testValidation(Validation.string().oneOf("hello", "world"), "hello")
        testValidation(Validation.string().oneOf("hello", "world"), "xyz", shouldPass = false)
        testValidation(Validation.string().oneOf("hello", "world", ignoreCase = true), "HELLO")
        
        // Character type validations
        testValidation(Validation.string().alphanumeric(), "hello123")
        testValidation(Validation.string().alphanumeric(), "hello-123", shouldPass = false)
        testValidation(Validation.string().alphabetic(), "hello")
        testValidation(Validation.string().alphabetic(), "hello123", shouldPass = false)
        testValidation(Validation.string().numeric(), "123")
        testValidation(Validation.string().numeric(), "hello", shouldPass = false)
        
        // Case validations
        testValidation(Validation.string().lowercase(), "hello")
        testValidation(Validation.string().lowercase(), "Hello", shouldPass = false)
        testValidation(Validation.string().uppercase(), "HELLO")
        testValidation(Validation.string().uppercase(), "Hello", shouldPass = false)
        
        // Chained validations
        testValidation(Validation.string().minLength(3).maxLength(10).alphanumeric(), "hello123")
        testValidation(Validation.string().minLength(3).maxLength(10).alphanumeric(), "hi", shouldPass = false)
    }

    @Test
    fun testBooleanValidations() {
        testValidation(Validation.boolean(), true)
        testValidation(Validation.boolean(), false)
        testValidation(Validation.boolean().isTrue(), true)
        testValidation(Validation.boolean().isTrue(), false, shouldPass = false)
        testValidation(Validation.boolean().isFalse(), false)
        testValidation(Validation.boolean().isFalse(), true, shouldPass = false)
    }

    @Test
    fun testEmailValidation() {
        testValidation(Validation.email(), "test@example.com")
        testValidation(Validation.email(), "user.name+tag@domain.co.uk")
        testValidation(Validation.email(), "invalid-email", shouldPass = false)
        testValidation(Validation.email(), "test@", shouldPass = false)
        testValidation(Validation.email(), "@example.com", shouldPass = false)
        testValidation(Validation.email(), "test.example.com", shouldPass = false)
    }

    @Test
    fun testUrlValidation() {
        testValidation(Validation.url(), "https://example.com")
        testValidation(Validation.url(), "http://subdomain.example.com/path?query=value")
        testValidation(Validation.url(), "invalid-url", shouldPass = false)
        testValidation(Validation.url(), "ftp://example.com", shouldPass = false)
        testValidation(Validation.url(), "example.com", shouldPass = false)
    }

    @Test
    fun testUuidValidation() {
        testValidation(Validation.uuid(), "123e4567-e89b-12d3-a456-426614174000")
        testValidation(Validation.uuid(), "550e8400-e29b-41d4-a716-446655440000")
        testValidation(Validation.uuid(), "invalid-uuid", shouldPass = false)
        testValidation(Validation.uuid(), "123e4567-e89b-12d3-a456", shouldPass = false)
        testValidation(Validation.uuid(), "123e4567-e89b-12d3-a456-42661417400g", shouldPass = false)
    }

    @Test
    fun testCollectionValidations() {
        val list = listOf("a", "b", "c")
        val emptyList = emptyList<String>()
        val singleItemList = listOf("a")
        
        // Size validations
        testValidation(Validation<Collection<String>> { it }.minSize(2), list)
        testValidation(Validation<Collection<String>> { it }.minSize(2), singleItemList, shouldPass = false)
        testValidation(Validation<Collection<String>> { it }.maxSize(5), list)
        testValidation(Validation<Collection<String>> { it }.maxSize(2), list, shouldPass = false)
        testValidation(Validation<Collection<String>> { it }.size(2, 5), list)
        testValidation(Validation<Collection<String>> { it }.size(2, 5), singleItemList, shouldPass = false)
        testValidation(Validation<Collection<String>> { it }.exactSize(3), list)
        testValidation(Validation<Collection<String>> { it }.exactSize(3), singleItemList, shouldPass = false)
        
        // Not empty validation
        testValidation(Validation<Collection<String>> { it }.notEmpty(), list)
        testValidation(Validation<Collection<String>> { it }.notEmpty(), emptyList, shouldPass = false)
    }

    @Test
    fun testCustomErrorMessages() {
        val customMessage = "Custom error message"
        val errors = testValidation(
            Validation.int().min(10, customMessage), 
            5, 
            shouldPass = false
        )
        assertTrue(errors.contains(customMessage))
    }

    @Test
    fun testChainedValidations() {
        // Test multiple validation failures
        val errors = mutableListOf<String>()
        val validation = Validation.string()
            .minLength(5, "Too short")
            .maxLength(10, "Too long")
            .alphanumeric("Must be alphanumeric")
        
        with(validation) {
            errors.validate("hi!")
        }
        
        assertTrue(errors.size >= 2) // Should have at least "Too short" and "Must be alphanumeric"
        assertTrue(errors.any { it.contains("Too short") })
        assertTrue(errors.any { it.contains("Must be alphanumeric") })
    }

    @Test
    fun testValidationPassing() {
        // Test that validation returns the original value when passing
        val value = 42
        val errors = mutableListOf<String>()
        val result = with(Validation.int().min(10).max(100)) {
            errors.validate(value)
        }
        
        assertEquals(value, result)
        assertTrue(errors.isEmpty())
    }
}