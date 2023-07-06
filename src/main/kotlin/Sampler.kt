import data.Measurement
import data.MeasurementType
import java.util.Date

class Sampler(private val interval: Int) {
    fun sample(
        startOfSampling: Date,
        unsampledMeasurements: List<Measurement>
    ): Map<MeasurementType, List<Measurement>> {
        val result = mutableMapOf<MeasurementType, List<Measurement>>()
        val deltaStartOfTime = Date(startOfSampling.time - interval)
        unsampledMeasurements.filter {
            it.tstamp.after(deltaStartOfTime)
        }.sortedBy { it.tstamp }.apply {
            enumValues<MeasurementType>().forEach { measurementTypeValue ->
                raster(startOfSampling, interval, this.filter {
                    it.type == measurementTypeValue
                }).also {
                    if (it.isNotEmpty()) {
                        result[measurementTypeValue] = it
                    }
                }
            }
        }
        return result
    }

    private fun raster(startOfSampling: Date, interval: Int, items: List<Measurement>): List<Measurement> {
        val rasterize = mutableListOf<Measurement>()
        items.iterator().also {
            var currentBucket = startOfSampling.time
            var cachedItem: Measurement? = null
            while (it.hasNext()) {
                val target = it.next()
                if (target.tstamp.time > currentBucket + interval) {
                    currentBucket += ((target.tstamp.time - currentBucket) / interval) * interval + interval
                }

                if (cachedItem == null) {
                    cachedItem = target
                }

                if (target.tstamp.time <= currentBucket) {
                    cachedItem = target
                }

                if (target.tstamp.time > currentBucket) {
                    rasterize.addSample(cachedItem, currentBucket)
                    cachedItem = target
                    currentBucket += interval
                }

                if (!it.hasNext()) {
                    if (rasterize.size == 0) {
                        rasterize.addSample(target, currentBucket)
                    } else if (rasterize.last() != target) {
                        rasterize.addSample(target, currentBucket)
                    }
                }
            }
        }
        return rasterize
    }

    private fun MutableList<Measurement>.addSample(element: Measurement, bucketTime: Long) {
        this.add(element.apply { this.tstamp = Date(bucketTime) })
    }
}