package com.hkk

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AppTest {
    private val countries = listOf(
        Country("C1", "A1", 0.0, 0, listOf()),
        Country("C2", "A1", 0.0, 0, listOf()),
        Country("C3", "A1", 1000.0, 1000, listOf(Language("L1"))),
        Country("C4", "A1", 1000.0, 1001, listOf(Language("L1"))),
        Country("C5", "A1", 5000.0, 499, listOf(Language("L2")))
    )

    @Test
    fun testAvgCountryPopulation() {
        assertEquals(500.0, avgCountryPopulation(countries))
    }

    @Test
    fun testSqMilestoKm() {
        // Test rounded off to integer, "close enough"
        assertEquals(125020, sqMilesToSqKm(323802.0).toInt())
    }

    @Test
    fun testFindSmallestCountries() {
        val c = findSmallestCountries(countries)
        assertEquals(2, c.size)
        // assert country names, some list assertion
    }

    @Test
    fun testFindBiggestCountries() {
        val c = findBiggestCountries(countries)
        assertEquals(1, c.size)
        assertEquals("C5", c[0].name)
    }

    @Test
    fun testSortCountriesByPopulation() {
        val cp = sortCountries(countries, "population")
        assertEquals("C4", cp[0].name)
    }

    @Test
    fun testSortCountriesByArea() {
        val ca = sortCountries(countries, "area")
        assertEquals("C5", ca[0].name)
    }

    @Test
    fun testSortCountriesByDefault() {
        val cd = sortCountries(countries, "...")
        assertEquals("C1", cd[0].name)
    }

    @Test
    fun testAggregateLanguages() {
        val l = aggregateLanguages(countries)
        assertEquals(2, l.size)
        assertEquals(2, l[Language("L1")]?.first?.size)
        assertEquals(2001, l[Language("L1")]?.second)
    }
}