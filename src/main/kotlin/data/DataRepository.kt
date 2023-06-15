package data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

interface DataRepository {
    fun getMedicalDeviceData(path: String): Flow<List<Measurement>>
}

class DataRepositoryImpl : DataRepository {
    override fun getMedicalDeviceData(path: String): Flow<List<Measurement>> = flow {
        val result = mutableListOf<Measurement>()
        File(path).forEachLine { line ->
            MeasurementMapper().map(line)?.also {
                result.add(it)
            }
        }
        emit(result)
    }
}