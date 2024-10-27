package com.cnam.medic_assist.screens

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
import com.cnam.medic_assist.data.model.RendezVous
import com.cnam.medic_assist.data.network.RetrofitClient
import java.text.SimpleDateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException

// Format pour la date et l'heure
val dateFormat = SimpleDateFormat("dd/MM/yyyy")
val timeFormat = SimpleDateFormat("HH:mm:ss")

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

        // Appeler l'API pour récupérer la liste des rendez-vous
        RetrofitClient.instance.getRendezvousByUserId(1).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (response.isSuccessful && response.body() != null) {
                    rdvList = response.body()!!

        // Créer une liste de chaînes avec l'intitulé et la date
        val rdvStrings = rdvList.map { "${it.intitule} - ${dateFormat.format(it.daterdv)}" }

                    // Initialiser l'ArrayAdapter avec les données reçues
                    adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        rdvStrings
                    )

                    // Assigner l'adaptateur à la ListView
                    listView.adapter = adapter

                    // Afficher un Toast indiquant le nombre de rendez-vous récupérés
                    Toast.makeText(requireContext(), "Nombre de rendez-vous : ${rdvList.size}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Aucun rendez-vous trouvé", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RendezVous>>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

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

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    // Méthode pour afficher le Dialog avec les détails du RDV
    private fun showRdvDetailsDialog(rdv: RendezVous) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_rdv_details)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvIntitule = dialog.findViewById<TextView>(R.id.dialog_intitule)
        val tvDate = dialog.findViewById<TextView>(R.id.dialog_date)
        val tvHeure = dialog.findViewById<TextView>(R.id.dialog_heure)
        val closeButton = dialog.findViewById<ImageView>(R.id.close_button)

        tvIntitule.text = rdv.intitule
        tvDate.text = dateFormat.format(rdv.daterdv)
        tvHeure.text =  timeFormat.format(rdv.horaire)
        //tvAdresse.text = rdv. -> mettre adresse utilisateur

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
    companion object {
        fun newInstance(): RDVFragment {
            return RDVFragment()
        }
    }
}
