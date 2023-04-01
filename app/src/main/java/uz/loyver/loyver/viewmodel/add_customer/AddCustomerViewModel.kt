package uz.loyver.loyver.viewmodel.add_customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: AddCustomerRepository
): ViewModel() {

    private val _customers = MutableStateFlow<UiStateList<Customer>>(UiStateList.EMPTY)
    val customer = _customers

    fun getCustomers() = viewModelScope.launch {
        _customers.value = UiStateList.LOADING

        try {
            val response = repository.getCustomers()
            if (response.isSuccessful) {
                _customers.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _customers.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _customers.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _search = MutableStateFlow<UiStateList<Customer>>(UiStateList.EMPTY)
    val search = _search

    fun searchCustomers(name: String) = viewModelScope.launch {
        _search.value = UiStateList.LOADING

        try {
            val response = repository.searchCustomers(name)
            if (response.isSuccessful) {
                _search.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _search.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _search.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}