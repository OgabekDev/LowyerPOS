package uz.loyver.loyver.viewmodel.add_customer.create_customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class CreateCustomerViewModel @Inject constructor(
    private val repository: CreateCustomerRepository
): ViewModel() {

    private val _create = MutableStateFlow<UiStateObject<Customer>>(UiStateObject.EMPTY)
    val create = _create

    fun createCustomer(customer: Customer) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val response = repository.createCustomer(customer)
            if (response.isSuccessful) {
                _create.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _create.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _create.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}