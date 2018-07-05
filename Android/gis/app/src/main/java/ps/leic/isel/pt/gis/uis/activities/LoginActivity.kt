package ps.leic.isel.pt.gis.uis.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.credentials.Credential
import kotlinx.android.synthetic.main.activity_login.*
import ps.leic.isel.pt.gis.GisApplication
import ps.leic.isel.pt.gis.R
import ps.leic.isel.pt.gis.ServiceLocator
import ps.leic.isel.pt.gis.model.dtos.ErrorDto
import ps.leic.isel.pt.gis.model.dtos.UserDto
import ps.leic.isel.pt.gis.repositories.Status
import ps.leic.isel.pt.gis.stores.SmartLock
import ps.leic.isel.pt.gis.viewModel.UserViewModel

class LoginActivity : AppCompatActivity() {

    private var url: String? = null
    private var userViewModel: UserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signupBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        signinBtn.setOnClickListener(::onLoginClick)
    }

    override fun onStart() {
        super.onStart()
        ServiceLocator
                .getSmartLock(applicationContext)
                .retrieveCredentials(this, ::onComplete, ::onUncomplete)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SmartLock.RC_READ) {
            if (resultCode == RESULT_OK) {
                val credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
                onComplete(credential)
            } else {
                Log.e(TAG, "Credential Read: NOT OK")
                Toast.makeText(this, "Credential Read Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onLoginClick(view: View) {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        val credential = signinBtn.tag as? Credential

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_in_all_required_fields), Toast.LENGTH_LONG).show()
            return
        }

        val gisApplication = application as GisApplication
        gisApplication.index

        url = gisApplication.index.getUserUrl(username)

        url?.let {
            userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
            userViewModel?.init(it)
            userViewModel?.getUser()?.observe(this, Observer {
                when (it?.status) {
                    Status.SUCCESS -> onSuccess(it.data)
                    Status.UNSUCCESS -> onUnsuccess(it.apiError, credential)
                    Status.ERROR -> onError(it.message, credential)
                }
            })
        }
    }

    private fun onComplete(credential: Credential) {
        Log.i(TAG, "Credentials stored.")

        usernameEditText.setText(credential.id)
        passwordEditText.setText(credential.password)
        signinBtn.tag = credential
    }

    private fun onUncomplete() {
        onError(getString(R.string.something_went_wrong_please_try_again), null)
    }

    private fun onSuccess(user: UserDto?) {
        Log.i(TAG, "Login succeeded.")
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun onUnsuccess(error: ErrorDto?, credential: Credential?) {
        error?.let {
            Log.e(TAG, it.developerErrorMessage)
            Log.i(TAG, "Wrong credentials.")
            Toast.makeText(this, getString(R.string.wrong_credentials_please_login_again), Toast.LENGTH_SHORT).show()
        }
        onError(null, credential)
    }

    private fun onError(message: String?, credential: Credential?) {
        message?.let {
            Log.e(TAG, it)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        credential?.let {
            ServiceLocator
                    .getSmartLock(applicationContext)
                    .deleteCredentials(it)
        }
    }

    companion object {
        const val TAG: String = "LoginActivity"
    }
}
