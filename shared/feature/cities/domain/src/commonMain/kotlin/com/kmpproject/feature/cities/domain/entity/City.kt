package com.kmpproject.feature.cities.domain.entity

data class City(
  val id: Long,
  val name: String,
  val country: String,
  val population: Long,
) {
  val title: String
    get() = "$name, $country"
}
