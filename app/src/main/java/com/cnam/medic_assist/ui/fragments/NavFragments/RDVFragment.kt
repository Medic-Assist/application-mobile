package com.cnam.medic_assist.ui.fragments.NavFragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.utils.CalendarHelper
import com.cnam.medic_assist.utils.ICalendarHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RDVFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var rdvList: List<RendezVous> = listOf()
    private lateinit var calendarHelper: ICalendarHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarHelper = CalendarHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rdv_page, container, false)

        // Initialiser la ListView et son adaptateur
        listView = view.findViewById(R.id.list_view)
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        listView.adapter = adapter

        // Gérer les clics pour afficher les détails du rendez-vous
        listView.setOnItemClickListener { _, _, position, _ ->
            val rdv = rdvList[position]
            showRdvDetailsDialog(rdv)
        }

        // Configurer la SearchView
        val searchView: SearchView = view.findViewById(R.id.searchView)
        setupSearchView(searchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Charger les données au démarrage
        if (rdvList.isEmpty()) {
            fetchData()
        } else {
            reloadData()
        }
    }

    private fun fetchData() {
        RetrofitClient.instance.getRendezvousByUserId(1).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        rdvList = response.body()!!
                        reloadData()
                        Toast.makeText(requireContext(), "Nombre de rendez-vous : ${rdvList.size}", Toast.LENGTH_SHORT).show()
                    } else {
                        loadDefaultRdvData()
                    }
                }
            }

            override fun onFailure(call: Call<List<RendezVous>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                }
                loadDefaultRdvData()
            }
        })
    }

    private fun reloadData() {
        if (!isAdded) return // Ne pas mettre à jour si le fragment n'est pas attaché
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
        val tvTitreAdresse = dialog.findViewById<TextView>(R.id.titre_adresse)
        val tvAdresse = dialog.findViewById<TextView>(R.id.dialog_adresse)
        val closeButton = dialog.findViewById<ImageView>(R.id.close_button)
        val addToCalendarButton = dialog.findViewById<Button>(R.id.add_to_calendar_button)

        tvIntitule.text = rdv.intitule
        tvDate.text = "Date : ${formatageDate(rdv.daterdv)}"
        tvHeure.text = "Horaire : ${formatageTime(rdv.horaire)}"
        tvAdresse.text = "${rdv.nom} \n${rdv.numero_rue} ${rdv.rue}\n${rdv.codepostal} ${rdv.ville}"

        if(tvAdresse.text == ""){
            tvTitreAdresse.text = "";
        }else{
            tvTitreAdresse.text = "Centre Médical :";
        }



        closeButton.setOnClickListener { dialog.dismiss() }

        addToCalendarButton.setOnClickListener {
            calendarHelper.addEventToCalendar(rdv)
            Toast.makeText(requireContext(), "Rendez-vous ajouté au calendrier.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
    private fun formatageDate(date: String): String {
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (format in inputFormats) {
            try {
                val parsedDate = format.parse(date)
                return outputDateFormat.format(parsedDate)
            } catch (e: Exception) {
                Log.w("RDVFragment", "Erreur de formatage de la date : $date")
            }
        }

        return "Date invalide"
    }


    private fun formatageTime(time: String): String {
        return try {
            val horaireFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputTimeFormat.format(horaireFormat.parse(time))
        } catch (e: Exception) {
            Log.w("RDVFragment", "Erreur de formatage de l'heure : $time")
            "Heure invalide"
        }
    }

    private fun loadDefaultRdvData() {
        if (Constants.rdvList.isNotEmpty()) {
            rdvList = Constants.rdvList
            reloadData()
            if (isAdded) {
                Toast.makeText(requireContext(), "Données par défaut chargées", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Aucune donnée par défaut disponible.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(): RDVFragment {
            return RDVFragment()
        }
    }
}
