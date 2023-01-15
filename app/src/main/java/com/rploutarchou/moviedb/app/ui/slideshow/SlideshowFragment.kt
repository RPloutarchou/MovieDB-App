package com.rploutarchou.moviedb.app.ui.slideshow

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
import com.rploutarchou.moviedb.app.databinding.FragmentSlideshowBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Serializable
data class MultiSearchPayload (
    val page          : Int? = null,
    val results       : ArrayList<MultiSearchResults> = arrayListOf(),
    val total_pages   : Int? = null,
    val total_results : Int? = null
)

@Serializable
data class MultiSearchResults (
    val adult             : Boolean? = null,
    var backdrop_path     : String? = null,
    var first_air_date    : String? = null,
    var genre_ids         : ArrayList<Int> = arrayListOf(),
    var id                : Int? = null,
    var media_type        : String? = null,
    var name              : String? = null,
    var origin_country    : ArrayList<String> = arrayListOf(),
    var original_language : String? = null,
    var original_title    : String? = null,
    var original_name     : String? = null,
    var overview          : String? = null,
    var popularity        : Double? = null,
    var poster_path       : String? = null,
    var release_date      : String? = null,
    var title             : String? = null,
    var video             : Boolean? = null,
    var vote_average      : Double? = null,
    var vote_count        : Int? = null
)

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var requestQueue: RequestQueue

    private val queryText = "Doctor Who"

    private fun apiCall() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl =
                "https://api.themoviedb.org/3/search/multi?api_key=b2d0076e854b8797cb934384dd3da22f&page=1&include_adult=false&query=$queryText"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiUrl, null, {
                Log.d("API Request Result", it.toString())
                val obj = Json.decodeFromString<MultiSearchPayload>(it.toString())
                for (i in obj.results) {
                    if (i.name != null)
                    {
                        Log.d("Name", i.name.toString())
                        Log.d("Air Date", i.first_air_date.toString())
                        Log.d("Poster", i.poster_path.toString())
                        Log.d("Average", i.vote_average.toString())
                    }
                    else
                    {
                        Log.d("Title", i.title.toString())
                        Log.d("Date", i.release_date.toString())
                        Log.d("Poster", i.poster_path.toString())
                        Log.d("Average", i.vote_average.toString())
                    }
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
            val slideshowViewModel =
                ViewModelProvider(this)[SlideshowViewModel::class.java]

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
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