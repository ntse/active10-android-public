package com.flipsidegroup.active10.utils

import android.animation.Animator


abstract class ConfettiAnimatorListener : Animator.AnimatorListener {

    override fun onAnimationCancel(animation: Animator) {}

    override fun onAnimationRepeat(animation: Animator) {}

    override fun onAnimationStart(animation: Animator) {}
}