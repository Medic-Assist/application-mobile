package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cnam.medic_assist.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MesInformationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MesInformationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mes_informations, container, false)
    }

    // Pour faire fonctionner le bouton retour
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        /**
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return Nouvelle instance de MesInformationsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MesInformationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}