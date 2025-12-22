package com.flipside.briskcounter

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType

const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1

internal object PermissionHelper {

    fun hasOAuthPermission(activity: Activity): Boolean {
        val fitnessOptions = getFitnessSignInOptions()
        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions)
    }

    fun requestOAuthPermission(activity: Activity) {
        val fitnessOptions = getFitnessSignInOptions()
        GoogleSignIn.requestPermissions(
            activity,
            GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
            null,
            fitnessOptions
        )
    }

    private fun getFitnessSignInOptions(): FitnessOptions {
        return FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .build()
    }
}
