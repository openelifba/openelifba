package com.wordiam.openelifba.infra.repository

import com.wordiam.openelifba.domain.category.Category
import com.wordiam.openelifba.domain.category.CategoryId
import com.wordiam.openelifba.domain.port.CategoryFetcher
import com.wordiam.openelifba.jooq.generated.tables.Category.CATEGORY
import com.wordiam.openelifba.jooq.generated.tables.records.CategoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqCategoryRepository(
    private val jooq: DSLContext,
) : CategoryFetcher {
    override fun fetchCategories(): List<Category> =
        jooq
            .selectFrom(CATEGORY)
            .orderBy(CATEGORY.RANK.asc())
            .fetchInto(CategoryRecord::class.java)
            .map {
                it.toDomain()
            }

    private fun CategoryRecord.toDomain(): Category =
        Category(
            id = CategoryId(this.id),
            name = this.name,
            statistic = null,
        )
}
