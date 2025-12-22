package com.flipside.briskcounter.internal

interface InitListener {

    fun onSuccess(state: State)
    fun onFailure(error: Error)
}
