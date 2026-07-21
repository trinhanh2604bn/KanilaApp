package com.example.frontend.feature.arcore

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.frontend.R
import com.example.frontend.feature.ar.data.ArShade

class ArColorAdapter(
    private val onColorSelected: (ArShade) -> Unit
) : RecyclerView.Adapter<ArColorAdapter.ColorViewHolder>() {

    private var shades: List<ArShade> = emptyList()
    private var selectedPosition = -1

    fun submitList(newShades: List<ArShade>) {
        shades = newShades
        notifyDataSetChanged()
    }

    fun setSelectedShade(shade: ArShade) {
        val position = shades.indexOfFirst { it.variantId == shade.variantId }
        if (position != -1 && position != selectedPosition) {
            val oldPos = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ar_color, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val shade = shades[position]
        holder.bind(shade, position == selectedPosition)
        holder.itemView.setOnClickListener {
            onColorSelected(shade)
        }
    }

    override fun getItemCount(): Int = shades.size

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewColor: View = itemView.findViewById(R.id.viewColor)
        private val viewSelectionRing: View = itemView.findViewById(R.id.viewSelectionRing)

        fun bind(shade: ArShade, isSelected: Boolean) {
            val colorInt = try {
                Color.parseColor(shade.shadeHex ?: "#000000")
            } catch (e: Exception) {
                Color.BLACK
            }
            
            // Tint the inner circle
            viewColor.backgroundTintList = ColorStateList.valueOf(colorInt)
            
            // Show/hide selection ring
            viewSelectionRing.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }
}
