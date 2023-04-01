package uz.loyver.loyver.viewmodel.add_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class AddToProductViewModel @Inject constructor(
    private val repository: AddToCategoryRepository
): ViewModel() {

    private val _products = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val products = _products

    fun getAllProducts() = viewModelScope.launch {
        _products.value = UiStateList.LOADING
        try {
            val response = repository.getAllProducts()
            _products.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _products.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}