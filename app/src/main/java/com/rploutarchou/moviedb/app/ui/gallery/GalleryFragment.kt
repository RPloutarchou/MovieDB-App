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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class TopRatedTVShowsPayload (
    val page          : Int? = null,
    val results       : ArrayList<TopRatedTVShowsResults> = arrayListOf(),
    val total_pages   : Int? = null,
    val total_results : Int? = null
)

@Serializable
data class TopRatedTVShowsResults (
    var backdrop_path     : String? = null,
    var first_air_date    : String? = null,
    var genre_ids         : ArrayList<Int> = arrayListOf(),
    var id                : Int? = null,
    var name              : String? = null,
    var origin_country    : ArrayList<String> = arrayListOf(),
    var original_language : String? = null,
    var original_name     : String? = null,
    var overview          : String? = null,
    var popularity        : Double? = null,
    var poster_path       : String? = null,
    var vote_average      : Double? = null,
    var vote_count        : Int? = null
)

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestQueue: RequestQueue

    private fun apiCall() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://api.themoviedb.org/3/tv/top_rated?api_key=b2d0076e854b8797cb934384dd3da22f&page=1"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiUrl, null, {
                Log.d("API Request Result", it.toString())
                val obj = Json.decodeFromString<TopRatedTVShowsPayload>(it.toString())
                for (i in obj.results) {
                    Log.d("Name", i.name.toString())
                    Log.d("Air Date", i.first_air_date.toString())
                    Log.d("Poster", i.poster_path.toString())
                    Log.d("Average", i.vote_average.toString())
                }
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
            ViewModelProvider(this)[GalleryViewModel::class.java]

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