import data.DataRepositoryImpl
import data.Measurement
import data.MeasurementType
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import util.DateOperator
import util.INTERVAL_5_MIN_IN_MILLISEC
import kotlin.test.assertEquals

class SamplerTest {

    private val underTest = Sampler(INTERVAL_5_MIN_IN_MILLISEC)
    private val testStartDate = DateOperator.parseDateFromString("2017-01-03T10:05:00")
    private val expectedTstampSample1 = DateOperator.parseDateFromString("2017-01-03T10:05:00")
    private val expectedTstampSample2 = DateOperator.parseDateFromString("2017-01-03T10:10:00")

    private lateinit var repo: DataRepositoryImpl

    @Before
    fun setUp() {
        repo = DataRepositoryImpl()
    }

    @Test
    fun `valid sampling`() = runTest {
        repo.getMedicalDeviceData(VALID_INPUT_FILE_NAME).collect {
            expectValidResult(underTest.sample(testStartDate!!, it))
        }
    }

    private fun expectValidResult(result: Map<MeasurementType, List<Measurement>>) {
        assertEquals(2, result.size)
        assertEquals(setOf(MeasurementType.SPO2, MeasurementType.TEMP), result.keys)
        assertEquals(2, result[MeasurementType.TEMP]?.size)
        val firstTempSample = result[MeasurementType.TEMP]?.get(0)
        val secondTempSample = result[MeasurementType.TEMP]?.get(1)
        assertEquals(35.79, firstTempSample?.value)
        assertEquals(35.01, secondTempSample?.value)
        assertEquals(expectedTstampSample1, firstTempSample?.tstamp)
        assertEquals(expectedTstampSample2, secondTempSample?.tstamp)
        val firstSpo2Sample = result[MeasurementType.SPO2]?.get(0)
        val secondSpo2Sample = result[MeasurementType.SPO2]?.get(1)
        assertEquals(97.17, firstSpo2Sample?.value)
        assertEquals(95.08, secondSpo2Sample?.value)
        assertEquals(expectedTstampSample1, firstSpo2Sample?.tstamp)
        assertEquals(expectedTstampSample2, secondSpo2Sample?.tstamp)
    }

    @Test
    fun `no samples from empty data`() = runTest {
        repo.getMedicalDeviceData(EMPTY_INPUT_FILE_NAME).collect {
            val result = underTest.sample(testStartDate!!, it)
            assertEquals(0, result.size)
        }
    }

    @Test
    fun `will skip not readable lines`() = runTest {
        repo.getMedicalDeviceData(MIXED_INPUT_FILE_NAME).collect {
            val result = underTest.sample(testStartDate!!, it)
            assertEquals(2, result.size)
        }
    }

    @Test
    fun `can't sample invalid input format`() = runTest {
        repo.getMedicalDeviceData(INVALID_INPUT_FILE_NAME).collect {
            val result = underTest.sample(testStartDate!!, it)
            assertEquals(0, result.size)
        }
    }

    @Test
    fun `sample start date to far in future wan't return data`() = runTest {
        val farInFutureStartDate = DateOperator.parseDateFromString("2070-01-03T10:05:00")
        repo.getMedicalDeviceData(VALID_INPUT_FILE_NAME).collect {
            val result = underTest.sample(farInFutureStartDate!!, it)
            assertEquals(0, result.size)
        }
    }

    @Test
    fun `sample start date in past will return valid sampling data`() = runTest {
        val farInPastStartDate = DateOperator.parseDateFromString("2016-01-03T10:05:00")
        repo.getMedicalDeviceData(VALID_INPUT_FILE_NAME).collect {
            expectValidResult(underTest.sample(farInPastStartDate!!, it))
        }
    }

    @Test
    fun `integer value input will be converted to double`() = runTest {
        repo.getMedicalDeviceData(INTEGER_INPUT_FILE_NAME).collect {
            val result = underTest.sample(testStartDate!!, it)
            assertEquals(1, result.size)
            assertEquals(98.00, result[MeasurementType.SPO2]?.get(0)?.value)
        }
    }
}