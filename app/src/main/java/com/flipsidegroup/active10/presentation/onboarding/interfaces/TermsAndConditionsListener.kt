package com.flipsidegroup.active10.presentation.onboarding.interfaces


interface TermsAndConditionsListener {

    fun areTermsAndConditionsAccepted(): Boolean

    fun showTermsAndConditionError()

    fun scrollDown()

    fun getTermsVersion(): Int
}