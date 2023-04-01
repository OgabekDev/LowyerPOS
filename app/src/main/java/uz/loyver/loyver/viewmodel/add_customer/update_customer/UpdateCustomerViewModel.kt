package uz.loyver.loyver.viewmodel.add_customer.update_customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class UpdateCustomerViewModel @Inject constructor(
    private val repository: UpdateCustomerRepository
): ViewModel() {

    private val _update = MutableStateFlow<UiStateObject<Customer>>(UiStateObject.EMPTY)
    val update = _update

    fun updateCustomer(id: Int, customer: Customer) = viewModelScope.launch {
        _update.value = UiStateObject.LOADING
        try {
            val response = repository.updateCustomer(id, customer)
            if (response.isSuccessful) {
                _update.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _update.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _update.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _customer = MutableStateFlow<UiStateObject<Customer>>(UiStateObject.EMPTY)
    val customer = _customer

    fun getCustomer(id: Int) = viewModelScope.launch {
        _customer.value = UiStateObject.LOADING
        try {
            val response = repository.getCustomer(id)
            if (response.isSuccessful) {
                _customer.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _customer.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _customer.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}