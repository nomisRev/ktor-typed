package com.example

import com.example.codec.Codec
import io.ktor.http.CookieEncoding
import io.ktor.server.routing.RoutingContext

sealed interface Parameter<A> {
    class Path<A>(val name: String, val deserialize: Codec<A>)

    class Query<A>(val name: String, val optional: Boolean, val deserialize: Codec<A>) : Parameter<A>

    // TODO support optional
    class Header(val name: String, val optional: Boolean) : Parameter<List<String>>

    // TODO support optional
    class Cookie(
        val name: String,
        val optional: Boolean,
        val encoding: CookieEncoding = CookieEncoding.URI_ENCODING,
    ) : Parameter<String>
}


sealed interface Params {
    fun toList(): List<Any?>
}

data object Params0 : Params {
    override fun toList(): List<Any?> = emptyList()
}

data class Params1<out A>(val value: A) : Params {
    override fun toList(): List<Any?> = listOf(value)
}

data class Params2<out A, out B>(val first: A, val second: B) : Params {
    override fun toList(): List<Any?> = listOf(first, second)
}

data class Params3<out A, out B, out C>(val first: A, val second: B, val third: C) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third)
}

data class Params4<out A, out B, out C, out D>(val first: A, val second: B, val third: C, val fourth: D) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth)
}

data class Params5<out A, out B, out C, out D, out E>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E
) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth, fifth)
}

data class Params6<out A, out B, out C, out D, out E, out F>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F
) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth, fifth, sixth)
}

data class Params7<out A, out B, out C, out D, out E, out F, out G>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F, val seventh: G
) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth, fifth, sixth, seventh)
}

data class Params8<out A, out B, out C, out D, out E, out F, out G, out H>(
    val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F, val seventh: G, val eighth: H
) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth, fifth, sixth, seventh, eighth)
}

data class Params9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I
) : Params {
    override fun toList(): List<Any?> = listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth)
}

data class Params10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J
) : Params {
    override fun toList(): List<Any?> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth)
}

data class Params11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K
) : Params {
    override fun toList(): List<Any?> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh)
}

data class Params12<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L
) : Params {
    override fun toList(): List<Any?> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth)
}

data class Params13<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M
) : Params {
    override fun toList(): List<Any?> =
        listOf(first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twelfth, thirteenth)
}

data class Params14<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth
    )
}

data class Params15<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth
    )
}

data class Params16<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth
    )
}

data class Params17<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth
    )
}

data class Params18<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth,
        eighteenth
    )
}

data class Params19<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth,
        eighteenth,
        nineteenth
    )
}

data class Params20<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S,
    val twentieth: T
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth,
        eighteenth,
        nineteenth,
        twentieth
    )
}

data class Params21<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S,
    val twentieth: T,
    val twentyFirst: U
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth,
        eighteenth,
        nineteenth,
        twentieth,
        twentyFirst
    )
}

data class Params22<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
    val seventh: G,
    val eighth: H,
    val ninth: I,
    val tenth: J,
    val eleventh: K,
    val twelfth: L,
    val thirteenth: M,
    val fourteenth: N,
    val fifteenth: O,
    val sixteenth: P,
    val seventeenth: Q,
    val eighteenth: R,
    val nineteenth: S,
    val twentieth: T,
    val twentyFirst: U,
    val twentySecond: V
) : Params {
    override fun toList(): List<Any?> = listOf(
        first,
        second,
        third,
        fourth,
        fifth,
        sixth,
        seventh,
        eighth,
        ninth,
        tenth,
        eleventh,
        twelfth,
        thirteenth,
        fourteenth,
        fifteenth,
        sixteenth,
        seventeenth,
        eighteenth,
        nineteenth,
        twentieth,
        twentyFirst,
        twentySecond
    )
}

context(routing: RoutingContext) internal suspend fun List<*>.execute(
    block: suspend RoutingContext.(Any?) -> Unit, transform: (suspend (Any?) -> Any?)?
): Any {
    val execute: suspend (Any?) -> Unit = { block.invoke(routing, transform?.invoke(it) ?: it) }
    return when (size) {
        0 -> execute(Params0)
        1 -> execute(this[0])
        2 -> execute(Params2(this[0], this[1]))
        3 -> execute(Params3(this[0], this[1], this[2]))
        4 -> execute(Params4(this[0], this[1], this[2], this[3]))
        5 -> execute(Params5(this[0], this[1], this[2], this[3], this[4]))
        6 -> execute(Params6(this[0], this[1], this[2], this[3], this[4], this[5]))
        7 -> execute(Params7(this[0], this[1], this[2], this[3], this[4], this[5], this[6]))
        8 -> execute(Params8(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7]))
        9 -> execute(Params9(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7], this[8]))
        10 -> execute(
            Params10(
                this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7], this[8], this[9]
            )
        )

        11 -> execute(
            Params11(
                this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7], this[8], this[9], this[10]
            )
        )

        12 -> execute(
            Params12(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11]
            )
        )

        13 -> execute(
            Params13(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12]
            )
        )

        14 -> execute(
            Params14(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13]
            )
        )

        15 -> execute(
            Params15(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14]
            )
        )

        16 -> execute(
            Params16(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15]
            )
        )

        17 -> execute(
            Params17(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16]
            )
        )

        18 -> execute(
            Params18(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16],
                this[17]
            )
        )

        19 -> execute(
            Params19(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16],
                this[17],
                this[18]
            )
        )

        20 -> execute(
            Params20(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16],
                this[17],
                this[18],
                this[19]
            )
        )

        21 -> execute(
            Params21(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16],
                this[17],
                this[18],
                this[19],
                this[20]
            )
        )

        22 -> execute(
            Params22(
                this[0],
                this[1],
                this[2],
                this[3],
                this[4],
                this[5],
                this[6],
                this[7],
                this[8],
                this[9],
                this[10],
                this[11],
                this[12],
                this[13],
                this[14],
                this[15],
                this[16],
                this[17],
                this[18],
                this[19],
                this[20],
                this[21]
            )
        )

        else -> throw IllegalArgumentException("Cannot convert list of size $size to params!")
    }
}
