package uz.loyver.loyver.viewmodel.main.products.create_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.model.ProductCreateUpdate
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class CreateProductViewModel @Inject constructor(
    private val repository: CreateProductRepository
): ViewModel() {

    private val _create = MutableStateFlow<UiStateObject<ProductCreateUpdate>>(UiStateObject.EMPTY)
    val create = _create

    fun createProduct(product: ProductCreateUpdate) = viewModelScope.launch {
        _create.value = UiStateObject.LOADING
        try {
            val response = repository.createProduct(product)
            if (response.isSuccessful) {
                _create.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _create.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _create.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _categories = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val categories = _categories

    fun getCategories() = viewModelScope.launch {
        _categories.value = UiStateList.LOADING
        try {
            val response = repository.getCategories()
            _categories.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _categories.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _product = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val product = _product

    fun saveProduct(product: Product) = viewModelScope.launch {
        _product.value = UiStateObject.LOADING
        try {
            val response = repository.saveProduct(product)
            _product.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _product.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}