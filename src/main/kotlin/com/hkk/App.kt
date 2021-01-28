package com.hkk

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject

class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Fuel.get("https://restcountries.eu/rest/v2/all").responseObject<List<Country>> { _, _, result ->
                result.fold(
                    success = { countries ->
                        val sortedCountries = sortCountries(countries, args.elementAtOrNull(0))
                        printCountryList(sortedCountries)
                        val avgPopulation = avgCountryPopulation(countries)
                        val smallestCountries = findSmallestCountries(countries)
                        val biggestCountries = findBiggestCountries(countries)
                        printSummary(avgPopulation, smallestCountries, biggestCountries)
                        val languages = aggregateLanguages(countries)
                        printLanguages(languages)
                    },
                    failure = { err -> println(err) })
            }.get()
        }
    }
}

fun avgCountryPopulation(countries: List<Country>): Double {
    return countries.map { it.population }.average()
}

fun findSmallestCountries(countries: List<Country>): List<Country> {
    val smallestCountrySize = countries.map { it.area }.minOrNull()
    return countries.filter { it.area == smallestCountrySize }
}

fun findBiggestCountries(countries: List<Country>): List<Country> {
    val biggestCountrySize = countries.map { it.area }.maxOrNull()
    return countries.filter { it.area == biggestCountrySize }
}

fun sortCountries(countries: List<Country>, sort: String?): List<Country> {
    return when (sort) {
        "population" -> countries.sortedByDescending { it.population }
        "area" -> countries.sortedByDescending { it.area }
        else -> countries.sortedBy { it.name }
    }
}

fun aggregateLanguages(countries: List<Country>): Map<Language, Pair<List<String>, Long>> {
    return countries.fold(HashMap()) { agg, currentCountry ->
        currentCountry.languages.forEach { l ->
            agg[l]?.also {
                agg[l] = Pair(it.first + currentCountry.name, it.second + currentCountry.population)
            } ?: agg.put(l, Pair(listOf(currentCountry.name), currentCountry.population))
        }
        agg
    }
}

fun sqMilesToSqKm(areaSqMiles: Double): Double {
    val sqConversion = 2.58998811
    return areaSqMiles / sqConversion
}

fun printCountryList(countries: List<Country>) {
    val headingTemplate = "%-55s\t%-10s\t%10s\t%10s"
    val formatTemplate = "%-55s\t%-10s\t%10.0f\t%10.1f"
    println(headingTemplate.format("Name", "Region", "Area", "Population"))
    countries.forEach { c ->
        println(
            formatTemplate.format(
                c.name,
                c.region,
                sqMilesToSqKm(c.area),
                c.population / 1000000.0
            )
        )
    }
}

fun printSummary(avgPopulation: Double, smallestCountries: List<Country>, biggestCountries: List<Country>) {
    println("\n\nSummary:")
    println("\nAvg country population: %.1f million".format(avgPopulation / 1000000.0))
    println("\nCountries with smallest area:")
    smallestCountries.forEach { c -> println(c.name) }
    println("\nCountries with biggest area:")
    biggestCountries.forEach { c -> println(c.name) }
    print("\n\n\n\n")
}

fun printLanguages(languageAggregate: Map<Language, Pair<List<String>, Long>>) {
    val langHeadingTemplate = "%-55s\t%-100s\t%10s"
    val langFormatTemplate = "%-55s\t%-100s\t%10.1f"
    println("Languages and where they are spoken:\n")
    println(langHeadingTemplate.format("Language", "Countries", "Population"))
    languageAggregate.forEach{ l ->
        println(langFormatTemplate.format(l.key.name, l.value.first.joinToString(", "), l.value.second / 1000000.0))
    }
}

data class Language(val name: String)

data class Country(
    val name: String,
    val region: String,
    val area: Double,
    val population: Long,
    val languages: List<Language>
)