package ps.leic.isel.pt.gis.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import ps.leic.isel.pt.gis.ServiceLocator
import ps.leic.isel.pt.gis.model.dtos.ErrorDto
import ps.leic.isel.pt.gis.model.dtos.IndexDto
import ps.leic.isel.pt.gis.repositories.Resource

class SplashScreenViewModel(private val app: Application) : AndroidViewModel(app) {

    private var index: LiveData<Resource<IndexDto, ErrorDto>>? = null

    fun init() {
        if (index != null) return
        index = ServiceLocator.getRepository(app.applicationContext)
                .get(IndexDto::class.java, ErrorDto::class.java, url, TAG)
    }

    fun getIndex(): LiveData<Resource<IndexDto, ErrorDto>>? {
        return index
    }

    fun cancel() {
        ServiceLocator.getRepository(app.applicationContext).cancelAllPendingRequests(TAG)
        index = null
    }

    companion object {
		private const val url: String = "http://10.10.4.173:8081/v1" //TODO: Change URL here.
        const val TAG: String = "SplashScreenViewModel"
    }
}