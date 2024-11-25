package com.tariapp.scancare.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tariapp.scancare.MainActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.data.pref.UserPreference
import com.tariapp.scancare.data.pref.dataStore
import com.tariapp.scancare.databinding.FragmentHomeBinding
import com.tariapp.scancare.ui.login.LoginActivity
import com.tariapp.scancare.ui.scan.ScanCareActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreference: UserPreference
    private lateinit var homeViewModel: HomeViewModel

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
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

        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navView = (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.navView)
        with(binding){
            tvSeeallHistory.setOnClickListener {
                navView?.selectedItemId = R.id.navigation_history
            }
            tvSeeallBookmark.setOnClickListener {
                navView?.selectedItemId = R.id.navigation_bookmark
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}