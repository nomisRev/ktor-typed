package io.github.nomisrev.typedapi

interface EndpointFactory<A> {
    fun <B> create(block: (path: String, (EndpointAPI) -> A) -> B): B
}

class Test(api: EndpointAPI) {
    companion object : EndpointFactory<Test> {
        override fun <B> create(block: (path: String, (EndpointAPI) -> Test) -> B): B =
            block.invoke("path-from-annotation", { api -> Test(api) })
    }
}