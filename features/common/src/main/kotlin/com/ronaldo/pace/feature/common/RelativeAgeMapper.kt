package com.ronaldo.pace.feature.common

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil

object RelativeAgeMapper {
    fun map(dateOfBirth: Instant): String {
        val period = dateOfBirth.periodUntil(Clock.System.now(), TimeZone.UTC)
        val years = period.years
        val months = period.months
        val monthsPercent = (months.toFloat() / 1.2f).toString().first()
        val days = period.days

        return if (years > 0) {
            buildString {
                append(years)
                if (monthsPercent != '0') {
                    append(".${monthsPercent.toString().first()}")
                }
                append(" years old")
            }
        } else if (months > 0) {
            "$months months young"
        } else if (days > 14) { // at least 2 weeks
            "${days / 7} weeks young"
        } else {
            "$days days young"
        }
    }
}