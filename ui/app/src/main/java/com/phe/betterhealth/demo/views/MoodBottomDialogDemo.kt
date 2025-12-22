package com.phe.betterhealth.demo.views

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialog
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialogType
import com.phe.betterhealth.components.moodbottomdialog.BHRelatedArticle
import com.phe.betterhealth.demo.R
import com.phe.betterhealth.demo.databinding.FragmentMoodBottomDemoBinding

class MoodBottomDialogDemo : Fragment(R.layout.fragment_mood_bottom_demo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentMoodBottomDemoBinding.bind(view)
        binding.showButtonGreat.setOnClickListener {
            val bottomDialog = BHMoodBottomDialog().apply {
                onButtonClickListener = {
                    Toast.makeText(
                        requireContext(),
                        "Button clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                titleText = "That’s great!"
                subtitleText =
                    "MasSubtitlesa placerat duis ultricies lacus"
                descriptionText =
                    "Ullamcorper a lacus vestibulum sed arcu non odio euismod. Vitae semper quis lectus nulla at. Arcu cursus vitae congue mauris rhoncus aenean vel elit."
                buttonText = "Close"
                dialogType = BHMoodBottomDialogType.GREAT
            }
            bottomDialog.showDialog(childFragmentManager)
        }

        binding.showButtonGood.setOnClickListener {
            val bottomDialog = BHMoodBottomDialog().apply {
                onButtonClickListener = {
                    Toast.makeText(
                        requireContext(),
                        "Button clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                titleText = "That’s ok!"
                subtitleText =
                    "MasSubtitlesa placerat duis ultricies lacus"
                descriptionText =
                    "Ullamcorper a lacus vestibulum sed arcu non odio euismod. Vitae semper quis lectus nulla at. Arcu cursus vitae congue mauris rhoncus aenean vel elit."
                buttonText = "Close"
                dialogType = BHMoodBottomDialogType.GOOD
            }
            bottomDialog.showDialog(childFragmentManager)
        }

        binding.showButtonOk.setOnClickListener {
            val bottomDialog = BHMoodBottomDialog().apply {
                onButtonClickListener = {
                    Toast.makeText(
                        requireContext(),
                        "Button clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                titleText = "Everyone has their off days"
                subtitleText =
                    "MasSubtitlesa placerat duis ultricies lacus"
                descriptionText =
                    "Ullamcorper a lacus vestibulum sed arcu non odio euismod. Vitae semper quis lectus nulla at. Arcu cursus vitae congue mauris rhoncus aenean vel elit."
                buttonText = "Close"
                dialogType = BHMoodBottomDialogType.OK
            }
            bottomDialog.showDialog(childFragmentManager)
        }

        binding.showButtonTired.setOnClickListener {
            val bottomDialog = BHMoodBottomDialog().apply {
                onButtonClickListener = {
                    Toast.makeText(
                        requireContext(),
                        "Button clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                onArticleClickListener = {
                    Toast.makeText(
                        requireContext(),
                        "Article clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                article = BHRelatedArticle(
                    categoryLabel = "Advice and Support",
                    title = "Get more advice and support for mental health and urgent support",
                    imageUrl = "https://c25k-ecs-s3bucket-1gojludaiw0at.s3.amazonaws.com/eu-west-2/images/148d7d58992e4990a3993a7ac051c536.jpg",
                    isLink = false
                )
                titleText = "Feeling low won’t last, it will get better"
                subtitleText =
                    "MasSubtitlesa placerat duis ultricies lacus"
                descriptionText =
                    "Ullamcorper a lacus vestibulum sed arcu non odio euismod. Vitae semper quis lectus nulla at. Arcu cursus vitae congue mauris rhoncus aenean vel elit."
                buttonText = "Close"
                dialogType = BHMoodBottomDialogType.TIRED
            }
            bottomDialog.showDialog(childFragmentManager)
        }
    }
}
