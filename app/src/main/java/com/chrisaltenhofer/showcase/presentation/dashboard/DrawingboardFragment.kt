package com.chrisaltenhofer.showcase.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chrisaltenhofer.showcase.R
import com.chrisaltenhofer.showcase.databinding.FragmentDrawingboardBinding
import com.google.android.material.appbar.MaterialToolbar

class DrawingboardFragment : Fragment() {

    // Views
    private lateinit var topAppBar : MaterialToolbar
    private lateinit var textView : TextView

    private lateinit var dashboardViewModel: DrawingboardViewModel
    private var _binding: FragmentDrawingboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel = ViewModelProvider(this).get(DrawingboardViewModel::class.java)

        _binding = FragmentDrawingboardBinding.inflate(inflater, container, false)

        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
        setListeners()
        updateObservers()
    }

    private fun setViews() {
        topAppBar = binding.topAppBar
        textView = binding.textDashboard
    }

    private fun setListeners() {
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_reset -> {
                    dashboardViewModel.resetClicked()
                    true
                }
                R.id.navigation_send -> {
                    dashboardViewModel.sendClicked()
                    true
                }
                else -> false
            }
        }
    }

    private fun updateObservers() {
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}