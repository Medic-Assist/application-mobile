package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Proche
import com.cnam.medic_assist.utils.ContactManager

/**
 * Creer un formulaire de modification des informations d'un proche
 */
class ProcheFormDialog(
    private val proche: Proche?,
    private val onProcheSaved: (Proche) -> Unit,
    private val onProcheDeleted: ((Int) -> Unit)? = null,
    private val contactManager: ContactManager
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_mes_proches, null)

        // Pré-remplir les données si un proche est passé
        val inputNom: EditText = view.findViewById(R.id.input_nom)
        val inputPrenom: EditText = view.findViewById(R.id.input_prenom)
        val inputTelephone: EditText = view.findViewById(R.id.input_telephone)
        val inputEmail: EditText = view.findViewById(R.id.input_email)
        val btnDelete: Button = view.findViewById(R.id.btn_supprimer)

        proche?.let {currentProche ->
            inputNom.setText(currentProche.nom)
            inputPrenom.setText(currentProche.prenom)
            inputTelephone.setText(currentProche.numero_tel)
            inputEmail.setText(currentProche.mail)

            // Afficher le bouton "Supprimer"
            btnDelete.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                onProcheDeleted?.invoke(currentProche.iduser!!)
                dismiss()
            }
        }

        builder.setView(view)
            .setTitle(if (proche == null) "Ajouter un proche" else "Modifier un proche")
            .setPositiveButton("Enregistrer") { _, _ ->
                val newProche = Proche(
                    nom = inputNom.text.toString(),
                    prenom = inputPrenom.text.toString(),
                    numero_tel = inputTelephone.text.toString(),
                    mail = inputEmail.text.toString()
                )

                contactManager.inviteUserByEmail(inputEmail.text.toString(), requireContext())
                onProcheSaved(newProche) // Retourner les données au parent
            }
            .setNegativeButton("Annuler") { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }

}
