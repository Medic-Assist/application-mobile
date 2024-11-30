package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Proche

class ProcheFormDialog(
    private val proche: Proche?,
    private val onSave: (Proche) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_mes_proches, null)

        // Pré-remplir les données si un proche est passé
        val inputNom: EditText = view.findViewById(R.id.input_nom)
        val inputPrenom: EditText = view.findViewById(R.id.input_prenom)
        val inputTelephone: EditText = view.findViewById(R.id.input_telephone)
        //val inputEmail: EditText = view.findViewById(R.id.input_email)

        proche?.let {
            inputNom.setText(it.nom)
            inputPrenom.setText(it.prenom)
            inputTelephone.setText(it.numero_tel)
            //inputEmail.setText(it.email)
        }

        builder.setView(view)
            .setTitle(if (proche == null) "Ajouter un proche" else "Modifier un proche")
            .setPositiveButton("Enregistrer") { _, _ ->
                val newProche = Proche(
                    nom = inputNom.text.toString(),
                    prenom = inputPrenom.text.toString(),
                    numero_tel = inputTelephone.text.toString()
                )
//                ,
//                telephone = inputTelephone.text.toString(),
//                email = inputEmail.text.toString()
                onSave(newProche) // Retourner les données au parent
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

}
