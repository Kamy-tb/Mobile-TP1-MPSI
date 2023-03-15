package com.example.conduitechangement

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import com.example.conduitechangement.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var bindinglogin : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindinglogin = ActivityLoginBinding.inflate(layoutInflater)
        val view = bindinglogin.root
        setContentView(view)


        var isPasswordVisible = false
        bindinglogin.eye.setOnClickListener(){
            if (isPasswordVisible) {
                bindinglogin.pwd.transformationMethod = PasswordTransformationMethod.getInstance()
                bindinglogin.eye.setImageResource(R.drawable.eye_slash)
            } else {
                bindinglogin.pwd.transformationMethod = null
                bindinglogin.eye.setImageResource(R.drawable.eye)
            }
            isPasswordVisible = !isPasswordVisible
            bindinglogin.pwd.setSelection(bindinglogin.pwd.text.length)
        }

        bindinglogin.signin.setOnClickListener {
            val nom = bindinglogin.mail.text.toString()
            val password = bindinglogin.pwd.text.toString()
            if (nom.length == 0) {
                bindinglogin.mail.error = "Entrer votre nom"
            }
            else if (password.length == 0) {
                bindinglogin.pwd.error = "Entrer le mot de passe"
            }
            else {
                try {
                    login(nom, password)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                catch (_: InterruptedException) {
                } catch (_: IOException) {
                } catch (_: NullPointerException) {
                } catch (_: IllegalStateException) {
                } catch (_: Exception) { }
            }
        }
    }


    private fun login(nom: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitService.endpoint.verifyUser(nom, password)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val user = response.body() // mettre adapter a ce niveau car c adapter qui contient les donnees
                    if (user != null) {
                        val pref = getSharedPreferences("fileConnexion", MODE_PRIVATE)
                        pref.edit {
                            putBoolean("connected", true)
                            putInt("user_id" , user.user_id!!)
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "nom ou mot de passe non valide",
                            Toast.LENGTH_SHORT
                        ).show();
                    }

                }
                else {
                    throw Exception("Unexpected code $response")
                }
            }

        }
    }


}