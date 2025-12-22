package com.flipsidegroup.active10.presentation.licenses

import com.flipsidegroup.active10.data.AcknowledgementLicense
import com.flipsidegroup.active10.data.DependencyLicense
import com.flipsidegroup.active10.data.License
import com.flipsidegroup.active10.data.persistance.jsonstorage.JsonRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LicensesPresenter @Inject constructor(
    private val jsonRepository: JsonRepository
): BasePresenter<LicensesContract.View>(), LicensesContract.Presenter {

    override fun getLicenses() {
        Single.zip(
            jsonRepository.getLicenses(),
            jsonRepository.getValidationTextFile(),
            ::mergeLicensesAndValidationText
        )
        .subscribe(
            { mergedResult -> view?.showLicenses(mergedResult) },
            { error ->
                Timber.e(error, "Error while getting licenses and validation text")
                view?.showError()
            }
        ).addToDisposables()
    }

    private fun mergeLicensesAndValidationText(dependencyLicenses: List<DependencyLicense>, validationText: String): List<AcknowledgementLicense> {
        return dependencyLicenses.map { dependency ->
            val licence = dependency.spdxLicenses?.firstOrNull()
                ?: dependency.unknownLicenses?.firstOrNull { it.name != null }
                ?: getLicenseFromValidationFile(dependency, validationText)

            AcknowledgementLicense(
                name = dependency.name,
                groupWithArtifact = dependency.groupId + ":" + dependency.artifactId,
                url = licence.url,
                license = licence.name ?: ""
            )
        }
    }

    private fun getLicenseFromValidationFile(dependency: DependencyLicense, validationText: String): License {
        val id = dependency.groupId + ":" + dependency.artifactId + ":" + dependency.version
        val nameOfTheLicense = validationText.substringAfter(id).substringAfter("because ")
            .substringBefore("\n")
        return  License(
            identifier = null,
            name = nameOfTheLicense,
            url = dependency.scm?.url
        )
    }

}