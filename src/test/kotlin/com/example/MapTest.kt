//package com.example
//
//import kotlin.test.Test
//
//class MapTest {
//
//    @Test
//    fun single() {
//        data class Single(val name: String)
//
//        testInput(
//            Path { "root" / string("name") }
//                .map(::Single) { a -> a.name },
//            Single("John")
//        )
//    }
//
//    @Test
//    fun pair() {
//        testInput(
//            Path { "root" / string("name") / int("age") }
//                .map(::Pair) { (a, b) -> Params2(a, b) },
//            Pair("John", 30)
//        )
//    }
//
//    @Test
//    fun triple() {
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") }
//                .map(::Triple) { (a, b, c) -> Params3(a, b, c) },
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
//                .map(::Four) { (a, b, c, d) -> Params4(a, b, c, d) },
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
//                .map(::Five) { (a, b, c, d, e) -> Params5(a, b, c, d, e) },
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
//            val role: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") }
//                .map(::Six) { (a, b, c, d, e, f) -> Params6(a, b, c, d, e, f) },
//            Six("John", 30, 1L, 1.5, true, "admin")
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
//            val role: String,
//            val level: Int
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") }
//                .map(::Seven) { (a, b, c, d, e, f, g) -> Params7(a, b, c, d, e, f, g) },
//            Seven("John", 30, 1L, 1.5, true, "admin", 5)
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
//            val role: String,
//            val level: Int,
//            val score: Double
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") }
//                .map(::Eight) { (a, b, c, d, e, f, g, h) -> Params8(a, b, c, d, e, f, g, h) },
//            Eight("John", 30, 1L, 1.5, true, "admin", 5, 9.5)
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") }
//                .map(::Nine) { (a, b, c, d, e, f, g, h, i) -> Params9(a, b, c, d, e, f, g, h, i) },
//            Nine("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") }
//                .map(::Ten) { (a, b, c, d, e, f, g, h, i, j) -> Params10(a, b, c, d, e, f, g, h, i, j) },
//            Ten("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0)
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") }
//                .map(::Eleven) { (a, b, c, d, e, f, g, h, i, j, k) -> Params11(a, b, c, d, e, f, g, h, i, j, k) },
//            Eleven("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8)
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") }
//                .map(::Twelve) { (a, b, c, d, e, f, g, h, i, j, k, l) -> Params12(a, b, c, d, e, f, g, h, i, j, k, l) },
//            Twelve("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") }
//                .map(::Thirteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m) -> Params13(a, b, c, d, e, f, g, h, i, j, k, l, m) },
//            Thirteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") }
//                .map(::Fourteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n) -> Params14(a, b, c, d, e, f, g, h, i, j, k, l, m, n) },
//            Fourteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") }
//                .map(::Fifteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) -> Params15(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) },
//            Fifteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") }
//                .map(::Sixteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) -> Params16(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) },
//            Sixteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") }
//                .map(::Seventeen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) -> Params17(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) },
//            Seventeen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1)
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int,
//            val status: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") / string("status") }
//                .map(::Eighteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) -> Params18(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) },
//            Eighteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1, "Active")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int,
//            val status: String,
//            val deadline: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") / string("status") / string("deadline") }
//                .map(::Nineteen) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) -> Params19(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) },
//            Nineteen("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1, "Active", "2023-12-31")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int,
//            val status: String,
//            val deadline: String,
//            val budget: Double
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") / string("status") / string("deadline") / double("budget") }
//                .map(::Twenty) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) -> Params20(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) },
//            Twenty("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1, "Active", "2023-12-31", 50000.0)
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int,
//            val status: String,
//            val deadline: String,
//            val budget: Double,
//            val risk: String
//        )
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") / string("status") / string("deadline") / double("budget") / string("risk") }
//                .map(::TwentyOne) { (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) -> Params21(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) },
//            TwentyOne("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1, "Active", "2023-12-31", 50000.0, "Low")
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
//            val role: String,
//            val level: Int,
//            val score: Double,
//            val department: String,
//            val salary: Double,
//            val yearsOfExperience: Int,
//            val manager: String,
//            val location: String,
//            val team: String,
//            val project: String,
//            val client: String,
//            val priority: Int,
//            val status: String,
//            val deadline: String,
//            val budget: Double,
//            val risk: String,
//            val category: String
//        )
//
//        fun paramsToTwentyTwo(params: Params22<String, Int, Long, Double, Boolean, String, Int, Double, String, Double, Int, String, String, String, String, String, Int, String, String, Double, String, String>): TwentyTwo {
//            return TwentyTwo(
//                params.first,
//                params.second,
//                params.third,
//                params.fourth,
//                params.fifth,
//                params.sixth,
//                params.seventh,
//                params.eighth,
//                params.ninth,
//                params.tenth,
//                params.eleventh,
//                params.twelfth,
//                params.thirteenth,
//                params.fourteenth,
//                params.fifteenth,
//                params.sixteenth,
//                params.seventeenth,
//                params.eighteenth,
//                params.nineteenth,
//                params.twentieth,
//                params.twentyFirst,
//                params.twentySecond
//            )
//        }
//
//        fun twentyTwoToParams(twentyTwo: TwentyTwo): Params22<String, Int, Long, Double, Boolean, String, Int, Double, String, Double, Int, String, String, String, String, String, Int, String, String, Double, String, String> {
//            return Params22(
//                twentyTwo.name,
//                twentyTwo.age,
//                twentyTwo.userId,
//                twentyTwo.price,
//                twentyTwo.active,
//                twentyTwo.role,
//                twentyTwo.level,
//                twentyTwo.score,
//                twentyTwo.department,
//                twentyTwo.salary,
//                twentyTwo.yearsOfExperience,
//                twentyTwo.manager,
//                twentyTwo.location,
//                twentyTwo.team,
//                twentyTwo.project,
//                twentyTwo.client,
//                twentyTwo.priority,
//                twentyTwo.status,
//                twentyTwo.deadline,
//                twentyTwo.budget,
//                twentyTwo.risk,
//                twentyTwo.category
//            )
//        }
//
//        testInput(
//            Path { "root" / string("name") / int("age") / long("userId") / double("price") / bool("active") / string("role") / int("level") / double("score") / string("department") / double("salary") / int("yearsOfExperience") / string("manager") / string("location") / string("team") / string("project") / string("client") / int("priority") / string("status") / string("deadline") / double("budget") / string("risk") / string("category") }
//                .map(::paramsToTwentyTwo, ::twentyTwoToParams),
//            TwentyTwo("John", 30, 1L, 1.5, true, "admin", 5, 9.5, "Engineering", 100000.0, 8, "Jane", "New York", "Alpha", "Project X", "Acme Inc", 1, "Active", "2023-12-31", 50000.0, "Low", "Internal")
//        )
//    }
//}
