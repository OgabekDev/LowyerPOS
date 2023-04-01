package uz.loyver.loyver.viewmodel.main.printer

import uz.loyver.loyver.db.PrinterDao
import uz.loyver.loyver.model.Printer
import javax.inject.Inject

class PrintersRepository @Inject constructor(
    private val printerDao: PrinterDao
) {

    suspend fun getAllPrinters() = printerDao.getAllPrinters()

    suspend fun updatePrinter(printer: Printer) = printerDao.updatePrinter(printer)

}