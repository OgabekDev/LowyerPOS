package uz.loyver.loyver.viewmodel.main.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class PrintersViewModel @Inject constructor(
    private val repository: PrintersRepository
): ViewModel() {

    private val _printers = MutableStateFlow<UiStateList<Printer>>(UiStateList.EMPTY)
    val printers = _printers

    fun getAllPrinters() = viewModelScope.launch {
        _printers.value = UiStateList.LOADING
        try {
            val response = repository.getAllPrinters()
            _printers.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _printers.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
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

}