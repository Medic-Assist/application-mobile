package com.cnam.medic_assist.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import java.text.SimpleDateFormat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.TimeZone

// Format pour la date et l'heure
val dateFormat = SimpleDateFormat("dd/MM/yyyy")
val timeFormat = SimpleDateFormat("HH:mm:ss")
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.models.RendezVous
import com.cnam.medic_assist.utils.ICalendarHelper
import com.cnam.medic_assist.utils.CalendarHelper
import java.text.SimpleDateFormat

// Format pour la date et l'heure
val dateFormat = SimpleDateFormat("dd/MM/yyyy")
val timeFormat = SimpleDateFormat("HH:mm")

class RDVFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var rdvList: List<RendezVous> = listOf()

    private lateinit var calendarHelper: ICalendarHelper
    private lateinit var rdvList: List<RendezVous>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rdv_page, container, false)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        // Récupérer la ListView
        listView = view.findViewById(R.id.list_view)

        // Appeler l'API pour récupérer la liste des rendez-vous
        RetrofitClient.instance.getRendezvousByUserId(1).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (response.isSuccessful && response.body() != null) {
                    rdvList = response.body()!!

        // Créer une liste de chaînes avec l'intitulé et la date

        val rdvStrings = rdvList.map { "${it.intitule} - ${formatageDate(it.daterdv)}" }

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


        // Récupérer CalendarHelper
        calendarHelper = CalendarHelper(requireContext())

        // Récupérer la ListView
        listView = view.findViewById(R.id.list_view)

        // Récupérer la liste des rendez-vous
        rdvList = Constants.rdvList

        // Créer une liste de chaînes avec l'intitulé et la date
        val rdvStrings = rdvList.map { "${it.intitule} - ${dateFormat.format(it.dateRDV)}" }

        // Utiliser un ArrayAdapter simple avec la liste des chaînes
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            rdvStrings.toMutableList()
        )

        // Assigner l'adaptateur à la ListView
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
        // Définir la taille du Dialog pour qu'il prenne toute la largeur de l'écran
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Récupérer les vues dans le Dialog
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

    private fun formatageDate(date:String):String{
        // Formats d'entrée pour les parser en Date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        // Formats de sortie pour afficher les dates au format souhaité
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return outputDateFormat.format(dateFormat.parse(date))
    }
    private fun formatageTime(time:String):String{
        // Formats d'entrée pour les parser en Date
        val horaireFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        // Formats de sortie pour afficher les horaires au format souhaité
        val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return outputTimeFormat.format(horaireFormat.parse(time))
    }
        val addToCalendarButton = dialog.findViewById<Button>(R.id.add_to_calendar_button)

        // Mettre à jour les TextViews avec les détails du RDV
        tvIntitule.text = rdv.intitule
        tvDate.text = dateFormat.format(rdv.dateRDV)
        tvHeure.text =  timeFormat.format(rdv.horaire)
        //tvAdresse.text = rdv. -> mettre adresse utilisateur


        // Ajouter un listener pour le bouton "Ajouter au calendrier"
        addToCalendarButton.setOnClickListener {
            calendarHelper.addEventToCalendar(rdv)
            dialog.dismiss() // Ferme le dialogue après l'action
        }


        // Ajouter un listener pour fermer le dialog en cliquant sur la croix
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Permettre la fermeture du Dialog en cliquant en dehors
        dialog.setCanceledOnTouchOutside(true)

        // Afficher le Dialog
        dialog.show()
    }

    companion object {
        fun newInstance(): RDVFragment {
            return RDVFragment()
        }
    }
}

