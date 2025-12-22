package com.flipsidegroup.active10.data.persistance.migration

import android.content.Context
import com.flipsidegroup.active10.data.MigrationData
import com.flipsidegroup.active10.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets


class MigrationRepositoryImpl(
    private val context: Context, private val gson: Gson
) : MigrationRepository {

    override fun deleteMigrationFile() {
        context.deleteFile(Constants.MIGRATION_FILE_NAME)
    }

    override fun getMigrationData(): Observable<MigrationData> {
        return Observable.fromCallable(this::readMigrationData).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun readMigrationData(): MigrationData {
        var migrationData = MigrationData()

        val fileInputStream: FileInputStream

        try {
            fileInputStream = context.openFileInput(Constants.MIGRATION_FILE_NAME)

            val inputStreamReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8)
            val stringBuilder = StringBuilder()

            inputStreamReader.forEachLine { line -> stringBuilder.append(line) }
            inputStreamReader.close()

            val migrationDataJson = stringBuilder.toString()
            migrationData =
                gson.fromJson(migrationDataJson, object : TypeToken<MigrationData>() {}.type)

        } catch (ex: Exception) {
            Timber.d(ex, "Failed to parse the logs or read from file")
        }

        return migrationData
    }
}
