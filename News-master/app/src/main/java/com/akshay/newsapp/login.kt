package com.akshay.newsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import org.json.JSONObject

//import com.facebook.*
//import com.facebook.login.LoginResult
//import com.facebook.login.widget.LoginButton
//import org.json.JSONException
//import org.json.JSONObject

class login : AppCompatActivity() {
    var callbackManager: CallbackManager?=null
    var loginButton: LoginButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_login)

        loginButton = findViewById<LoginButton>(R.id.login_button)
        findViewById<Button>(R.id.facebook_button).setOnClickListener {
            loginButton?.performClick()
            facebookInit()
        }

    }

    private fun facebookInit() {
        loginButton?.setPermissions("email", "public_profile")
        loginButton?.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                val request = GraphRequest.newMeRequest(result?.accessToken) { `object`, response ->
                    getFacebookData(`object`)
                }

                var parm = Bundle()
                parm.putString("fields", "id, name, email, gender, birthday")
                request.parameters = parm
                request.executeAsync()
            }

            override fun onCancel() {
                Toast.makeText(applicationContext, "Login Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                error?.printStackTrace()
            }
        })
    }

    private fun getFacebookData(jsonObject: JSONObject) {
        try {
            val url = "http:graph.facebook.com/${jsonObject.getString("id")}/picture?type=small"
            val name = jsonObject.getString("name")
            val email = jsonObject.getString("email")

            var intent = Intent(this, NewsApp::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("profile", url)
            startActivity(intent)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
