package com.fruitastic.ui.history

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fruitastic.R
import com.fruitastic.adapter.HistoryAdapter
import com.fruitastic.data.ViewModelFactory
import com.fruitastic.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity())
        val viewModel: HistoryViewModel by viewModels { factory }

        val historyAdapter = HistoryAdapter()

        viewModel.getHistory().observe(viewLifecycleOwner) { historyData ->
            binding.progressIndicator.visibility = View.GONE
            if (historyData != null && historyData.isNotEmpty()) {
                historyAdapter.submitList(historyData)
                binding.rvHistory.visibility = View.VISIBLE
                binding.tvEmptyData.visibility = View.GONE
            } else {
                binding.rvHistory.visibility = View.GONE
                binding.tvEmptyData.visibility = View.VISIBLE
            }
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = historyAdapter
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val itemToDelete = historyAdapter.currentList[position]

                viewModel.deleteHistory(itemToDelete)

                val updatedList = historyAdapter.currentList.toMutableList()
                updatedList.removeAt(position)
                historyAdapter.submitList(updatedList)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                val backgroundColor = ContextCompat.getColor(recyclerView.context, R.color.red)
                val background = ColorDrawable(backgroundColor)
                background.setBounds(
                    itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom
                )
                background.draw(c)

                val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                val iconBottom = iconTop + deleteIcon.intrinsicHeight

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                deleteIcon.draw(c)

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvHistory)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}