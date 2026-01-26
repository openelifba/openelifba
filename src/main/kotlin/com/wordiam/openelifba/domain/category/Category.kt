package com.wordiam.openelifba.domain.category

data class Category(
    val id: CategoryId,
    val name: String,
    val statistic: CategoryStatistic?,
)
