package com.kmpproject.core.ui

fun formatPopulation(population: Long): String {
  return population
    .toString()
    .reversed()
    .chunked(DIGIT_GROUP_SIZE)
    .joinToString(DIGIT_GROUP_SEPARATOR)
    .reversed()
}

private const val DIGIT_GROUP_SIZE = 3
private const val DIGIT_GROUP_SEPARATOR = " "
