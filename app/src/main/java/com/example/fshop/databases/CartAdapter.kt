package com.example.fshop.databases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.fshop.R
import com.example.fshop.viewmodels.CartViewModel

class CartAdapter(private val viewModel: CartViewModel, private val navController: NavController) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var carts: List<CartItem> = listOf()

    companion object {
        private const val TYPE_EMPTY = 0
        private const val TYPE_ITEM = 1
    }

    fun setCart(cart: List<CartItem>) {
        this.carts = cart
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (carts.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun isCartEmpty(): Boolean {
        return carts.isEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_EMPTY -> {
                val view = inflater.inflate(R.layout.cart_model_empty, parent, false)
                EmptyViewHolder(view, navController)
            }
            TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.cart_model, parent, false)
                CartViewHolder(view, viewModel)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CartViewHolder) {
            holder.bind(carts[position])
        }
    }

    override fun getItemCount(): Int {
        return if (carts.isEmpty()) 1 else carts.size
    }

    class EmptyViewHolder(itemView: View, private val navController: NavController)  : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.findViewById<Button>(R.id.toHome).setOnClickListener {
                navController.navigate(R.id.homeFragment)
            }
        }
    }

    class CartViewHolder(itemView: View, private val viewModel: CartViewModel) : RecyclerView.ViewHolder(itemView) {
        fun bind(cart: CartItem) {

            itemView.findViewById<CardView>(R.id.btnDelete).setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(it.context)
                builder.setMessage("Вы уверены, что хотите удалить товар?")
                builder.setCancelable(true)
                builder.setPositiveButton("Да") { dialog, _ ->
                    cart.id?.let { viewModel.deleteCartItem(it) }
                    dialog.dismiss()
                }
                builder.setNegativeButton("Нет") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }

            itemView.findViewById<TextView>(R.id.name).text = cart.name
            itemView.findViewById<TextView>(R.id.price).text = cart.price
            itemView.findViewById<TextView>(R.id.size).text = cart.size
            val imageView = itemView.findViewById<ImageView>(R.id.prodImg)
            if (cart.imageRes != null) {
                val imageResId = itemView.context.resources.getIdentifier(
                    cart.imageRes,
                    "drawable",
                    itemView.context.packageName
                )
                imageView.setImageResource(imageResId)
            } else {
                imageView.setImageResource(R.drawable.flogo) // Загрузите изображение по умолчанию, если у пользователя нет изображения
            }
        }
    }
}