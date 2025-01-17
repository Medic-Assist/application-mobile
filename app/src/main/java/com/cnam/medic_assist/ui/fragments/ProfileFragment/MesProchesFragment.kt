package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.models.Proche
import com.cnam.medic_assist.datas.models.RoleUser
import com.cnam.medic_assist.datas.models.Utilisateur
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PATIENT = "arg_patient"

class MesProchesFragment(patient : Patient) : Fragment() {
    private lateinit var patient: Patient
    private var proches = mutableListOf<Proche>()
    private lateinit var adapter: ProcheAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            patient = it.getParcelable(ARG_PATIENT)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // étendre la mise en page pour ce fragment (mes proches)
        return inflater.inflate(R.layout.fragment_proches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView_proches)
        val btnAddProche: Button = view.findViewById(R.id.btn_add_proche)

        adapter = ProcheAdapter(proches) { proche ->
            showProcheFormDialog(proche) // Modifier un proche
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddProche.setOnClickListener {
            showProcheFormDialog(null) // Ajouter un proche
        }

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            // Retourner au ProfilFragment
            parentFragmentManager.popBackStack()
        }

        //val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        //val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)

//        btnModifier.setOnClickListener {
//            // TODO : logique de modification
//        }
//
//        btnEnregistrer.setOnClickListener {
//            // TODO : logique pour enregister les modifs
//        }
    }

    private fun showProcheFormDialog(proche: Proche?) {
        val dialog = ProcheFormDialog(
            proche,
            onProcheSaved = { newProche ->
                if (proche == null) {
                    createAndAddNewProche(newProche)
                } else {
                    newProche.iduser = proche.iduser
                    updateProche(newProche)
                }
            },
            onProcheDeleted = { idUser ->
                deleteProche(idUser)
            }
        )
        dialog.show(parentFragmentManager, "ProcheFormDialog")
    }

    fun updateProche(proche : Proche) {
        RetrofitClient.instance.updateProche(proche.iduser!!, proche).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    fetchData()
                    Toast.makeText(context, "Proche mis à jour.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Echec de la modification du proche: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createAndAddNewProche(proche: Proche){
        addUtilisateur(proche)
    }
    private fun addUtilisateur(proche: Proche) {
        val utilisateur = Utilisateur(
            prenom = proche.prenom,
            nom = proche.nom,
            numero_tel = proche.numero_tel,
            role = RoleUser.Proche
        )
        RetrofitClient.instance.addUser(utilisateur).enqueue(object : Callback<Utilisateur> {
            override fun onResponse(call: Call<Utilisateur>, response: Response<Utilisateur>) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        proche.iduser = response.body()!!.iduser
                        addProche(proche)
                        //Toast.makeText(requireContext(), "Ajout du nouvel utilisateur réussi.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Utilisateur>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Ajout de l'utilisateur échoué.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addProche(proche: Proche) {
        if(proche.iduser != null && proche.mail != null){
            RetrofitClient.instance.addProche(proche).enqueue(object :
                Callback<Proche> {
                override fun onResponse(call: Call<Proche>, response: Response<Proche>) {
                    if (isAdded) {
                        if (response.isSuccessful && response.body() != null) {
                            addProchePatient(proche)
                            Toast.makeText(requireContext(), "Ajout du nouveau proche réussi.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Proche>, t: Throwable) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }else{
            Toast.makeText(requireContext(), "Informations manquantes.", Toast.LENGTH_SHORT).show()

        }
    }

    private fun addProchePatient(proche: Proche) {
        val relationData = mapOf(
            "idPatient" to patient.iduser,
            "idProche" to proche.iduser
        )
        RetrofitClient.instance.addProchePatientRelation(relationData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Ajout d'une nouvelle relation proche/patient réussi.", Toast.LENGTH_SHORT).show()
                        fetchData()
                    } else {
                        Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Ajout de l'utilisateur échoué.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun deleteProche(idUser: Int) {
        RetrofitClient.instance.deleteProche(idUser).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    proches.removeAll { it.iduser == idUser }
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Proche supprimé avec succès.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Erreur : ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur réseau : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchData() {
        if(patient.iduser != null){
            RetrofitClient.instance.getProchesByPatientId(patient.iduser!!).enqueue(object :
                Callback<List<Proche>> {
                override fun onResponse(call: Call<List<Proche>>, response: Response<List<Proche>>) {
                    if (isAdded) {
                        if (response.isSuccessful && response.body() != null) {
                            proches.clear() // Vider la liste actuelle
                            proches.addAll(response.body()!!) // Ajouter les nouveaux proches
                            adapter.notifyDataSetChanged() // Mettre à jour la RecyclerView
                            Toast.makeText(requireContext(), "Chargement des informations réussi.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()

                            loadDefaultProcheData()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Proche>>, t: Throwable) {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                    }
                    loadDefaultProcheData()
                }
            })
        }
    }

    private fun loadDefaultProcheData() {
        if (Constants.proches.get(1) != null) {
            proches.clear()
            proches.addAll(Constants.proches)
            adapter.notifyDataSetChanged() // Mise à jour de la RecyclerView
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
        @JvmStatic
        fun newInstance(patient: Patient) =
            MesProchesFragment(patient).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATIENT, patient)
                }
            }
    }
}
