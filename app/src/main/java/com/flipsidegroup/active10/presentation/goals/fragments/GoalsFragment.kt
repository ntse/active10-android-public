package com.flipsidegroup.active10.presentation.goals.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.PermissionEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.databinding.FragmentGoalsBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsFragmentPresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsFragmentView
import com.flipsidegroup.active10.presentation.onboarding.adapters.GoalsAdapter
import com.flipsidegroup.active10.presentation.onboarding.interfaces.GoalListener
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import javax.inject.Inject

private const val IN_PERMISSION_POSITION_KEY = "in_permission_position_key"
private const val ONBOARDING = "onboarding"

class GoalsFragment : BaseFragment<GoalsFragmentView>(), GoalsFragmentView {

    companion object {
        fun newInstance(position: Int, isOnboarding: Boolean = false): GoalsFragment {
            val instance = GoalsFragment()
            instance.arguments = Bundle().apply {
                putInt(IN_PERMISSION_POSITION_KEY, position)
                putBoolean(ONBOARDING, isOnboarding)
            }
            return instance
        }
    }

    @Inject
    internal lateinit var presenter: GoalsFragmentPresenter

    private var binding: FragmentGoalsBinding by lifecycleAwareVariable()

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    private var goalAdapter: GoalsAdapter? = null
    private var goalListener: GoalListener? = null
    private var permissionEnum: PermissionEnum? = null

    override fun getPresenter(): LifecycleAwarePresenter<GoalsFragmentView>? = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is GoalListener) {
            goalListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_goals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGoalsBinding.bind(view)

        val permissionPos = arguments?.getInt(IN_PERMISSION_POSITION_KEY)
        val isOnboarding = arguments?.getBoolean(ONBOARDING) ?: false
        permissionPos ?: return

        permissionEnum = PermissionEnum.values()[permissionPos]

        setupViews()
        presenter.getGoals()
    }

    private fun setupViews() {
        binding.goalsRV.layoutManager = LinearLayoutManager(context)

        goalAdapter = GoalsAdapter(
            emptyList(), emptyList()
        ) { newGoalsList ->
            goalListener?.onGoalChangeListener(newGoalsList)
        }
        binding.goalsRV.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )
        binding.goalsRV.adapter = goalAdapter

        binding.titleTV.setHeading()
    }

    fun updateCurrentGoals(newGoal: String) = addGoalToList(newGoal)

    override fun onResume() {
        super.onResume()
        binding.titleTV.announceHeader()
    }

    override fun onGoalsReceived(goals: List<Goal>) {
        goalAdapter?.updateGoals(goals)
    }

    private fun addGoalToList(goalDescription: String) {
        val newGoalsList = goalAdapter?.goalList?.toMutableList()
        val goalAlreadyExists =
            newGoalsList?.map { it.goal }?.any { it == goalDescription } ?: false
        if (!goalAlreadyExists) {
            newGoalsList?.add(
                Goal(
                    goalId = -1,
                    goal = goalDescription,
                    isCustomGoal = true,
                    isSelected = true
                )
            )
            goalAdapter?.updateGoals(newGoalsList?.toList() ?: listOf())
            goalListener?.onGoalChangeListener(newGoalsList?.toList() ?: listOf())
        }
    }
}