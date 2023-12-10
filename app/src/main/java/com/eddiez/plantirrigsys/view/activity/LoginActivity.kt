package com.eddiez.plantirrigsys.view.activity

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.eddiez.plantirrigsys.R
import com.eddiez.plantirrigsys.databinding.ActivityLoginBinding
import com.eddiez.plantirrigsys.datamodel.UserDataModel
import com.eddiez.plantirrigsys.viewmodel.UserViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val TAG: String = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val viewModel: UserViewModel by viewModels()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->

//        Log.d(TAG, result.resultCode.toString())

            if (result.resultCode != Activity.RESULT_CANCELED) {
                // There are no request codes
                val data: Intent? = result.data

                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                val familyName = credential.familyName
                val givenName = credential.givenName

                try {
//                val account = task.result

//                Log.d(TAG, "idToken: ${username}, userName: ${username}, password: ${pictureUri}")

                    firebaseAuth(idToken, familyName, givenName)
                } catch (e: Exception) {
                    e.message?.let { Log.e(e.cause.toString(), it) }
                }
            }
        }

    private fun firebaseAuth(idToken: String?, familyName: String?, givenName: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = auth.currentUser

                Log.d(TAG, user.toString())

                if (user != null) {
                    val userData = UserDataModel(
                        userName = user.email,
                        email = user.email,
                        password = user.uid,
                        firstName = givenName,
                        lastName = familyName,
                        photoUrl = user.photoUrl.toString()
                    )

                    viewModel.userData.postValue(userData)

//                    viewModel.register(userData)
                    viewModel.login(userData)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(getString(R.string.web_client_id))
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false).build()
        ).build()

        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.registrationResponse.observe(this, Observer { response ->
            if (response != null) {
                // Update UI with successful response data
                Log.d(TAG, response.toString())

                viewModel.userData.value?.let { viewModel.login(it) }
            }
        })

        viewModel.loginResponse.observe(this, Observer { response ->
            if (response != null) {
                // Update UI with successful response data
                Log.d(TAG, "Access Token: ${response.accessToken}")

                val intent = Intent(this, ScanQrActivity::class.java)
                startActivity(intent)
            }
        })

        viewModel.errorMessage.observe(this, Observer { error ->
            if (error.isNotEmpty()) {
                Log.d(TAG, error.toString())
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()

                if (error == "Login: User not found") {
                    viewModel.userData.value?.let { viewModel.register(it) }
                }
            }
        })
    }

    private fun signIn() {
        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(this) { result ->
            try {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                resultLauncher.launch(intentSenderRequest)

//                    startIntentSenderForResult(
//                        result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
//                        null, 0, 0, 0);
            } catch (e: IntentSender.SendIntentException) {
                Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
            }
        }.addOnFailureListener(this) { e ->
            // No saved credentials found. Launch the One Tap sign-up flow, or
            // do nothing and continue presenting the signed-out UI.
            Log.d(TAG, e.localizedMessage)
        }
    }
}