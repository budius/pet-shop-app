package com.ronaldo.pace.feature.common

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.time.Duration.Companion.days

class RelativeAgeMapperTest {

    private fun test(ageInDays: Int, expected: String) {
        val input = Clock.System.now().minus(ageInDays.days)
        val output = RelativeAgeMapper.map(input)
        assertThat(output).isEqualTo(expected)
    }


    @Test
    fun `should map older than 1 year`() {
        test(472, "1.2 years old")
    }

    @Test
    fun `should map older than 10 years`() {
        test(3797, "10.3 years old")
    }

    @Test
    fun `should map 2 years`() {
        test(731, "2 years old")
    }

    @Test
    fun `should map 10 years`() {
        test(3655, "10 years old")
    }

    @Test
    fun `should map in months`() {
        test(121, "3 months young")
    }

    @Test
    fun `should map in weeks`() {
        test(17, "2 weeks young")
    }

    @Test
    fun `should map in days`() {
        test(12, "12 days young")
    }
}