package uz.loyver.loyver.viewmodel.main.cheque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Cart
import uz.loyver.loyver.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class ChequeHistoryViewModel @Inject constructor(
    private val repository: ChequeHistoryRepository
): ViewModel() {

    private val _cheques = MutableStateFlow<UiStateList<Cart>>(UiStateList.EMPTY)
    val cheques = _cheques

    fun getAllCheques(after: String, before: String) = viewModelScope.launch {
        _cheques.value = UiStateList.LOADING
        try {
            val response = repository.getAllCheques(after, before)
            if (response.isSuccessful) {
                _cheques.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _cheques.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _cheques.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _search = MutableStateFlow<UiStateList<Cart>>(UiStateList.EMPTY)
    val search = _search

    fun searchCheques(cardNumber: String) = viewModelScope.launch {
        _search.value = UiStateList.LOADING
        try {
            val response = repository.searchCheque(cardNumber)
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