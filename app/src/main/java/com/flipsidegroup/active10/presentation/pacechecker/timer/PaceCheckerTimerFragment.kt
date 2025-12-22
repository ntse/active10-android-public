package com.flipsidegroup.active10.presentation.pacechecker.timer

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.activity.addCallback
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.databinding.FragmentPaceCheckerTimerBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.pacechecker.PaceCheckerActivity
import com.flipsidegroup.active10.presentation.pacechecker.exit.PaceCheckerExitDialog
import com.flipsidegroup.active10.presentation.pacechecker.pause.PaceCheckerPauseDialog
import com.flipsidegroup.active10.presentation.pacechecker.result.PaceCheckerResultFragment
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import javax.inject.Inject

class PaceCheckerTimerFragment : BaseFragment<PaceCheckerTimerView>(), PaceCheckerTimerView {

    @Inject
    lateinit var presenter: PaceCheckerTimerPresenter

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private var _binding: FragmentPaceCheckerTimerBinding? = null
    private val binding get() = _binding!!

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null

    private var mediaPlayer: MediaPlayer? = null

    private val sensorStepListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onSensorChanged(event: SensorEvent?) {
            presenter.recordSteps(event?.values?.firstOrNull())
        }
    }

    companion object {
        fun newInstance() = PaceCheckerTimerFragment()
    }

    override fun getPresenter(): LifecycleAwarePresenter<PaceCheckerTimerView> = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = requireContext().getSystemService()
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaceCheckerTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(
            sensorStepListener,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(sensorStepListener)
        presenter.onPauseView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setAccessibilityHeading(binding.paceCheckerTitle, true)

        binding.paceCheckerButtonContinue.setOnClickListenerWithDebounce { presenter.onStartClicked() }
        binding.paceCheckerClose.setOnClickListenerWithDebounce {
            requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            presenter.onBackPressed()
        }

        presenter.loadData()
    }

    override fun showInitialCountdown(sec: Int) {
        with(binding) {
            paceCheckerDummyPulse.setImageResource(R.drawable.ic_pace_checker_timer_pulse_border)
            paceCheckerOverlay.isVisible = true
            paceCheckerCountdown.isVisible = true
            paceCheckerButtonContinue.text = "Close"
            paceCheckerCountdown.text = "$sec"
            paceCheckerCountdown.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showMainCountdown(sec: Int) {
        with(binding) {
            paceCheckerDummyPulse.setImageResource(R.drawable.ic_pace_checker_timer_pulse)
            paceCheckerOverlay.isVisible = false
            paceCheckerCountdown.isVisible = false

            paceCheckerTimer.contentDescription = "$sec seconds"
            ViewCompat.setStateDescription(paceCheckerTimer, "$sec seconds")
            paceCheckerTimer.text = "00:$sec sec"

            pulseAnimation.isInvisible = !preferenceRepository.isAnimationEnabled
            paceCheckerDummyPulse.isVisible = !preferenceRepository.isAnimationEnabled
            if (!pulseAnimation.isAnimating && preferenceRepository.isAnimationEnabled) {
                pulseAnimation.playAnimation()
            }
        }
    }

    override fun showExitDialog(screenContent: ScreenContent?, seconds: Int) {
        PaceCheckerExitDialog.newInstance(screenContent, seconds).show(childFragmentManager, null)
    }

    override fun showPauseDialog(screenContent: ScreenContent?) {
        PaceCheckerPauseDialog.newInstance(screenContent).show(childFragmentManager, null)
    }

    override fun popView() {
        parentFragmentManager.popBackStack()
    }

    override fun navigateToHome() {
        requireActivity().finish()
    }

    override fun navigateToResult(steps: Float) {
        binding.pulseAnimation.pauseAnimation()
        binding.pulseAnimation.frame = 0

        binding.pulseAnimation.isInvisible = true
        binding.paceCheckerDummyPulse.isVisible = true

        (requireActivity() as PaceCheckerActivity).navigateToFragments(
            PaceCheckerResultFragment.newInstance(steps)
        )
    }

    override fun playBellSound() {
        val manager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0)
        if (manager.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            return
        }

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bell)
        }

        mediaPlayer?.start()
    }

    override fun showCloseButton(show: Boolean) {
        binding.paceCheckerClose.isInvisible = !show
    }

    override fun pauseAnimation() {
        binding.pulseAnimation.pauseAnimation()
    }

    fun resumeTimer() {
        presenter.resumeMainCountdown()
    }

    override fun hapticTik() {
        val buzzer = requireContext().getSystemService<Vibrator>()
        val pattern = longArrayOf(0, 200, 100)
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                @Suppress("DEPRECATION")
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
