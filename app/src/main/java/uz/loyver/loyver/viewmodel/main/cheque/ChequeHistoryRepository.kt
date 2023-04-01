package uz.loyver.loyver.viewmodel.main.cheque

import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class ChequeHistoryRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getAllCheques(after: String, before: String) = appService.getAllCheques(after, before)

    suspend fun searchCheque(cardNumber: String) = appService.searchCheques(cardNumber)

}