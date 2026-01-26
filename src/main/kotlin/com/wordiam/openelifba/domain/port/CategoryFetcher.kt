package com.wordiam.openelifba.domain.port

import com.wordiam.openelifba.domain.category.Category

interface CategoryFetcher {
    fun fetchCategories(): List<Category>
}
