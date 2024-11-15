package com.cnam.medic_assist.ui.fragments.NavFragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.datas.Constants
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RDVFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var rdvList: List<RendezVous> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rdv_page, container, false)

        // Récupérer la ListView
        listView = view.findViewById(R.id.list_view)

        // Initialiser l'adaptateur avec une liste mutable vide
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf() // Liste mutable
        )
        listView.adapter = adapter

        // Gérer les clics pour afficher les détails du rendez-vous
        listView.setOnItemClickListener { _, _, position, _ ->
            val rdv = rdvList[position]
            showRdvDetailsDialog(rdv)
        }

        // Setup SearchView
        val searchView: SearchView = view.findViewById(R.id.searchView)
        setupSearchView(searchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Charger les données lors de l'affichage du fragment
        if (rdvList.isEmpty()) {
            fetchData()
        } else {
            reloadData()
        }
    }

    private fun fetchData() {
        RetrofitClient.instance.getRendezvousByUserId(1).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    // Charger les données de l'API
                    rdvList = response.body()!!
                    reloadData()

                    Toast.makeText(requireContext(), "Nombre de rendez-vous : ${rdvList.size}", Toast.LENGTH_SHORT).show()
                } else {
                    // Si aucune donnée n'est retournée, charger les données constantes
                    loadDefaultRdvData()
                }
            }

            override fun onFailure(call: Call<List<RendezVous>>, t: Throwable) {
                // En cas d'échec de la requête, charger les données constantes
                Toast.makeText(requireContext(), "Erreur : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                loadDefaultRdvData()
            }
        })
    }

    private fun reloadData() {
        val rdvStrings = rdvList.map { "${it.intitule} - ${formatageDate(it.daterdv)}" }

        adapter.clear()
        adapter.addAll(rdvStrings)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::adapter.isInitialized) {
                    adapter.filter.filter(newText)
                }
                return false
            }
        })
    }

    private fun showRdvDetailsDialog(rdv: RendezVous) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_rdv_details)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvIntitule = dialog.findViewById<TextView>(R.id.dialog_intitule)
        val tvDate = dialog.findViewById<TextView>(R.id.dialog_date)
        val tvHeure = dialog.findViewById<TextView>(R.id.dialog_heure)
        val tvAdresse = dialog.findViewById<TextView>(R.id.dialog_adresse)
        val closeButton = dialog.findViewById<ImageView>(R.id.close_button)

        tvIntitule.text = rdv.intitule
        tvDate.text = "Date : "+formatageDate(rdv.daterdv)
        tvHeure.text = "Horaire : "+formatageTime(rdv.horaire)
        tvAdresse.text = "${rdv.nom} \n${rdv.numero_rue} ${rdv.rue}\n${rdv.codepostal} ${rdv.ville}"

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun formatageDate(date: String): String {
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Format simple sans heure
        )
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (format in inputFormats) {
            try {
                val parsedDate = format.parse(date)
                return outputDateFormat.format(parsedDate)
            } catch (e: Exception) {
                // Ignorer l'erreur et passer au format suivant
            }
        }

        return date // Retourner la date brute si aucun format ne correspond
    }

    private fun formatageTime(time: String): String {
        val horaireFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputTimeFormat.format(horaireFormat.parse(time))
    }

    private fun loadDefaultRdvData() {
        rdvList = Constants.rdvList
        reloadData()
        Toast.makeText(requireContext(), "Données par défaut chargées", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(): RDVFragment {
            return RDVFragment()
        }
    }
}
