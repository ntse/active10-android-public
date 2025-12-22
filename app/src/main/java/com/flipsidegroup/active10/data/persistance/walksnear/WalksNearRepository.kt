package com.flipsidegroup.active10.data.persistance.walksnear

import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.flipsidegroup.active10.data.models.requests.CircularWalksRequest
import com.flipsidegroup.active10.data.models.requests.CuratedWalkByIdRequest
import com.flipsidegroup.active10.data.models.requests.CuratedWalksRequest
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.data.models.response.CuratedWalksResponse
import com.flipsidegroup.active10.data.network.GoJauntlyApi
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalksNearRepository @Inject constructor(
    private val goJauntlyApi: GoJauntlyApi,
    private val geocoder: Geocoder?,
    private val settingsUtils: SettingsUtils,
    ) {

    private var circularWalksCache: Pair<Instant, List<CircularWalkResponse>>? = null
    private var curatedWalksCache: Pair<Instant, CuratedWalksResponse>? = null
    private var postcodeCoordinatesCache: Pair<Instant, Address?>? = null

    fun getCircularWalks(postcode: String): Single<List<CircularWalkResponse>> {
        if (circularWalksCache != null
            && circularWalksCache!!.first.isAfter(Instant.now().minusSeconds(CACHE_DURATION))
        ) return Single.just(circularWalksCache!!.second)

        val token = settingsUtils.getSettingsHolder().nhsToken
        return goJauntlyApi
            .getCircularWalks(CircularWalksRequest(postcode = postcode), "Bearer $token")
            .subscribeOn(Schedulers.io())
            .doOnSuccess { circularWalksCache = Instant.now() to it }
    }

    fun getCuratedWalks(postcode: String): Single<CuratedWalksResponse> {
        if (curatedWalksCache != null
            && curatedWalksCache!!.first.isAfter(Instant.now().minusSeconds(CACHE_DURATION))
        ) return Single.just(curatedWalksCache!!.second)

        val token = settingsUtils.getSettingsHolder().nhsToken
        return goJauntlyApi
            .getCuratedWalks(CuratedWalksRequest(postcode = postcode), "Bearer $token")
            .subscribeOn(Schedulers.io())
            .doOnSuccess { curatedWalksCache = Instant.now() to it }
    }

    fun getWalk(id: String) : Single<CuratedWalk> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        return goJauntlyApi.getCuratedWalkById(id, CuratedWalkByIdRequest(), "Bearer $token")
            .subscribeOn(Schedulers.io())
    }

    fun getPostcodeCoordinates(postcode : String) : Single<Address>? {
        if (geocoder == null) return null

        if (postcodeCoordinatesCache != null
            && postcodeCoordinatesCache!!.first.isAfter(Instant.now().minusSeconds(CACHE_DURATION))
        ) return Single.just(postcodeCoordinatesCache!!.second)

        return Single.create { emitter ->
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(postcode, 1) { addresses ->
                        addresses.firstOrNull()?.let { address ->
                            emitter.onSuccess(address)
                        }
                    }
                } else {
                    val addresses = geocoder.getFromLocationName(postcode, 1)
                    addresses?.firstOrNull()?.let { address ->
                        emitter.onSuccess(address)
                    }
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { postcodeCoordinatesCache = Instant.now() to it }
    }

    companion object {
        private const val CACHE_DURATION = 600L
    }
}
