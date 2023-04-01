package uz.loyver.loyver.viewmodel.active_cheque

import uz.loyver.loyver.db.ChequeDao
import uz.loyver.loyver.model.ChequeCreate
import uz.loyver.loyver.network.AppService
import javax.inject.Inject

class ActiveChequeRepository @Inject constructor(
    private val chequeDao: ChequeDao,
    private val appService: AppService
) {

    suspend fun getCheques() = chequeDao.getAllCheques()

    suspend fun createCheque(chequeCreate: ChequeCreate) = appService.createCheque(chequeCreate)

    suspend fun deleteCheques() = chequeDao.deleteCheques()
    
}
