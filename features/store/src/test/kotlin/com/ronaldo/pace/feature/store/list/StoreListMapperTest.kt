package com.ronaldo.pace.feature.store.list

import com.google.common.truth.Truth.assertThat
import com.ronaldo.pace.domain.pets.Pet
import com.ronaldo.pace.feature.common.UiVisibility
import com.ronaldo.pace.store.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

class StoreListMapperTest {

    private var data = listOf<Pet>()
    private var visible = UiVisibility.Content
    private val pet = Pet(
        "id",
        "Name",
        "Description",
        365.daysOld,
        price = 2959,
        type = Pet.Type.Turtle
    )

    @Before
    fun before() {
        visible = UiVisibility.Content
        data = listOf(pet)
    }

    @Test
    fun `should map empty data to full screen loading`() {
        // given
        visible = UiVisibility.Loading
        data = emptyList()

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Loading)

    }

    @Test
    fun `should map empty data to full screen error`() {
        // given
        visible = UiVisibility.Error
        data = emptyList()

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Error)

    }

    @Test
    fun `should map with data to loading more loading`() {
        // given
        visible = UiVisibility.Loading

        // when
        val result = testInputs()

        // then
        assertThat(result.loadMoreVisibility).isEqualTo(UiVisibility.Loading)

    }

    @Test
    fun `should map with data to load more error`() {
        // given
        visible = UiVisibility.Error
        data = emptyList()

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Error)

    }

    @Test
    fun `should map dog`() {
        // given
        data = listOf(
            Pet(
                "id",
                "Name",
                "Description",
                365.daysOld,
                price = 2959,
                type = Pet.Type.Dog
            )
        )

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.loadMoreVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.items).isEqualTo(
            listOf(
                Item(
                    id = "id",
                    line1 = "Name",
                    line2 = "age, description",
                    price = "€29,59",
                    icon = R.drawable.ic_dog,
                    iconDescription = "background image of a dog"
                )
            )
        )
    }

    @Test
    fun `should map cat`() {
        // given
        data = listOf(
            Pet(
                "id",
                "Name",
                "", // empty description
                365.daysOld,
                price = 123,
                type = Pet.Type.Cat
            )
        )

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.loadMoreVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.items).isEqualTo(
            listOf(
                Item(
                    id = "id",
                    line1 = "Name",
                    line2 = "age",
                    price = "€1,23",
                    icon = R.drawable.ic_cat,
                    iconDescription = "background image of a cat"
                )
            )
        )
    }

    @Test
    fun `should map parrot`() {
        // given
        data = listOf(
            Pet(
                "id",
                "Name",
                "Description",
                365.daysOld,
                price = 34,
                type = Pet.Type.Parrot
            )
        )

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.loadMoreVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.items).isEqualTo(
            listOf(
                Item(
                    id = "id",
                    line1 = "Name",
                    line2 = "age, description",
                    price = "€0,34",
                    icon = R.drawable.ic_parrot,
                    iconDescription = "background image of a parrot"
                )
            )
        )
    }


    @Test
    fun `should map turtle`() {
        // given
        data = listOf(
            Pet(
                "id",
                "Name",
                "Description",
                365.daysOld,
                price = 7,
                type = Pet.Type.Turtle
            )
        )

        // when
        val result = testInputs()

        // then
        assertThat(result.fullScreenVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.loadMoreVisibility).isEqualTo(UiVisibility.Content)
        assertThat(result.items).isEqualTo(
            listOf(
                Item(
                    id = "id",
                    line1 = "Name",
                    line2 = "age, description",
                    price = "€0,07",
                    icon = R.drawable.ic_turtle,
                    iconDescription = "background image of a turtle"
                )
            )
        )
    }

    private fun testInputs() = StoreListMapper { "age" }.map(data, visible)
}

private val Int.daysOld: Instant
    get() = Clock.System.now().minus(this.days)