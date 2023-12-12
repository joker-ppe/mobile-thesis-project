package com.eddiez.plantirrigsys.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.databinding.FragmentHomeBinding
import com.eddiez.plantirrigsys.viewmodel.UserViewModel

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fullName.observe(viewLifecycleOwner) { fullName ->
            // Use fullName here
            binding.tvName.text = fullName
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()
    }
}