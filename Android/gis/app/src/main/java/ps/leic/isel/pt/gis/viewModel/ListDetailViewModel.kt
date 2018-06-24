package ps.leic.isel.pt.gis.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import ps.leic.isel.pt.gis.ServiceLocator
import ps.leic.isel.pt.gis.model.dtos.ListDto
import ps.leic.isel.pt.gis.repositories.Resource

class ListDetailViewModel(private val app: Application) : AndroidViewModel(app) {

    private var listDetail: LiveData<Resource<ListDto>>? = null

    fun init(url: String) {
        if (listDetail != null) return
        listDetail = ServiceLocator.getRepository(app.applicationContext)
                .get(ListDto::class.java, url, TAG)
    }

    fun getListDetail(): LiveData<Resource<ListDto>>? {
        return listDetail
    }

    fun cancel() {
        ServiceLocator.getRepository(app.applicationContext).cancelAllPendingRequests(TAG)
        listDetail = null
    }

    companion object {
        private const val TAG = "ListDetailViewModel"
    }
}