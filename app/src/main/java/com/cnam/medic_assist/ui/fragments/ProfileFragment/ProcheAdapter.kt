package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Proche

/**
 * Permet l'affiche des infos des proches dans la liste
 */
class ProcheAdapter(
    private val proches: MutableList<Proche>,
    private val onItemClick: (Proche) -> Unit
) : RecyclerView.Adapter<ProcheAdapter.ProcheViewHolder>() {

    class ProcheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val procheName: TextView = itemView.findViewById(R.id.proche_name)
        val prochePhone: TextView = itemView.findViewById(R.id.proche_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcheViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proche, parent, false)
        return ProcheViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProcheViewHolder, position: Int) {
        val proche = proches[position]
        holder.procheName.text = "${proche.nom} ${proche.prenom}"
        holder.prochePhone.text = proche.numero_tel

        holder.itemView.setOnClickListener {
            onItemClick(proche) // Appelle une fonction pour gérer le clic
        }
    }

    override fun getItemCount(): Int = proches.size
}
