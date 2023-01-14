package com.rploutarchou.moviedb.app.ui.home

import android.app.DownloadManager.Request
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.browser.trusted.sharing.ShareTarget.RequestMethod
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request.Method.GET
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rploutarchou.moviedb.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var requestQueue: RequestQueue

    private fun apiCall() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=b2d0076e854b8797cb934384dd3da22f"
            val jsonObjectRequest = JsonObjectRequest(GET, apiUrl, null, {
                Log.d("API Request Result", it.toString())
            }, {
                Log.d("API Request Error", "${it.printStackTrace()}")
            })
            requestQueue.add(jsonObjectRequest)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        requestQueue = Volley.newRequestQueue(this.context)
        apiCall()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}