package com.rploutarchou.moviedb.app.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
data class TopRatedMoviesPayload (
    val page          : Int? = null,
    val results       : ArrayList<TopRatedMoviesResults> = arrayListOf(),
    val total_pages   : Int? = null,
    val total_results : Int? = null
)

@Serializable
data class TopRatedMoviesResults (
    val adult             : Boolean? = null,
    var backdrop_path     : String? = null,
    var genre_ids         : ArrayList<Int> = arrayListOf(),
    var id                : Int? = null,
    var original_language : String? = null,
    var original_title    : String? = null,
    var overview          : String? = null,
    var popularity        : Double? = null,
    var poster_path       : String? = null,
    var release_date      : String? = null,
    var title             : String? = null,
    var video             : Boolean? = null,
    var vote_average      : Double? = null,
    var vote_count        : Int? = null
)

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestQueue: RequestQueue

    private fun apiCall() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://api.themoviedb.org/3/movie/top_rated?api_key=b2d0076e854b8797cb934384dd3da22f&page=1"
            val jsonObjectRequest = JsonObjectRequest(GET, apiUrl, null, {
                Log.d("API Request Result", it.toString())
                val obj = Json.decodeFromString<TopRatedMoviesPayload>(it.toString())
                for (i in obj.results) {
                    Log.d("Title", i.title.toString())
                    Log.d("Date", i.release_date.toString())
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
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

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