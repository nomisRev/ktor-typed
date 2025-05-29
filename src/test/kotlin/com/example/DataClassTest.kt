//package com.example
//
//import kotlin.test.Test
//
//class DataClassTest {
//    @Test
//    fun single() {
//        data class Single(val name: String)
//
//        testInput(
//            Path { "root" / string("name") }.asDataClass(::Single),
//            Single("John")
//        )
//    }
//
//    @Test
//    fun pair() {
//        testInput(
//            Path { "root" / string("name") / int("age") }
//                .asDataClass(::Pair),
//            Pair("John", 30)
//        )
//    }
//
//    @Test
//    fun triple() {
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") }
//                .asDataClass(::Triple),
//            Triple("John", 30, 1L)
//        )
//    }
//
//    @Test
//    fun four() {
//        data class Four(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") }
//                .asDataClass(::Four),
//            Four("John", 30, 1L, 1.5)
//        )
//    }
//
//    @Test
//    fun five() {
//        data class Five(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") }
//                .asDataClass(::Five),
//            Five("John", 30, 1L, 1.5, true)
//        )
//    }
//
//    @Test
//    fun six() {
//        data class Six(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") }
//                .asDataClass(::Six),
//            Six("John", 30, 1L, 1.5, true, 1.8f)
//        )
//    }
//
//    @Test
//    fun seven() {
//        data class Seven(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                )
//            }
//                .asDataClass(::Seven),
//            Seven("John", 30, 1L, 1.5, true, 1.8f, 70)
//        )
//    }
//
//    @Test
//    fun eight() {
//        data class Eight(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize")
//            }
//                .asDataClass(::Eight),
//            Eight("John", 30, 1L, 1.5, true, 1.8f, 70, 42)
//        )
//    }
//
//    @Test
//    fun nine() {
//        data class Nine(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email")
//            }
//                .asDataClass(::Nine),
//            Nine("John", 30, 1L, 1.5, true, 1.8f, 70, 42, "john@example.com")
//        )
//    }
//
//    @Test
//    fun ten() {
//        data class Ten(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone")
//            }.asDataClass(::Ten),
//            Ten("John", 30, 1L, 1.5, true, 1.8f, 70, 42, "john@example.com", "123-456-7890")
//        )
//    }
//
//    @Test
//    fun eleven() {
//        data class Eleven(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address")
//            }
//                .asDataClass(::Eleven),
//            Eleven("John", 30, 1L, 1.5, true, 1.8f, 70, 42, "john@example.com", "123-456-7890", "123 Main St")
//        )
//    }
//
//    @Test
//    fun twelve() {
//        data class Twelve(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city")
//            }
//                .asDataClass(::Twelve),
//            Twelve(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York"
//            )
//        )
//    }
//
//    @Test
//    fun thirteen() {
//        data class Thirteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state")
//            }
//                .asDataClass(::Thirteen),
//            Thirteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY"
//            )
//        )
//    }
//
//    @Test
//    fun fourteen() {
//        data class Fourteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                )
//            }
//                .asDataClass(::Fourteen),
//            Fourteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001"
//            )
//        )
//    }
//
//    @Test
//    fun fifteen() {
//        data class Fifteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country")
//            }
//                .asDataClass(::Fifteen),
//            Fifteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA"
//            )
//        )
//    }
//
//    @Test
//    fun sixteen() {
//        data class Sixteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation")
//            }
//                .asDataClass(::Sixteen),
//            Sixteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer"
//            )
//        )
//    }
//
//    @Test
//    fun seventeen() {
//        data class Seventeen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company")
//            }
//                .asDataClass(::Seventeen),
//            Seventeen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc"
//            )
//        )
//    }
//
//    @Test
//    fun eighteen() {
//        data class Eighteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String,
//            val department: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company") / string("department")
//            }
//                .asDataClass(::Eighteen),
//            Eighteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc",
//                "Engineering"
//            )
//        )
//    }
//
//    @Test
//    fun nineteen() {
//        data class Nineteen(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String,
//            val department: String,
//            val title: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company") / string("department") / string("title")
//            }
//                .asDataClass(::Nineteen),
//            Nineteen(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc",
//                "Engineering",
//                "Senior Developer"
//            )
//        )
//    }
//
//    @Test
//    fun twenty() {
//        data class Twenty(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String,
//            val department: String,
//            val title: String,
//            val salary: Int
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company") / string("department") / string("title") / int(
//                    "salary"
//                )
//            }
//                .asDataClass(::Twenty),
//            Twenty(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc",
//                "Engineering",
//                "Senior Developer",
//                100000
//            )
//        )
//    }
//
//    @Test
//    fun twentyOne() {
//        data class TwentyOne(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String,
//            val department: String,
//            val title: String,
//            val salary: Int,
//            val startDate: String
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company") / string("department") / string("title") / int(
//                    "salary"
//                ) / string("startDate")
//            }
//                .asDataClass(::TwentyOne),
//            TwentyOne(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc",
//                "Engineering",
//                "Senior Developer",
//                100000,
//                "2023-01-01"
//            )
//        )
//    }
//
//    @Test
//    fun twentyTwo() {
//        data class TwentyTwo(
//            val name: String,
//            val age: Int,
//            val userId: Long,
//            val price: Double,
//            val active: Boolean,
//            val height: Float,
//            val weight: Short,
//            val shoeSize: Byte,
//            val email: String,
//            val phone: String,
//            val address: String,
//            val city: String,
//            val state: String,
//            val zip: String,
//            val country: String,
//            val occupation: String,
//            val company: String,
//            val department: String,
//            val title: String,
//            val salary: Int,
//            val startDate: String,
//            val yearsOfExperience: Int
//        )
//
//        testInput(
//            Path {
//                "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / float("height") / short(
//                    "weight"
//                ) / byte("shoeSize") / string("email") / string("phone") / string("address") / string("city") / string("state") / string(
//                    "zip"
//                ) / string("country") / string("occupation") / string("company") / string("department") / string("title") / int(
//                    "salary"
//                ) / string("startDate") / int("yearsOfExperience")
//            }
//                .asDataClass(::TwentyTwo),
//            TwentyTwo(
//                "John",
//                30,
//                1L,
//                1.5,
//                true,
//                1.8f,
//                70,
//                42,
//                "john@example.com",
//                "123-456-7890",
//                "123 Main St",
//                "New York",
//                "NY",
//                "10001",
//                "USA",
//                "Developer",
//                "Acme Inc",
//                "Engineering",
//                "Senior Developer",
//                100000,
//                "2023-01-01",
//                10
//            )
//        )
//    }
//}
