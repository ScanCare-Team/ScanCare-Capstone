@file:Suppress("DEPRECATION")

package com.tariapp.scancare.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.tariapp.scancare.ResultState
import com.tariapp.scancare.auth.AuthViewModel
import com.tariapp.scancare.data.ViewModelFactory
import com.tariapp.scancare.data.pref.UserPreference
import com.tariapp.scancare.data.pref.dataStore
import com.tariapp.scancare.databinding.FragmentProfileBinding
import com.tariapp.scancare.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userPreference: UserPreference

    private val viewModel: AuthViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        return root
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(requireContext())
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun observeUserData() {
        lifecycleScope.launchWhenStarted {
            userPreference.getUser().collect { user ->
                val email = user.email
                if (email.isNotEmpty()) {
                    profileViewModel.fetchUserProfile(email)
                } else {
                    Toast.makeText(requireContext(), "Email tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        profileViewModel.userProfile.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    // Tampilkan loading indicator
                }
                is ResultState.Success -> {
                    val profile = result.data
                    binding.userName.text = profile.user.name
                    binding.userEmail.text = profile.user.email
                }
                is ResultState.Error -> {
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        observeUserData()

        with(binding) {
            btnEditProfile.setOnClickListener {
                val intent = Intent(activity, EditProfileActivity::class.java)
                startActivity(intent)
            }
            btnAboutUs.setOnClickListener {
                val intent = Intent(activity, AboutUsActivity::class.java)
                startActivity(intent)
            }
            btnLogout.setOnClickListener {
                viewModel.logout()
                startActivity(Intent(this@ProfileFragment.requireContext(), LoginActivity::class.java))
                activity?.finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
