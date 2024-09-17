package com.ronaldo.pace.repository.pets

import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.domain.pets.PetFilters
import com.ronaldo.pace.services.database.pet.models.PetEntity
import com.ronaldo.pace.services.database.pet.models.PetEntityType
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class PetFilterTest {

    val test = PetFilter::filter

    @Test
    fun `should return any pets when filter is empty`() {
        val filter = PetFilters.empty
        val testCases = listOf(
            PetEntity("1", "", "", ageInMillis(1.days), 10, PetEntityType.Dog, .1f),
            PetEntity("2", "", "", ageInMillis(10.days), 100, PetEntityType.Cat, .2f),
            PetEntity("3", "", "", ageInMillis(100.days), 10_00, PetEntityType.Turtle, .3f),
            PetEntity("4", "", "", ageInMillis(1000.days), 100_00, PetEntityType.Parrot, .4f),
            PetEntity("5", "", "", ageInMillis(10_000.days), 1000_00, PetEntityType.Dog, .5f),
            PetEntity("6", "", "", ageInMillis(100_000.days), 10_000_00, PetEntityType.Cat, .6f),
            PetEntity("7", "", "", ageInMillis(1.days), 234567, PetEntityType.Turtle, .7f),
        )
        testCases.forEach {
            assertThat(test(it, filter)).isTrue()
        }
    }

    @Test
    fun `should return cats`() {
        val filter = PetFilters(type = Pet.Type.Cat)
        val testCases = listOf(
            PetEntity("1", "", "", ageInMillis(1.days), 10, PetEntityType.Dog, .1f),
            PetEntity("3", "", "", ageInMillis(100.days), 10_00, PetEntityType.Turtle, .3f),
            PetEntity("4", "", "", ageInMillis(1000.days), 100_00, PetEntityType.Parrot, .4f),
            PetEntity("5", "", "", ageInMillis(10_000.days), 1000_00, PetEntityType.Dog, .5f),
            PetEntity("7", "", "", ageInMillis(1.days), 234567, PetEntityType.Turtle, .7f),
        )
        testCases.forEach {
            assertThat(test(it, filter)).isFalse()
        }
        val testCases2 = listOf(
            PetEntity("2", "", "", ageInMillis(10.days), 100, PetEntityType.Cat, .2f),
            PetEntity("6", "", "", ageInMillis(100_000.days), 10_000_00, PetEntityType.Cat, .6f),
        )
        testCases2.forEach {
            assertThat(test(it, filter)).isTrue()
        }
    }

    @Test
    fun `should return young pets`() {
        val filter = PetFilters(maxAge = 100.days)
        val testCases = listOf(
            PetEntity("4", "", "", ageInMillis(107.days), 100_00, PetEntityType.Parrot, .4f),
            PetEntity("1", "", "", ageInMillis(100.days + 1.seconds), 10, PetEntityType.Dog, .1f),
        )
        testCases.forEach {
            assertThat(test(it, filter)).isFalse()
        }
        // due to millisecond comparison, using exactly 100.days produces a flaky test
        val age99days = ageInMillis(100.days - 1.seconds)
        val testCases2 = listOf(
            PetEntity("2", "", "", ageInMillis(2.days), 100, PetEntityType.Cat, .2f),
            PetEntity("3", "", "", age99days, 10_00, PetEntityType.Turtle, .3f),
        )
        testCases2.forEach {
            assertThat(test(it, filter)).isTrue()
        }
    }

    @Test
    fun `should return cheap pets`() {
        val filter = PetFilters(maxPrice = 59_99)
        val testCases = listOf(
            PetEntity("2", "", "", ageInMillis(10.days), 60_00, PetEntityType.Cat, .2f),
            PetEntity("3", "", "", ageInMillis(100.days), 78_99, PetEntityType.Turtle, .3f),

            )
        testCases.forEach {
            assertThat(test(it, filter)).isFalse()
        }
        val testCases2 = listOf(
            PetEntity("1", "", "", ageInMillis(1.days), 29_99, PetEntityType.Dog, .1f),
            PetEntity("4", "", "", ageInMillis(1000.days), 59_99, PetEntityType.Parrot, .4f),

            )
        testCases2.forEach {
            assertThat(test(it, filter)).isTrue()
        }
    }
}