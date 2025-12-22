package com.flipsidegroup.active10.data.persistance.badwords

import io.reactivex.Observable


interface BadWordsRepository {

    fun loadBadWords(): Observable<List<String>>
}