package com.nerdachse.controlnerd.ui.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nerdachse.controlnerd.databinding.FragmentTransformBinding
import com.nerdachse.controlnerd.databinding.ItemTransformBinding
import com.nerdachse.controlnerd.obswebsocket.protocol.*
import com.nerdachse.controlnerd.ui.ActionButton


/**
 * Fragment that demonstrates a responsive layout pattern where the format of the content
 * transforms depending on the size of the screen. Specifically this Fragment shows items in
 * the [RecyclerView] using LinearLayoutManager in a small screen
 * and shows items using GridLayoutManager in a large screen.
 */
class TransformFragment : Fragment() {

    private lateinit var transformViewModel: TransformViewModel
    private var _binding: FragmentTransformBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        transformViewModel = ViewModelProvider(this).get(TransformViewModel::class.java)
        _binding = FragmentTransformBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView = binding.recyclerviewTransform
        val adapter = TransformAdapter()
        recyclerView.adapter = adapter
        transformViewModel.buttons.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class TransformAdapter :
        ListAdapter<ActionButton, TransformViewHolder>(object : DiffUtil.ItemCallback<ActionButton>() {
            override fun areItemsTheSame(oldItem: ActionButton, newItem: ActionButton): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: ActionButton, newItem: ActionButton): Boolean =
                oldItem == newItem
        }) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransformViewHolder {
            val binding = ItemTransformBinding.inflate(LayoutInflater.from(parent.context))
            return TransformViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TransformViewHolder, position: Int) {
            val button = getItem(position)
            holder.textView.text = button.name
            holder.imageView.setImageDrawable(
                ResourcesCompat.getDrawable(holder.imageView.resources, button.drawable, null)
            )
            holder.imageView.setOnClickListener { v -> button.action(v) }
        }
    }

    class TransformViewHolder(binding: ItemTransformBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.imageViewItemTransform
        val textView: TextView = binding.textViewItemTransform
    }
}
