package usecase

import data.DataRepository
import data.Measurement
import kotlinx.coroutines.flow.Flow
import util.INPUT_FILE_NAME

class GetInputDataUseCase(
    private val repository: DataRepository
) {
    operator fun invoke(): Flow<List<Measurement>> {
        return repository.getMedicalDeviceData(INPUT_FILE_NAME)
    }
}