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

class OrdersAdapter(private val deleteCallback: (OrderItem) -> Unit, private val navController: NavController) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var orders: List<OrderItem> = listOf()

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    fun setOrder(order: List<OrderItem>) {
        this.orders = order
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (orders.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun isOrderEmpty(): Boolean {
        return orders.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = inflater.inflate(R.layout.orders_model_empty, parent, false)
                EmptyViewHolder(view, navController)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.orders_model, parent, false)
                OrderViewHolder(view, deleteCallback)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OrderViewHolder) {
            holder.bind(orders[position])
        }
        // Для EmptyViewHolder ничего делать не нужно
    }

    override fun getItemCount(): Int {
        return if (orders.isEmpty()) 1 else orders.size
    }

    class EmptyViewHolder(itemView: View, private val navController: NavController)  : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.toHome).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    class OrderViewHolder(itemView: View, private val deleteCallback: (OrderItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(order: OrderItem) {


            itemView.findViewById<TextView>(R.id.name).text = order.name
//            itemView.findViewById<TextView>(R.id.type).text = cloth.type
            itemView.findViewById<TextView>(R.id.price).text = order.price
            itemView.findViewById<TextView>(R.id.size).text = order.size
            val imageView = itemView.findViewById<ImageView>(R.id.prodImg)
            if (order.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    order.imageRes,
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