package com.eddiez.plantirrigsys.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.base.BaseFragment
import com.eddiez.plantirrigsys.base.SortType
import com.eddiez.plantirrigsys.dataModel.ScheduleDataModel
import com.eddiez.plantirrigsys.databinding.FragmentListScheduleBinding
import com.eddiez.plantirrigsys.utilities.AppConstants
import com.eddiez.plantirrigsys.view.activity.ViewScheduleActivity
import com.eddiez.plantirrigsys.view.adapter.ScheduleExploreItemAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [ListScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListScheduleFragment : BaseFragment() {
    private lateinit var binding: FragmentListScheduleBinding
    private var listSchedule = listOf<ScheduleDataModel>()
    private var sortType = SortType.DATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListScheduleBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSchedule.layoutManager = LinearLayoutManager(context)

        observeData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Logic to refresh the RecyclerView goes here
            refreshData()
        }

        binding.swipeRefreshLayout.isRefreshing = true

        setupSearchView()

        setupSortButton()
    }

    private fun setupSortButton() {
        binding.imgSort.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.sort_by_name -> {
                        sortType = SortType.NAME
                    }
                    R.id.sort_by_date -> {
                        sortType = SortType.DATE
                    }
                    R.id.sort_by_view -> {
                        sortType = SortType.VIEW
                    }
                    R.id.sort_by_copy -> {
                        sortType = SortType.COPY
                    }
                }

                val query = binding.searchView.query.toString().trim().lowercase()
                val listScheduleTmp = searchAndSortSchedule(listSchedule, query, sortType)
                setupRecyclerView(listScheduleTmp)

                true
            }

            popup.show()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim()?.lowercase()
                val listScheduleTmp = query?.let { searchAndSortSchedule(listSchedule, it, sortType) }!!
                setupRecyclerView(listScheduleTmp)
                return true
            }
        })
    }



    private fun searchAndSortSchedule(listSchedule: List<ScheduleDataModel>, query: String, sortType: SortType): List<ScheduleDataModel> {
        if (query.isEmpty()) {
            return sortSchedule(listSchedule, sortType)
        }

        val filteredList = listSchedule.filter {
            it.title?.lowercase()?.contains(query.lowercase().trim()) == true
        }

        return sortSchedule(filteredList, sortType)
    }

    private fun sortSchedule(listSchedule: List<ScheduleDataModel>, sortType: SortType): List<ScheduleDataModel> {
        return when (sortType) {
            SortType.NAME -> listSchedule.sortedBy { it.title?.lowercase() }
            SortType.DATE -> listSchedule.sortedByDescending { it.updateContentAt }
            SortType.VIEW -> listSchedule.sortedByDescending { it.numberOfViews }
            SortType.COPY -> listSchedule.sortedByDescending { it.numberOfCopies }
        }
    }

    private fun observeData() {
        scheduleViewModel.currentSchedule.observe(viewLifecycleOwner) {
            if (it != null) {
                val intent = Intent(requireContext(), ViewScheduleActivity::class.java)
                intent.putExtra(AppConstants.SCHEDULE, it)
                startActivity(intent)
            }
        }

        userViewModel.accessToken.observe(viewLifecycleOwner) { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getPublicSchedule(token)
            }
        }

        scheduleViewModel.publicSchedules.observe(viewLifecycleOwner) {
            if (it != null) {
                listSchedule = it
                // check search query
                val query = binding.searchView.query.toString().trim().lowercase()
                val listScheduleTmp = searchAndSortSchedule(listSchedule, query, sortType)
                // check current list
                setupRecyclerView(listScheduleTmp)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun refreshData() {
        // Refresh data in your RecyclerView
        userViewModel.accessToken.value?.let { token ->
            if (token.isNotEmpty()) {
                scheduleViewModel.getPublicSchedule(token)
            }
        }
    }

    private fun setupRecyclerView(items: List<ScheduleDataModel>) {

        if (items.isEmpty()) {
            binding.bgEmpty.visibility = View.VISIBLE
            binding.rvSchedule.visibility = View.INVISIBLE
        } else {
            binding.bgEmpty.visibility = View.GONE
            binding.rvSchedule.visibility = View.VISIBLE

            // When data is loaded, call this to hide the refresh indicator
            binding.rvSchedule.adapter = ScheduleExploreItemAdapter(items, scheduleViewModel)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ListScheduleFragment()
    }
}