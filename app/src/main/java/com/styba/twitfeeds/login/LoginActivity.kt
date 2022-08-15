package com.styba.twitfeeds.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.TwitterAuthProvider
import com.styba.twitfeeds.R
import com.styba.twitfeeds.welcome.WelcomeActivity
import com.styba.twitfeeds.common.BaseActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private var fireBaseAuth: FirebaseAuth? = null
    private val tag = "LoginActivity"

    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fireBaseAuth = FirebaseAuth.getInstance()

        twitterButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d(tag, "twitterLogin:success$result")
                handleTwitterSession(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.w(tag, "twitterLogin:failure", exception)
            }
        }

        buttonLinkLater.setOnClickListener{
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        twitterButton.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleTwitterSession(session: TwitterSession) {
        Log.d(tag, "handleTwitterSession:$session")
        val credential = TwitterAuthProvider.getCredential(
                session.authToken.token,
                session.authToken.secret)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: AuthCredential) {
        fireBaseAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(tag, "signInWithCredential:success")
                        startActivity(Intent(this, WelcomeActivity::class.java))
                        finish()
                    } else
                        Log.w(tag, "signInWithCredential:failure", task.exception)
                }
    }
}
