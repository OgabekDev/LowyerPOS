package uz.loyver.loyver.viewmodel.change_amount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class ChangeAmountViewModel @Inject constructor(private val repository: ChangeAmountRepository): ViewModel() {

    private val _add = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val add = _add

    fun add(chequeProduct: ChequeProduct) = viewModelScope.launch {
        _add.value = UiStateObject.LOADING

        try {
            val response = repository.addCheque(chequeProduct)
            _add.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _add.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _amount = MutableStateFlow<UiStateObject<ChequeProduct?>>(UiStateObject.EMPTY)
    val amount = _amount

    fun getAmount(id: Int) = viewModelScope.launch {
        _amount.value = UiStateObject.LOADING

        try {
            val response = repository.getCheque(id)
            _amount.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _amount.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _delete = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val delete = _delete

    fun delete(id: Int) = viewModelScope.launch {
        _delete.value = UiStateObject.LOADING

        try {
            val response = repository.deleteCheque(id)
            _delete.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _delete.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}