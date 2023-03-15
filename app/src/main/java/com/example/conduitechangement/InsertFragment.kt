package com.example.conduitechangement


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.conduitechangement.databinding.FragmentInsertBinding
import kotlinx.coroutines.*



class InsertFragment : Fragment() {
    private lateinit var binding : FragmentInsertBinding
    private lateinit var ateliers : List<Atelier>
    private lateinit var selectedItem : Atelier

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInsertBinding.inflate( inflater , container , false )
        return binding.root
    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remplir la liste déroulante
        val spinner: Spinner = view.findViewById(R.id.spinner)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitService.endpoint.getatelier()
                // Mettre à jour le Spinner dans le thread principal
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        ateliers = response.body()!! // mettre adapter a ce niveau car c adapter qui contient les donnees

                        val adapter = ArrayAdapter( requireActivity() ,  android.R.layout.simple_spinner_item, ateliers)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                    }
                    else {
                        Toast.makeText(requireActivity(), "Unexpected code $response"  , Toast.LENGTH_SHORT).show()
                        ateliers = listOf(Atelier(-1 , "erreur" ))
                        val adapter = ArrayAdapter( requireActivity() ,  android.R.layout.simple_spinner_item, ateliers)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter
                        throw Exception("Unexpected code $response")
                    }
                }
            } catch (e: Exception) {
                // log the exception with Log.e()
                Log.e("TAG", "Error message: ${e.message}", e)            }
        }

        // Ajouter un nouvel etat
        binding.button.setOnClickListener{
            // Vérifier que les valeurs ont été introduit:
            if (binding.actuel.text.isEmpty()){
                binding.actuel.error = "Champ vide"
            }
            if (binding.future.text.isEmpty()){
                binding.future.error = "Champ vide"
            }
            else {
                val actuel = binding.actuel.text.toString().toInt()
                val future = binding.future.text.toString().toInt()

                if ((actuel <1)  || (actuel>22)){
                    binding.actuel.error = "Entrer un nombre entre 1 et 22"
                }
                else if ((future <1)  || (future>22)) {
                    binding.future.error = "Entrer un nombre entre 1 et 22"
                }
                else {
                    // récuperer user_id qui ajoute l'etat:
                    val pref = requireActivity().getSharedPreferences("fileConnexion" , AppCompatActivity.MODE_PRIVATE)
                    val userid = pref.getInt("user_id", 0)

                    val new = Etat(actuel , future , null ,userid , selectedItem.atelier_id)
                    addEtat(new)
                }
            }
        }

        // Récuperer l'élement sélectionner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = parent?.getItemAtPosition(position) as Atelier
                Toast.makeText(requireActivity(), "Selected item: $selectedItem", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //prendre le premier Atelier par default
                selectedItem = Atelier(-1 , "erreur")
            }
        }

    }


    private fun addEtat(new : Etat) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitService.endpoint.addetat(new)
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        Toast.makeText(requireActivity(), "Etat ajouté"  , Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireActivity(), "Erreur lors de l'ajout" +response.code().toString() , Toast.LENGTH_SHORT).show()
                    }
                }
            }
            catch (e: Exception) {
                // log the exception with Log.e()
                Log.e("TAG", "Error message: ${e.message}", e)            }
        }
    }
}








