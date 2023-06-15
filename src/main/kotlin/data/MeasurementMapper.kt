package data

import util.DateOperator
import util.Mapper


class MeasurementMapper : Mapper<String, Measurement?> {
    override fun map(input: String): Measurement? {
        val result: Measurement? = null
        val itemsIterator = input.dropLast(1).drop(1).split(",\\s".toRegex()).iterator()
        while (itemsIterator.hasNext()) {
            DateOperator.parseDateFromString(itemsIterator.next())?.apply {
                if (!itemsIterator.hasNext()) {
                    return@apply
                }
                val type = mapType(itemsIterator.next())
                if (!itemsIterator.hasNext() || type == null) {
                    return@apply
                }
                return Measurement(
                    tstamp = this,
                    value = itemsIterator.next().toDouble(),
                    type = type,
                )
            }
        }
        return null
    }

    private fun mapType(input: String): MeasurementType? {
        return when (input) {
            MeasurementType.HR.name -> MeasurementType.HR
            MeasurementType.SPO2.name -> MeasurementType.SPO2
            MeasurementType.TEMP.name -> MeasurementType.TEMP
            else -> null
        }
    }
}