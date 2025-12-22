package com.flipsidegroup.active10.data.persistance.badwords

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader


private const val BAD_WORDS_JSON_FILE_NAME = "swearlist.json"

class BadWordsAssetRepository(private val context: Context, private val gson: Gson) :
    BadWordsRepository {

    override fun loadBadWords(): Observable<List<String>> {
        var badWords: List<String> = ArrayList()
        try {
            val inputStream = context.assets.open(BAD_WORDS_JSON_FILE_NAME)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            inputStream.close()

            val badWordsJson = stringBuilder.toString()
            badWords = gson.fromJson<ArrayList<String>>(
                badWordsJson,
                object : TypeToken<ArrayList<String>>() {}.type
            )
        } catch (e: Exception) {
            Timber.e(e)
        }

        return Observable.just(badWords)
            .subscribeOn(Schedulers.io())
    }
}