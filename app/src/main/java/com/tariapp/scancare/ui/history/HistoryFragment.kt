package com.tariapp.scancare.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tariapp.scancare.MainViewModel
import com.tariapp.scancare.data.ScancareEntity
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.databinding.FragmentHistoryBinding
import com.tariapp.scancare.ui.detail.detailActivity
import com.tariapp.scancare.ui.result.ResultActivity
import java.io.File

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter

    private val factory: ViewModelFactory by lazy {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

        // Inisialisasi Adapter dengan kedua parameter
        historyAdapter = HistoryAdapter(
            deleteClick = { scancareEntity ->
                val viewModel: MainViewModel by viewModels { factory }
                viewModel.delete(scancareEntity)
            },
            onItemClicked = { scancareEntity ->
                sendToDetailActivity(scancareEntity)
            }
        )

        // Set RecyclerView
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        // Observasi data
        val viewModel: MainViewModel by viewModels { factory }
        viewModel.getAllScancare().observe(viewLifecycleOwner) { scanHistoryList ->
            if (scanHistoryList.isNotEmpty()) {
                historyAdapter.setDataList(scanHistoryList)
                binding.rvHistory.visibility = View.VISIBLE
            } else {
                binding.rvHistory.visibility = View.GONE
            }
        }
    }

    private fun sendToDetailActivity(scancareEntity: ScancareEntity) {
        val gson = Gson()

        // Deserialize hazardous materials and analyses
        val hazardousMaterials: List<String> = gson.fromJson(
            scancareEntity.hazardousMaterials,
            object : TypeToken<List<String>>() {}.type
        )
        val analyses: List<String> = gson.fromJson(
            scancareEntity.analyses,
            object : TypeToken<List<String>>() {}.type
        )
        val predictedSkinTypes: List<String> = gson.fromJson(
            scancareEntity.predictedSkinTypes,
            object : TypeToken<List<String>>() {}.type
        )

        // Debug logs (optional)
        Log.d("HazardousMaterials", hazardousMaterials.toString())
        Log.d("Analyses", analyses.toString())
        Log.d("PredictedSkinTypes", predictedSkinTypes.toString())

        // Send data to detailActivity
        val intent = Intent(requireContext(), detailActivity::class.java).apply {
            putExtra("imageUri", scancareEntity.imageUri)
            putExtra("status", scancareEntity.status)
            putExtra("scanName", scancareEntity.scanName)
            putStringArrayListExtra(
                "hazardousMaterials",
                ArrayList(hazardousMaterials)
            )
            putStringArrayListExtra(
                "analyses",
                ArrayList(analyses)
            )
            putStringArrayListExtra(
                "predictedSkinTypes",
                ArrayList(predictedSkinTypes)
            )
        }
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}