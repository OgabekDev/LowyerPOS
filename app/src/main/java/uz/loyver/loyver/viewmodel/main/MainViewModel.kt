package uz.loyver.loyver.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.db.PrinterDao
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private val _mainPrinter = MutableStateFlow<UiStateObject<Printer?>>(UiStateObject.EMPTY)
    val mainPrinter = _mainPrinter

    fun getMainPrinter() = viewModelScope.launch {
        _mainPrinter.value = UiStateObject.LOADING

        try {
            val response = repository.getMainPrinter()
            _mainPrinter.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _mainPrinter.value = UiStateObject.ERROR(e.localizedMessage ?: "Основной принтер не выбран")
        }
    }

}