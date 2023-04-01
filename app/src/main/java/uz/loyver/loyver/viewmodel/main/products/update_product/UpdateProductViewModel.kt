package uz.loyver.loyver.viewmodel.main.products.update_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.*
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class UpdateProductViewModel @Inject constructor(
    private val repository: UpdateProductRepository
): ViewModel() {

    private val _getCategories = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val getCategories = _getCategories

    fun getCategories() = viewModelScope.launch {
        _getCategories.value = UiStateList.LOADING
        try {
            val response = repository.getCategories()
            _getCategories.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _getCategories.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _getProduct = MutableStateFlow<UiStateObject<ProductUpdate>>(UiStateObject.EMPTY)
    val getProduct = _getProduct

    fun getProduct(id: Int) = viewModelScope.launch {
        _getProduct.value = UiStateObject.LOADING
        try {
            val response = repository.getProduct(id)
            if (response.isSuccessful) {
                _getProduct.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _getProduct.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _getProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _updateProduct = MutableStateFlow<UiStateObject<ProductUpdate>>(UiStateObject.EMPTY)
    val updateProduct = _updateProduct

    fun updateProduct(id: Int, product: ProductCreateUpdate) = viewModelScope.launch {
        _updateProduct.value = UiStateObject.LOADING
        try {
            val response = repository.updateProduct(id, product)
            if (response.isSuccessful) {
                _updateProduct.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _updateProduct.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _updateProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    fun updateProduct(id: Int, product: ProductUpdate) = viewModelScope.launch {
        _updateProduct.value = UiStateObject.LOADING
        try {
            val response = repository.updateProduct(id, product)
            if (response.isSuccessful) {
                _updateProduct.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _updateProduct.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _updateProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteProduct = MutableStateFlow<UiStateObject<DeleteMessage>>(UiStateObject.EMPTY)
    val deleteProduct = _deleteProduct

    fun deleteProduct(id: Int) = viewModelScope.launch {
        _deleteProduct.value = UiStateObject.LOADING
        try {
            val response = repository.deleteProduct(id)
            if (response.isSuccessful) {
                _deleteProduct.value = UiStateObject.SUCCESS(response.body()!!)
            } else {
                _deleteProduct.value = UiStateObject.ERROR(response.message())
            }
        } catch (e: Exception) {
            _deleteProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _updateProductDB = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val updateProductDB = _updateProductDB

    fun updateProductDB(product: Product) = viewModelScope.launch {
        _updateProductDB.value = UiStateObject.LOADING
        try {
            val response = repository.updateProductDB(product)
            _updateProductDB.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _updateProductDB.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteProductDB = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val deleteProductDB = _deleteProductDB

    fun deleteProductDB(id: Int) = viewModelScope.launch {
        _deleteProductDB.value = UiStateObject.LOADING
        try {
            val response = repository.deleteProductDB(id)
            _deleteProductDB.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _deleteProductDB.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}