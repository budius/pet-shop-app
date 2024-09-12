package com.ronaldo.pace.network.mock

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.delay
import kotlin.random.Random

private var requestCount = 0

internal val mockEngine = MockEngine { request ->
    requestCount++
    val isError = requestCount % 4 == 0 // simulate random errors

    delay(1200 + Random.nextLong(800)) // simulate network

    val path = request.url.encodedPath
    val response = responses[path]

    val (content, status) = if (isError) {
        ByteReadChannel("""{"code":"InternalServerError", "message":"Server error, try again"}""") to HttpStatusCode.InternalServerError
    } else if (path == GET_PET_PATH) {
        val index = request.url.parameters["index"]
            ?: throw IllegalArgumentException("Index is a mandatory field")
        val i = index.toInt()
        val data = jsonList.subList(i, i + 11)
        ByteReadChannel(
            GET_PET_RESPONSE
                .replace("INDEX", index)
                .replace("COUNT", jsonList.size.toString())
                .replace("DATA", data.joinToString(", "))
        ) to HttpStatusCode.OK
    } else if (response == null) {
        ByteReadChannel("""{"code":"NotFound", "message":"Path $path not found"}""") to HttpStatusCode.NotFound
    } else {
        ByteReadChannel(response) to HttpStatusCode.OK
    }
    respond(
        content = content,
        status = status,
        headers = headersOf(HttpHeaders.ContentType, "application/json")
    )
}

private const val GET_PET_PATH = "/pets/"
private const val GET_PET_RESPONSE = """{"index":INDEX, "count":COUNT, "items":[DATA]}"""

val responses = mapOf(
    "pets/" to """{"index":INDEX, "count":COUNT, "items":[DATA]}"""
)