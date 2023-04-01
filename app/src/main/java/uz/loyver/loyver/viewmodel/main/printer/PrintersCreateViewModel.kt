package uz.loyver.loyver.viewmodel.main.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class PrintersCreateViewModel @Inject constructor(
    private val repository: PrintersCreateRepository
): ViewModel() {

    private val _create = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val create = _create

    fun createPrinter(printer: Printer) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val response = repository.createPrinter(printer)
            _create.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _create.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _update = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val update = _update

    fun updatePrinter(printer: Printer) = viewModelScope.launch {
        _update.value = UiStateObject.LOADING
        try {
            val response = repository.updatePrinter(printer)
            _update.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _update.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _printer = MutableStateFlow<UiStateObject<Printer>>(UiStateObject.EMPTY)
    val printer = _printer

    fun getPrinter(name: String) = viewModelScope.launch {
        _printer.value = UiStateObject.LOADING
        try {
            val response = repository.getPrinter(name)
            _printer.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _printer.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _delete = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val delete = _delete

    fun deletePrinter(name: String) = viewModelScope.launch {
        _delete.value = UiStateObject.LOADING
        try {
            val response = repository.deletePrinter(name)
            _delete.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _delete.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}