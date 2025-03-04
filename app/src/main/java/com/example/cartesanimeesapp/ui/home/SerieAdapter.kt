package com.example.cartesanimeesapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cartesanimeesapp.R
import com.example.cartesanimeesapp.models.Serie
import com.bumptech.glide.Glide

class SerieAdapter(
    private val seriesList: List<Serie>,
    private val onSerieClick: (Serie) -> Unit
) : RecyclerView.Adapter<SerieAdapter.SerieViewHolder>() {

    class SerieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageSerie)
        val textView: TextView = view.findViewById(R.id.textSerieName)
        val buttonOpen: Button = view.findViewById(R.id.buttonOpen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_serie, parent, false)
        return SerieViewHolder(view)
    }

    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        val serie = seriesList[position]

        // Utilisation de Glide pour charger l'image depuis une URL
        Glide.with(holder.itemView.context)
            .load(serie.imageUrl)  // Assurez-vous que `imageUrl` est bien une URL valide
            .placeholder(R.drawable.placeholder) // Image par défaut pendant le chargement
            .error(R.drawable.error_image) // Image en cas d'erreur
            .into(holder.imageView)

        holder.textView.text = serie.name
        holder.buttonOpen.setOnClickListener { onSerieClick(serie) }
    }

    override fun getItemCount(): Int = seriesList.size
}

