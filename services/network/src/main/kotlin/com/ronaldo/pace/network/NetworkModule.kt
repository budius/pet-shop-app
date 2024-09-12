package com.ronaldo.pace.network

import com.ronaldo.pace.network.rest.GetPets
import org.koin.dsl.module

fun networkModule() = module {
    val httpClient = ClientHttpFactory.create()
    single { GetPets.build(httpClient) }
}
