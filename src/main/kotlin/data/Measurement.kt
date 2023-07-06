package data

import java.util.Date

data class Measurement(var tstamp: Date, val value: Double, val type: MeasurementType)

enum class MeasurementType {
    TEMP, SPO2, HR, LD
}