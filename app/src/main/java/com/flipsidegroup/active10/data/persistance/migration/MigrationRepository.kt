package com.flipsidegroup.active10.data.persistance.migration

import com.flipsidegroup.active10.data.MigrationData
import io.reactivex.Observable


interface MigrationRepository {

    fun deleteMigrationFile()

    fun getMigrationData(): Observable<MigrationData>
}