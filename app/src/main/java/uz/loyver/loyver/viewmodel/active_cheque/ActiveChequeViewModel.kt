package uz.loyver.loyver.viewmodel.active_cheque

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.model.ChequeCreate
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ActiveChequeViewModel @Inject constructor(
    private val repository: ActiveChequeRepository
): ViewModel() {

    private val _cheques = MutableStateFlow<UiStateList<ChequeProduct>>(UiStateList.EMPTY)
    val cheques = _cheques

    fun getAllCheques() = viewModelScope.launch {
        _cheques.value = UiStateList.LOADING
        try {
            val response = repository.getCheques()
            _cheques.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _cheques.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteCheques = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val deleteCheques = _deleteCheques

    fun deleteCheques() = viewModelScope.launch {
        _deleteCheques.value = UiStateObject.LOADING
        try {
            val response = repository.deleteCheques()
            _deleteCheques.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _deleteCheques.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }


    private val _create = MutableStateFlow<UiStateObject<Cheque>>(UiStateObject.EMPTY)
    val create = _create

    fun createCheque(cheque: ChequeCreate) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val response = repository.createCheque(cheque)
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