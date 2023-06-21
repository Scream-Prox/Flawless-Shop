package com.example.fshop.databases

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClothAdapter(private val clothDao: ClothDao,
                   private val lifecycleScope: LifecycleCoroutineScope,
                   private val itemClickListener: (Bundle) -> Unit) :
    RecyclerView.Adapter<ClothAdapter.ClothViewHolder>(), Filterable {
    private var clothes: List<ClothItem> = listOf()
    private var clothesFull: List<ClothItem> = listOf() // for storing all data

    fun setCloth(cloth: List<ClothItem>) {
        this.clothes = cloth
        clothesFull = ArrayList(cloth) // initialize with the full data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cloth_item, parent, false)
        return ClothViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClothViewHolder, position: Int) {
        val cloth = clothes[position]
        holder.bind(cloth)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("id", cloth.id!!)
                putString("name", cloth.name)
                putString("price", cloth.price)
                putString("size", cloth.size)
                putString("desc", cloth.text)
                putString("imgRes", cloth.imageRes)
            }

            lifecycleScope.launch {
                val sameNameClothes = withContext(Dispatchers.IO) {
                    clothDao.getClothesByName(cloth.name)
                }
                val differentSizeClothes = sameNameClothes.filter { it.size != cloth.size }
                if (differentSizeClothes.isNotEmpty()) {
                    bundle.putString("sizeM", differentSizeClothes[0].size)
                    if (differentSizeClothes.size > 1) {
                        bundle.putString("sizeL", differentSizeClothes[1].size)
                    }
                }

                Log.d("ClothAdapter", "Item clicked: $cloth")
                itemClickListener(bundle)
            }
        }
    }

    override fun getItemCount(): Int = clothes.size

    class ClothViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cloth: ClothItem) {

            itemView.findViewById<TextView>(R.id.name).text = cloth.name
//            itemView.findViewById<TextView>(R.id.type).text = cloth.type
            itemView.findViewById<TextView>(R.id.price).text = cloth.price
//            itemView.findViewById<TextView>(R.id.size).text = cloth.size
            val imageView = itemView.findViewById<ImageView>(R.id.user_image)
            if (cloth.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    cloth.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.flogo) // Load default image if user does not have an image
            }
        }
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filteredList = ArrayList<ClothItem>()
                if (charSequence.isEmpty()) {
                    filteredList.addAll(clothesFull)
                } else {
                    val filterPattern = charSequence.toString().toLowerCase().trim { it <= ' ' }

                    for (item in clothesFull) {
                        if (item.name.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item)
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList

                return results
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                clothes = filterResults.values as List<ClothItem>
                notifyDataSetChanged()
            }
        }
    }
}

