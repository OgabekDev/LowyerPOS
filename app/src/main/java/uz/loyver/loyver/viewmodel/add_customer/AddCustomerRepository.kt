package uz.loyver.loyver.viewmodel.add_customer

import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class AddCustomerRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCustomers() = appService.getCustomers()

    suspend fun searchCustomers(name: String) = appService.searchCustomers(name)

}