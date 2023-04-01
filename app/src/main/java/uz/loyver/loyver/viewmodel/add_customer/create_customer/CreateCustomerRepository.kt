package uz.loyver.loyver.viewmodel.add_customer.create_customer

import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class CreateCustomerRepository @Inject constructor(
    private val appService: AppService
) {

    suspend fun createCustomer(customer: Customer) = appService.createCustomer(customer)

}