// RUN_PIPELINE_TILL: FRONTEND

package my.test

import io.github.nomisrev.typedapi.Endpoint

<!CLASS_EXPECTED!>@Endpoint("/")
data class MyEndpoint(
    val name: String,
    val age: Int,
)<!>