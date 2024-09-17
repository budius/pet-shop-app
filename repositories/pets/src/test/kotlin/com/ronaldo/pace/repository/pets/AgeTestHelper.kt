package com.ronaldo.pace.repository.pets

import kotlinx.datetime.Clock
import kotlin.time.Duration

fun age(duration: Duration) = Clock.System.now() - duration
fun ageInMillis(duration: Duration) = age(duration).toEpochMilliseconds()