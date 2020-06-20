import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun main(){
    var userOption: String
    var isLanjutkan: Boolean = true
    val pathDB : String = "D:\\Belajar\\Kotlin\\Project\\TodoList.1.0\\src\\database.txt"
    val pathTempDB : String ="D:\\Belajar\\Kotlin\\Project\\TodoList.1.0\\src\\tempDB.txt"

    while (isLanjutkan) {
        clearScreen()
        println(" -- Todo List V1.0 --")
        println("-- Created by Gerryron")
        println("===============================")
        println("-- Menu : ")
        println("1.\tLihat Aktivitas")
        println("2.\tTambah Aktivitas")
        println("3.\tChecklist Aktivitas")
        println("4.\tHapus Aktivitas")

        print("\nPilihan Anda: ")
        userOption = readLine()!!

        when (userOption) {
            "1" -> tampilkanData(pathDB)
            "2" -> {
                println("=====================\nTambah data aktivitas\n=====================")
                tambahData(pathDB)
            }
            "3" -> {
                println("=======================\nCheklist data aktifitas\n=======================")
                checklistData(pathDB,pathTempDB)
            }
            "4" -> {
                println("====================\nHapus data aktifitas\n====================")
                deleteData(pathDB,pathTempDB)
            }
            else -> {
                System.err.println("Input yang anda masukkan tidak ada\nsilahkan coba lagi")
            }
        }

        isLanjutkan = getYesOrNo("Apakah anda ingin melanjutkan")
    }
}

fun checklistData(pathDB: String, pathTempDB: String){
    // get origin database
    val database: File = File(pathDB)
    val fileInput: FileReader = FileReader(database)
    val bufferInput: BufferedReader = BufferedReader(fileInput)

    // create temporary DB
    val tempDB: File = File(pathTempDB)
    val fileOutput: FileWriter = FileWriter(tempDB)
    val bufferOutput: BufferedWriter = BufferedWriter(fileOutput)

    // display data
    println(" -- List Activity")
    tampilkanData(pathDB)

    // checklist activity
    print("Masukkan nomer activitas yang ingin di checklist : ")
    var checklistNum:String = readLine()!!

    var rowsData = bufferInput.readLine()
    var entryCount: Int = 0

    while(rowsData != null){
        entryCount++

        if (checklistNum.toInt() == entryCount){
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val dateFormatted = current.format(formatter)
            val tempData = arrayOfNulls<String>(2)

            tempData[0] = rowsData.split(",").get(0)
            tempData[1] = "Done[$dateFormatted]"

            bufferOutput.write("${tempData[0]}, ${tempData[1]}")

            println("Aktivitas ${tempData[0]} berhasil diperbaharui !")
        } else{
            // copy row
            bufferOutput.write(rowsData)
        }
        bufferOutput.newLine();
        rowsData = bufferInput.readLine()
    }

    bufferOutput.flush()
    fileInput.close()
    fileOutput.close()
    bufferInput.close()
    bufferOutput.close()

    database.delete()
    tempDB.renameTo(database)
}

fun deleteData(pathDB: String, pathTempDB: String){
    val database: File = File(pathDB)
    val fileInput: FileReader = FileReader(database)
    val bufferInput: BufferedReader = BufferedReader(fileInput)

    // create temporary DB
    val tempDB: File = File(pathTempDB)
    val fileOutput: FileWriter = FileWriter(tempDB)
    val bufferOutput: BufferedWriter = BufferedWriter(fileOutput)

    // display data
    println(" -- List Activity")
    tampilkanData(pathDB)

    // select data
    print(" Masukkan nomer Aktivitas yang ingin dihapus : ")
    var deleteNum: String = readLine()!!

    // looping untuk membaca tiap data baris dan skip data yang akan didelete
    var isFound: Boolean= false
    var entryCount: Int = 0

    var rowsData = bufferInput.readLine()
    while (rowsData != null){
        entryCount++
        var isDelete: Boolean = false

        if (deleteNum.toInt() == entryCount){
            isDelete= getYesOrNo("Apakah anda yakin ingin menghapus data ${rowsData.split(",").get(0)}")
            isFound = true
        }

        if (isDelete){
            println("Data Berhasil Dihapus !")
        }else{
            // pindahkan data dari original ke temporari
            bufferOutput.write(rowsData)
            bufferOutput.newLine()
        }
        rowsData = bufferInput.readLine()
    }

    if (!isFound){
        println("Nomer Aktivitas tidak ditemukan")
    }
    bufferOutput.flush()
    fileInput.close()
    fileOutput.close()
    bufferInput.close()
    bufferOutput.close()
    // delete original file
    database.delete()
    // rename tempDB to original file name
    tempDB.renameTo(database)
}

