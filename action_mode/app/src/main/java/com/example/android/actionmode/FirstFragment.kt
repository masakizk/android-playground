package com.example.android.actionmode

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.android.actionmode.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_action_mode, menu)
            return true
        }

        // 更新時の処理
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.done -> {
                    mode?.finish()
                    true
                }
                R.id.close -> {
                    mode?.finish()
                    true
                }
                else -> true
            }
        }

        // 破棄後の処理
        override fun onDestroyActionMode(mode: ActionMode?) {

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false).apply {
            activityPrimaryActionMode.setOnClickListener { showActivityPrimaryActionMode() }
            activityFloatingActionMode.setOnClickListener { showActivityFloatingActionMode() }
            viewPrimaryActionMode.setOnClickListener { showViewPrimaryActionMode(viewPrimaryActionMode) }
            viewFloatingActionMode.setOnClickListener { showViewFloatingActionMode(viewFloatingActionMode) }
        }
        return binding.root
    }

    private fun showActivityPrimaryActionMode() {
        requireActivity().startActionMode(actionModeCallback, ActionMode.TYPE_PRIMARY)
    }

    private fun showActivityFloatingActionMode() {
        requireActivity().startActionMode(actionModeCallback, ActionMode.TYPE_FLOATING)
    }

    private fun showViewPrimaryActionMode(view: View) {
        view.startActionMode(actionModeCallback, ActionMode.TYPE_PRIMARY)
    }

    private fun showViewFloatingActionMode(view: View) {
        view.startActionMode(actionModeCallback, ActionMode.TYPE_FLOATING)
    }
}