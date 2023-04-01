package uz.loyver.loyver.viewmodel.change_amount

import uz.loyver.loyver.db.ChequeDao
import uz.loyver.loyver.model.ChequeProduct
import javax.inject.Inject

class ChangeAmountRepository @Inject constructor(
    private val chequeDao: ChequeDao
) {

    suspend fun getCheque(id: Int) = chequeDao.getCheque(id)

    suspend fun addCheque(chequeProduct: ChequeProduct) = chequeDao.createCheque(chequeProduct)

    suspend fun deleteCheque(id: Int) = chequeDao.deleteCheque(id)

}