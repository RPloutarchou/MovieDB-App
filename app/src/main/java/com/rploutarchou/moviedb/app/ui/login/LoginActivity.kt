package com.rploutarchou.moviedb.app.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rploutarchou.moviedb.app.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class RequestTokenPayload (
    var success       : Boolean? = null,
    var expires_at    : String?  = null,
    var request_token : String?  = null
)

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var requestQueue: RequestQueue

    companion object {
        var reqToken: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE

            if (loginResult.error != null) {
                Toast.makeText(baseContext, "Authentication Failed!",
                    Toast.LENGTH_SHORT).show()
            }

            if (loginResult.success != null) {
                auth.signInWithEmailAndPassword(username.text.toString(),password.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                requestQueue = Volley.newRequestQueue(this.baseContext)
                                requestAppToken()
                            }
                            else {
                                Toast.makeText(baseContext, "Authentication Failed!",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            Toast.makeText(baseContext, "Authentication Failed!",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            setResult(Activity.RESULT_OK)
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                        username.text.toString(),
                        password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                                username.text.toString(),
                                password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    private fun requestAppToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            val apiUrl = "https://api.themoviedb.org/3/authentication/token/new?api_key=b2d0076e854b8797cb934384dd3da22f"
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiUrl, null, {
                Log.d("API Request Result", it.toString())
                val obj = Json.decodeFromString<RequestTokenPayload>(it.toString())
                if (obj.success == true) {
                    Log.d("Request Token", obj.request_token.toString())
                    reqToken = obj.request_token.toString()
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://www.themoviedb.org/authenticate/$reqToken?redirect_to=http://moviedb.app.com/launch"))
                    startActivity(browserIntent)
                }
                else {
                    Toast.makeText(baseContext, "Authentication Failed!",
                        Toast.LENGTH_SHORT).show()
                }
            }, {
                Log.d("API Request Error", "${it.printStackTrace()}")
            })
            requestQueue.add(jsonObjectRequest)
        }
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}