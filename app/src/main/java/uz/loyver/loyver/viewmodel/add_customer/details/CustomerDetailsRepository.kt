package uz.loyver.loyver.viewmodel.add_customer.details

import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CustomerDetailsRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCustomer(id: Int) = appService.getCustomer(id)

}