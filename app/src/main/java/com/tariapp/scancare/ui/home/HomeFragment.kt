package com.tariapp.scancare.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tariapp.scancare.MainActivity
import com.tariapp.scancare.R
import com.tariapp.scancare.databinding.FragmentHomeBinding
import com.tariapp.scancare.ui.scan.ScanCareActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        val navView = (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.navView)
        with(binding){
            tvSeeallHistory.setOnClickListener {
//                view.findNavController().navigate(R.id.navigation_history)
                navView?.selectedItemId = R.id.navigation_history
            }
            tvSeeallBookmark.setOnClickListener {
                navView?.selectedItemId = R.id.navigation_bookmark
            }
            btnIdentify.setOnClickListener {
                val intent = Intent(activity, ScanCareActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}