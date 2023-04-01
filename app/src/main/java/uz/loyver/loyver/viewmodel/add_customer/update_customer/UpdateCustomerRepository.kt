package uz.loyver.loyver.viewmodel.add_customer.update_customer

import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class UpdateCustomerRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun getCustomer(id: Int) = appService.getCustomer(id)

    suspend fun updateCustomer(id: Int, customer: Customer) = appService.updateCustomer(id, customer)

}