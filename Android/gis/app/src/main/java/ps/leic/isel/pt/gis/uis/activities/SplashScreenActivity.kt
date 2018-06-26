package ps.leic.isel.pt.gis.uis.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import ps.leic.isel.pt.gis.GisApplication
import ps.leic.isel.pt.gis.R
import ps.leic.isel.pt.gis.ServiceLocator
import ps.leic.isel.pt.gis.model.dtos.IndexDto
import ps.leic.isel.pt.gis.repositories.Status
import ps.leic.isel.pt.gis.viewModel.SplashScreenViewModel

class SplashScreenActivity : AppCompatActivity() {

    private var firstTime: Boolean? = null
    private lateinit var splashScreenViewModel: SplashScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashScreenViewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        splashScreenViewModel.init()
        splashScreenViewModel.getIndex()?.observe(this, Observer {
            if (it?.status == Status.SUCCESS)
                onSuccess(it.data!!)
            else if (it?.status == Status.ERROR)
                onError(it.message)
        })
    }

    private fun onSuccess(index: IndexDto) {
        val gisApplication = application as GisApplication
        gisApplication.index = index

        if (isFirstTime()) {
            // First time using the app
            Log.i(TAG, "First time using the app.")
            val mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            mPreferences.edit().putBoolean(FIRST_TIME_TAG, false).apply()
            finish()
            startActivity(Intent(this, RegisterActivity::class.java))
        } else {
            // Already used the app
            Log.d(TAG, "Try to retrieve credentials.")
            val username = ServiceLocator.getCredentialsStore(applicationContext).getUsername()
            username?.let {
                Log.d(TAG, "Credentials retrieved.")
                if (true) {   //TODO: validar credenciais
                    // Valid Credentials
                    Log.i(TAG, "Retrieved valid credentials, so logged in user.")
                    finish()
                    startActivity(Intent(this, HomeActivity::class.java))
                    return
                } else {
                    // Invalid Credentials
                    Log.d(TAG, "Retrieved invalid credentials, so delete retrieved credential.")
                    ServiceLocator.getCredentialsStore(applicationContext).deleteCredentials()
                }
            }
            // No credentials retrieved
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun onError(message: String?) {
        message?.let {
            Log.i(TAG, it)
        }
        Toast.makeText(this, getString(R.string.could_not_connect_to_server), Toast.LENGTH_SHORT).show()
        finish()    //TODO: kill app
    }

    private fun isFirstTime(): Boolean {
        if (firstTime == null) {
            val mPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            firstTime = mPreferences.getBoolean(FIRST_TIME_TAG, true)
        }
        return firstTime as Boolean
    }

    companion object {
        private const val TAG: String = "SplashScreenActivity"
        private const val FIRST_TIME_TAG = "first_time"
    }
}
