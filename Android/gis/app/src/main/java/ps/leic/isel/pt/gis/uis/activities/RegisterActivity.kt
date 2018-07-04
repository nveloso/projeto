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
import kotlinx.android.synthetic.main.activity_register.*
import ps.leic.isel.pt.gis.R
import ps.leic.isel.pt.gis.ServiceLocator
import ps.leic.isel.pt.gis.model.body.UserBody
import ps.leic.isel.pt.gis.model.dtos.ErrorDto
import ps.leic.isel.pt.gis.model.dtos.UserDto
import ps.leic.isel.pt.gis.repositories.Status
import ps.leic.isel.pt.gis.stores.SmartLock
import ps.leic.isel.pt.gis.viewModel.UsersViewModel

class RegisterActivity : AppCompatActivity() {

    private var usersViewModel: UsersViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        signupBtn.setOnClickListener(::onSignup)

        signinBtn.setOnClickListener {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SmartLock.RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "SAVE: OK")
            } else {
                Log.e(TAG, "SAVE: Canceled by user")
            }
            val name = fullnameEditText.text.toString()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val age = ageEditText.text.toString().toShort()
            val password = passwordEditText.text.toString()
            val credential: Credential = data?.getParcelableExtra(Credential.EXTRA_KEY)!!
            onComplete(credential, username, name, email, age, password)
        }
    }

    private fun onSignup(view: View) {
        val name = fullnameEditText.text.toString()
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val age = ageEditText.text.toString().toShort()
        val password = passwordEditText.text.toString()

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.please_fill_in_all_required_fields), Toast.LENGTH_LONG).show()
            return
        }

        ServiceLocator
                .getSmartLock(applicationContext)
                .storeCredentials(this,
                        username,
                        password,
                        { onComplete(it, username, name, email, age, password) },
                        ::onUncomplete
                )
    }

    private fun onComplete(credential: Credential, username: String, name: String, email: String, age: Short, password: String) {
        usersViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)
        usersViewModel?.addUser(UserBody(username, name, email, age, password))?.observe(this, Observer {
            when {
                it?.status == Status.SUCCESS -> onSuccess(it.data)
                it?.status == Status.UNSUCCESS -> onUnsuccess(it.apiError, credential)
                it?.status == Status.ERROR -> onError(it.message, credential)
            }
        })
    }

    private fun onUncomplete(exception: Exception?) {
        // TODO o qe fazer?
    }

    private fun onSuccess(user: UserDto?) {
        Log.i(TAG, "Register user with success")
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun onUnsuccess(error: ErrorDto?, credential: Credential) {
        error?.let {
            Log.e(TAG, it.developerErrorMessage)
            if (it.statusCode == 401) {
                Log.i(TAG, "Wrong credentials.")
                Toast.makeText(this, getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        onError(null, credential)
    }

    private fun onError(message: String?, credential: Credential) {
        message?.let {
            Log.e(LoginActivity.TAG, it)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        ServiceLocator
                .getSmartLock(applicationContext)
                .deleteCredentials(credential)
    }

    companion object {
        private const val TAG: String = "RegisterActivity"
    }
}
