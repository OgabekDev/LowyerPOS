package uz.loyver.loyver.viewmodel.cheque.details

import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class ChequeDetailsRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCheque(id: Int) = appService.getCheque(id)

}