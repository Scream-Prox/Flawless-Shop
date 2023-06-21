package com.example.fshop.databases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R

class FavAdapter(private val deleteCallback: (FavItem) -> Unit, private val navController: NavController) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var favourites: List<FavItem> = listOf()

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    fun setFav(fav: List<FavItem>) {
        this.favourites = fav
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (favourites.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun isFavEmpty(): Boolean {
        return favourites.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = inflater.inflate(R.layout.fav_model_empty, parent, false)
                EmptyViewHolder(view, navController)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.fav_model, parent, false)
                FavViewHolder(view, deleteCallback)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FavViewHolder) {
            holder.bind(favourites[position])
        }
        // Для EmptyViewHolder ничего делать не нужно
    }

    override fun getItemCount(): Int {
        return if (favourites.isEmpty()) 1 else favourites.size
    }

    class EmptyViewHolder(itemView: View, private val navController: NavController)  : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.toHome).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    class FavViewHolder(itemView: View, private val deleteCallback: (FavItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(fav: FavItem) {


            itemView.findViewById<TextView>(R.id.name).text = fav.name
//            itemView.findViewById<TextView>(R.id.type).text = cloth.type
            itemView.findViewById<TextView>(R.id.price).text = fav.price
            itemView.findViewById<TextView>(R.id.size).text = fav.size
            val imageView = itemView.findViewById<ImageView>(R.id.prodImg)
            if (fav.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    fav.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.flogo)
            }
        }
    }
}