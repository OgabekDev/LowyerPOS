package uz.loyver.loyver.viewmodel.add_customer.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel @Inject constructor(
    private val repository: CustomerDetailsRepository
): ViewModel() {

    private val _customers = MutableStateFlow<UiStateObject<Customer>>(UiStateObject.EMPTY)
    val customer = _customers

    fun getCustomers(id: Int) = viewModelScope.launch {
        _customers.value = UiStateObject.LOADING

        try {
            val response = repository.getCustomer(id)
            if (response.isSuccessful) {
                _customers.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _customers.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _customers.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}