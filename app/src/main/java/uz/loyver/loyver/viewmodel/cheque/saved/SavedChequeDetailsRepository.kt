package uz.loyver.loyver.viewmodel.cheque.saved

import uz.loyver.loyver.model.DeleteMessage
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class SavedChequeDetailsRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCheque(id: Int) = appService.getCheque(id)

    suspend fun updateCheque(id: Int) = appService.updateCheque(id, DeleteMessage(false, "a"))

}