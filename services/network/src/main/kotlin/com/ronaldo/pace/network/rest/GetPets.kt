package com.ronaldo.pace.network.rest

import com.ronaldo.pace.network.models.PetsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

// TODO: use kebab-krafter to auto-generate a rest client
fun interface GetPets {

    suspend operator fun invoke(index: Int): Result<PetsResponse>

    companion object Factory {
        fun build(client: HttpClient): GetPets {
            return GetPets { index ->
                runCatching { client.get("pets/?index=$index").body() }
            }
        }
    }
}

