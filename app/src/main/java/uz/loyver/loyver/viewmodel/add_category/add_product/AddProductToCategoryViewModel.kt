package uz.loyver.loyver.viewmodel.add_category.add_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.ChangeCategoryProducts
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class AddProductToCategoryViewModel @Inject constructor(
    private val repository: AddProductToCategoryRepository
): ViewModel() {

    private val _products = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val product = _products

    fun getAllProducts() = viewModelScope.launch {
        _products.value = UiStateList.LOADING
        try {
            val response = repository.getProducts()
            _products.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _products.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _searchProducts = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val searchProducts = _searchProducts

    fun searchProducts(name: String) = viewModelScope.launch {
        _searchProducts.value = UiStateList.LOADING
        try {
            val response = repository.searchProduct(name)
            _searchProducts.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _searchProducts.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _changeCategory = MutableStateFlow<UiStateObject<ChangeCategoryProducts>>(UiStateObject.EMPTY)
    val changeCategory = _changeCategory

    fun changeCategory(changeCategoryProducts: ChangeCategoryProducts) = viewModelScope.launch {
        _changeCategory.value = UiStateObject.LOADING
        try {
            val response = repository.changeCategory(changeCategoryProducts)
            if (response.isSuccessful) {
                _changeCategory.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _changeCategory.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _changeCategory.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _saveProduct = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val saveProduct = _saveProduct

    fun saveProduct(product: Product) = viewModelScope.launch {
        _saveProduct.value = UiStateObject.LOADING
        try {
            val response = repository.saveProduct(product)
            _saveProduct.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _saveProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}