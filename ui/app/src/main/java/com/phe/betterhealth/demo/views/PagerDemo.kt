package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentPagerBinding
import com.phe.betterhealth.widgets.pager.BHPagerIndicator
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

class PagerDemo : Fragment(R.layout.fragment_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPagerBinding.bind(view)

        with(binding.pager1) {
            var pager1Date = LocalDate.now()
            updateIndicator(pager1Date.month, pager1Date.year)
            buttonPrev.setOnClickListener {
                pager1Date = pager1Date.minusMonths(1)
                updateIndicator(pager1Date.month, pager1Date.year)
            }
            buttonNext.setOnClickListener {
                pager1Date = pager1Date.plusMonths(1)
                updateIndicator(pager1Date.month, pager1Date.year)
            }
        }

        with(binding.pager2) {
            var pager2Data = LocalDate.now()
            updateIndicator(pager2Data)
            buttonPrev.setOnClickListener {
                pager2Data = pager2Data.minusDays(1)
                updateIndicator(pager2Data)
            }
            buttonNext.setOnClickListener {
                pager2Data = pager2Data.plusDays(1)
                updateIndicator(pager2Data)
            }
        }

        with(binding.pager3) {
            var pager3Data = 2
            updateIndicator(pager3Data)
            buttonPrev.setOnClickListener {
                pager3Data--
                updateIndicator(pager3Data)
            }
            buttonNext.setOnClickListener {
                pager3Data++
                updateIndicator(pager3Data)
            }
        }

        with(binding.pager4) {
            buttonPrev.setOnClickListener {
                showToast("Button prev action is disabled")
            }
            buttonNext.setOnClickListener {
                showToast("Button next action is disabled")
            }
        }
    }

    private fun BHPagerIndicator.updateIndicator(month: Month, year: Int) {
        pagerTitle = buildString {
            append(month.name.capitalise())
            append(", ")
            append(year)
        }
        pagerButtonPrevText = month.minus(1).name.capitalise()
        pagerButtonNextText = month.plus(1).name.capitalise()
    }

    private fun BHPagerIndicator.updateIndicator(date: LocalDate) {
        pagerTitle = date.dayOfWeek.name.capitalise()
        pagerDescription = date.format(DateTimeFormatter.ofPattern("EE, d LLLL yyyy"))
        pagerButtonPrevText = date.minusDays(1).dayOfWeek.name.capitalise()
        pagerButtonPrevContentDescription = "some random content description"
        pagerButtonNextText = date.plusDays(1).dayOfWeek.name.capitalise()
        pagerButtonNextContentDescription = "some random content description"
    }

    private fun BHPagerIndicator.updateIndicator(page: Int) {
        pagerDescription = "Page $page of 3"
        pagerButtonPrevText = if (page == 1) null else "Go to page ${(page - 1)}"
        pagerButtonNextText = if (page == 3) null else "Go to page ${(page + 1)}"
    }

    private fun String.capitalise() = lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }
}
