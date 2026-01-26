package com.wordiam.openelifba.infra.repository.config

import com.github.database.rider.core.api.configuration.DBUnit
import com.github.database.rider.core.api.dataset.DataSet
import com.github.database.rider.spring.api.DBRider

@DataSet
@DBRider
@DBUnit(
    caseSensitiveTableNames = true,
    dataTypeFactoryClass = org.dbunit.ext.postgresql.PostgresqlDataTypeFactory::class,
    alwaysCleanBefore = true,
    alwaysCleanAfter = true,
    cacheConnection = false,
)
annotation class WithDataSet
