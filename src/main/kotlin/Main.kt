import data.DataRepositoryImpl
import kotlinx.coroutines.runBlocking
import usecase.GetInputDataUseCase
import util.DateOperator
import util.INTERVAL_5_MIN_IN_MILLISEC

private val getInputData = GetInputDataUseCase(DataRepositoryImpl())
private val sampler = Sampler(INTERVAL_5_MIN_IN_MILLISEC)
private val sampleStartDate = DateOperator.parseDateFromString("2017-01-03T10:05:00")

fun main() = runBlocking {
    getInputData().collect { value ->
        println(sampler.sample(sampleStartDate!!, value))
    }
}

