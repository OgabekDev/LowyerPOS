package uz.loyver.loyver.viewmodel.main.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.*
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository): ViewModel() {

    private val _dbCategories = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val dbCategory = _dbCategories

    fun getDBCategories() = viewModelScope.launch {
        _dbCategories.value = UiStateList.LOADING

        try {
            val response = repository.getAllCategories()
            _dbCategories.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _dbCategories.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _dbProducts = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val dbProducts = _dbProducts

    fun getDBProducts() = viewModelScope.launch {
        _dbProducts.value = UiStateList.LOADING

        try {
            val response = repository.getAllProducts()
            _dbProducts.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _dbProducts.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _dbCategoryProducts = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val dbCategoryProducts = _dbCategoryProducts

    fun getDBCategoryProducts(category: String) = viewModelScope.launch {
        _dbCategoryProducts.value = UiStateList.LOADING

        try {
            val response = repository.getCategoryProducts(category)
            _dbCategoryProducts.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _dbCategoryProducts.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _search = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val search = _search

    fun search(name: String) = viewModelScope.launch {
        _search.value = UiStateList.LOADING
        try {
            val response = repository.searchProducts(name)
            _search.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _search.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _clearCheques = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val clearCheque = _clearCheques

    fun clearCheque() = viewModelScope.launch {
        _clearCheques.value = UiStateObject.LOADING
        try {
            val response = repository.clearCheque()
            _clearCheques.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _clearCheques.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _chequeSize = MutableStateFlow<UiStateList<ChequeProduct>>(UiStateList.EMPTY)
    val chequeSize = _chequeSize

    fun getChequeSize() = viewModelScope.launch {
        _chequeSize.value = UiStateList.LOADING
        try {
            val response = repository.getAllCheque()
            _chequeSize.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _chequeSize.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _createCheque = MutableStateFlow<UiStateObject<Cheque>>(UiStateObject.EMPTY)
    val createCheque = _createCheque

    fun createCheque(chequeCreate: ChequeCreate) = viewModelScope.launch {
        _createCheque.value = UiStateObject.LOADING
        try {
            val response = repository.createCheque(chequeCreate)
            _createCheque.value = UiStateObject.SUCCESS(response.body()!!)
        } catch (e: Exception) {
            _createCheque.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}