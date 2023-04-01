package uz.loyver.loyver.viewmodel.cheque.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.model.DeleteMessage
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class SavedChequeDetailsViewModel @Inject constructor(
    private val repository: SavedChequeDetailsRepository
): ViewModel() {

    private val _update = MutableStateFlow<UiStateObject<DeleteMessage>>(UiStateObject.EMPTY)
    val update = _update

    fun update(id: Int) = viewModelScope.launch {
        _update.value = UiStateObject.LOADING
        try {
            val response = repository.updateCheque(id)
            if (response.isSuccessful) {
                _update.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _update.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _update.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _cheque = MutableStateFlow<UiStateObject<Cheque>>(UiStateObject.EMPTY)
    val cheque = _cheque

    fun getCheque(id: Int) = viewModelScope.launch {
        _cheque.value = UiStateObject.LOADING
        try {
            val response = repository.getCheque(id)
            if (response.isSuccessful) {
                _cheque.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _cheque.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _cheque.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}