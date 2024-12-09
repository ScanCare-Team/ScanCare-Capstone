@file:Suppress("DEPRECATION")

package com.tariapp.scancare.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tariapp.scancare.MainActivity
import com.tariapp.scancare.MainViewModel
import com.tariapp.scancare.R
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ScancareEntity
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.data.pref.UserPreference
import com.tariapp.scancare.data.pref.dataStore
import com.tariapp.scancare.data.room.ScanDatabase
import com.tariapp.scancare.databinding.FragmentHomeBinding
import com.tariapp.scancare.ui.detail.detailActivity
import com.tariapp.scancare.ui.history.HistoryAdapter
import com.tariapp.scancare.ui.login.LoginActivity
import com.tariapp.scancare.ui.scan.ScanCareActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreference: UserPreference
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var historyAdapter: HistoryAdapter

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val factory: ViewModelFactory by lazy {
        ViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        return root
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun observeUserData() {
        lifecycleScope.launchWhenStarted {
            userPreference.getUser().collect { user ->
                val email = user.email
                if (email.isNotEmpty()) {
                    homeViewModel.fetchUserProfile(email)
                } else {
                    Toast.makeText(requireContext(), "Email tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.userProfile.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    // Tampilkan loading indicator
                }
                is ResultState.Success -> {
                    val profile = result.data
                    binding.nameHome.text = profile.user.name
                }
                is ResultState.Error -> {
                    val errorMessage = result.error
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeUserData()
        setupHistory()

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navView = (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.navView)
        with(binding){
            tvSeeallHistory.setOnClickListener {
                navView?.selectedItemId = R.id.navigation_history
            }
            btnIdentify.setOnClickListener {
                val intent = Intent(activity, ScanCareActivity::class.java)
                startActivity(intent)
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                startActivity(Intent(this@HomeFragment.requireContext(), LoginActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun setupHistory() {
        val database = ScanDatabase.getDatabase(requireContext())
        val historyDao = database.scancareDao()

        historyAdapter = HistoryAdapter(
            deleteClick = { historyItem ->
                val viewModel: MainViewModel by viewModels { factory }
                viewModel.delete(historyItem)
            },
            onItemClicked = { historyItem ->
                sendToDetailActivity(historyItem)
            }
        )

        binding.rvVertical.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
            setHasFixedSize(true)
        }

        lifecycleScope.launchWhenStarted {
            historyDao.getAllScancare().observe(viewLifecycleOwner) { historyList ->
            historyAdapter.setDataList(historyList)
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