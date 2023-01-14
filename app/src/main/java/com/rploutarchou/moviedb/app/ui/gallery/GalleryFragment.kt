package com.rploutarchou.moviedb.app.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rploutarchou.moviedb.app.databinding.FragmentGalleryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var requestQueue: RequestQueue

    private fun apiCall() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://api.themoviedb.org/4/tv/top_rated?api_key=b2d0076e854b8797cb934384dd3da22f"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiUrl, null, {
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
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
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