fun tambahData(pathDB: String){
    val fileOutput: FileWriter = FileWriter(pathDB,true)
    val bufferOutput: BufferedWriter = BufferedWriter(fileOutput)

    print("Masukkan Aktifitas Baru : ")
    var aktivitas = readLine()
    while (aktivitas.isNullOrEmpty()){
        println("Tidak boleh memasukkan karakter kosong !")
        print("Masukkan Aktivitas Baru : ")
        aktivitas = readLine()
    }

    var isExist: Boolean = cekData(pathDB, aktivitas, false)

    if (!isExist){
        var isTambah: Boolean = getYesOrNo("Apakah anda ingin memasukkan data $aktivitas ini kedalam list")
        if (isTambah){
            bufferOutput.write(aktivitas+",")
            bufferOutput.newLine()
            bufferOutput.flush()
        }
    }else{
        println("Aktivitas sudah ada di list")
        cekData(pathDB, aktivitas, true)
    }

    fileOutput.close()
    bufferOutput.close()
}

fun cekData(pathDB: String, aktivitas: String, isDisplay: Boolean): Boolean{
    val fileInput: FileReader = FileReader(pathDB)
    val bufferInput: BufferedReader = BufferedReader(fileInput)

    var rowsData = bufferInput.readLine()
    var isExist: Boolean = false
    var dataNumber: Int = 0

    if (isDisplay) {
        println("|-----------------------------------------------------------|")
        println("| No |\t     Aktivitas           |\tCheck List          |")
        println("|-----------------------------------------------------------|")
    }

    while (rowsData != null){
        isExist = rowsData.toLowerCase().contains(aktivitas.toLowerCase())

        if (isExist){
            if (isDisplay){
                try {
                    print("| %2d ".format(++dataNumber))
                    print("|\t%20s    ".format(rowsData.split(",").get(0)))
                    print("|\t%17s   ".format(rowsData.split(",").get(1)))
                    print("|\n")
                } catch (e: IllegalStateException){
                    break
                }
            } else {
                break
            }
        }
        rowsData = bufferInput.readLine()
    }
    if (isDisplay){
        println("|-----------------------------------------------------------|")
    }

    fileInput.close()
    bufferInput.close()
    return isExist;
}

fun tampilkanData(pathDB: String){
    val fileInput : FileReader
    var bufferInput : BufferedReader

    try {
        fileInput = FileReader(pathDB)
        bufferInput = BufferedReader(fileInput)
    } catch (e: Exception) {
        println("Database tidak ditemukan")
        return
    }

    var rowsData: String = bufferInput.readLine()
    var dataNumber : Int = 0
    println("|--------------------------------------------------------------------|")
    println("| No |\t     Aktivitas          |\t     Check List              |")
    println("|--------------------------------------------------------------------|")
    while (true){
        try {
            print("| %2d ".format(++dataNumber))
            print("|\t%20s    ".format(rowsData.split(",").get(0)))
            print("|\t%26s   ".format(rowsData.split(",").get(1)))
            print("|\n")
            rowsData = bufferInput.readLine()
        } catch (e: IllegalStateException){
            break
        }
    }
    println("|--------------------------------------------------------------------|")
    fileInput.close()
    bufferInput.close()
}

fun getYesOrNo(message:String) : Boolean{
    print("$message [y/n]? ")
    var userOption:String = readLine()!!

    while (!userOption.equals("y", ignoreCase = true) && !userOption.equals("n",ignoreCase = true)) {
        println("Pilihan anda bukan y atau n");

        print("$message [y/n]? ")
        userOption = readLine()!!
    }

    return userOption.equals("y", ignoreCase = true)
}

fun clearScreen(){
    try {
        if (System.getProperty("os.name").contains("Windows")){
            ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        } else{
            print("\u001b[H\u001b[2J")
        }
    } catch (e: Exception){
        println(e)
    }
}