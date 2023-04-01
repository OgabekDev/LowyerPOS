package uz.loyver.loyver.viewmodel.add_customer.customer_cheques

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Cart
import uz.loyver.loyver.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class CustomerChequeHistoryViewModel @Inject constructor(
    private val repository: CustomerChequeHistoryRepository
): ViewModel() {

    private val _cheques = MutableStateFlow<UiStateList<Cart>>(UiStateList.EMPTY)
    val cheque = _cheques

    fun getCheques(id: Int, after: String, before: String) = viewModelScope.launch {
        _cheques.value = UiStateList.LOADING
        try {
            val response = repository.getCustomerCheques(id, after, before)
            if (response.isSuccessful) {
                _cheques.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _cheques.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _cheques.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}