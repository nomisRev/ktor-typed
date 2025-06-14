import convert
import kotlin.test.*

class TypeConversionTest {

    @Test
    fun `test convert String to String`() {
        val result = convert("hello", String::class)
        assertEquals("hello", result)
    }

    @Test
    fun `test convert String to Int`() {
        val result = convert("42", Int::class)
        assertEquals(42, result)
    }

    @Test
    fun `test convert String to Long`() {
        val result = convert("1234567890", Long::class)
        assertEquals(1234567890L, result)
    }

    @Test
    fun `test convert String to Double`() {
        val result = convert("3.14159", Double::class)
        assertEquals(3.14159, result)
    }

    @Test
    fun `test convert String to Float`() {
        val result = convert("2.5", Float::class)
        assertEquals(2.5f, result)
    }

    @Test
    fun `test convert String to Boolean true`() {
        val result = convert("true", Boolean::class)
        assertEquals(true, result)
    }

    @Test
    fun `test convert String to Boolean false`() {
        val result = convert("false", Boolean::class)
        assertEquals(false, result)
    }

    @Test
    fun `test convert invalid Int throws NumberFormatException`() {
        assertFailsWith<NumberFormatException> {
            convert("not-a-number", Int::class)
        }
    }

    @Test
    fun `test convert invalid Long throws NumberFormatException`() {
        assertFailsWith<NumberFormatException> {
            convert("not-a-long", Long::class)
        }
    }

    @Test
    fun `test convert invalid Double throws NumberFormatException`() {
        assertFailsWith<NumberFormatException> {
            convert("not-a-double", Double::class)
        }
    }

    @Test
    fun `test convert invalid Float throws NumberFormatException`() {
        assertFailsWith<NumberFormatException> {
            convert("not-a-float", Float::class)
        }
    }

    @Test
    fun `test convert empty string to Int throws NumberFormatException`() {
        assertFailsWith<NumberFormatException> {
            convert("", Int::class)
        }
    }

    @Test
    fun `test convert unsupported type throws IllegalArgumentException`() {
        class UnsupportedType
        
        assertFailsWith<IllegalArgumentException> {
            convert("value", UnsupportedType::class)
        }
    }

    @Test
    fun `test convert edge case numbers`() {
        // Test Int boundaries
        assertEquals(Int.MAX_VALUE, convert(Int.MAX_VALUE.toString(), Int::class))
        assertEquals(Int.MIN_VALUE, convert(Int.MIN_VALUE.toString(), Int::class))
        
        // Test Long boundaries
        assertEquals(Long.MAX_VALUE, convert(Long.MAX_VALUE.toString(), Long::class))
        assertEquals(Long.MIN_VALUE, convert(Long.MIN_VALUE.toString(), Long::class))
        
        // Test zero values
        assertEquals(0, convert("0", Int::class))
        assertEquals(0L, convert("0", Long::class))
        assertEquals(0.0, convert("0.0", Double::class))
        assertEquals(0.0f, convert("0.0", Float::class))
    }

    @Test
    fun `test convert Boolean with different cases`() {
        // Note: Boolean.toBoolean() is case-insensitive and only returns true for "true"
        assertEquals(true, convert("true", Boolean::class))
        assertEquals(true, convert("True", Boolean::class))
        assertEquals(true, convert("TRUE", Boolean::class))
        assertEquals(false, convert("false", Boolean::class))
        assertEquals(false, convert("False", Boolean::class))
        assertEquals(false, convert("FALSE", Boolean::class))
        assertEquals(false, convert("anything-else", Boolean::class))
    }

    @Test
    fun `test convert with whitespace`() {
        // First, let's test what Kotlin's built-in functions actually do
        val intResult = " 42 ".toIntOrNull()
        val doubleResult = " 3.14 ".toDoubleOrNull()
        
        // If they return null, then our convert function should throw NumberFormatException
        // If they return values, then our convert function should work
        
        if (intResult == null) {
            assertFailsWith<NumberFormatException> {
                convert(" 42 ", Int::class)
            }
        } else {
            assertEquals(42, convert(" 42 ", Int::class))
        }
        
        if (doubleResult == null) {
            assertFailsWith<NumberFormatException> {
                convert(" 3.14 ", Double::class)
            }
        } else {
            assertEquals(3.14, convert(" 3.14 ", Double::class))
        }
        
        // Boolean.toBoolean() returns false for anything that's not exactly "true" (case insensitive)
        assertEquals(false, convert(" true ", Boolean::class))
        
        // String should preserve whitespace
        assertEquals(" hello ", convert(" hello ", String::class))
        
        // Test that trimmed values work correctly
        assertEquals(42, convert("42", Int::class))
        assertEquals(3.14, convert("3.14", Double::class))
        assertEquals(true, convert("true", Boolean::class))
    }
}