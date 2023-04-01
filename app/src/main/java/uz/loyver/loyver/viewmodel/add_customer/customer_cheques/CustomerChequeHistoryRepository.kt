package uz.loyver.loyver.viewmodel.add_customer.customer_cheques

import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CustomerChequeHistoryRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCustomerCheques(id: Int, after: String, before: String) = appService.getCustomerCheque(id, after, before)

}