package uz.loyver.loyver.viewmodel.main.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val repository: ProductsRepository): ViewModel() {

    private val _products = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val products = _products

    fun getProducts() = viewModelScope.launch {
        _products.value = UiStateList.LOADING
        try {
            val response = repository.getProducts()
            _products.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _products.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _search = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val search = _search

    fun searchProducts(name: String) = viewModelScope.launch {
        _search.value = UiStateList.LOADING
        try {
            val response = repository.searchProducts(name)
            _search.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _search.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}