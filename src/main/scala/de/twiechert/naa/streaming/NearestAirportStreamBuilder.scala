package de.twiechert.naa.streaming

import de.twiechert.naa.NearestAirportApplication.logger
import de.twiechert.naa.config.Params
import de.twiechert.naa.geo.NaiveNearestNeighbourFinder
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._

/**
  * This stream application processed the following steps
  * - reading the location report stream from the import stream
  * - it is NOT necessary to key the stream since elements are independent and the type of
  * distribution across cluster is not relevant
  * - for performance optimization, we batch the nearest neighbour calculation
  * - for each batch we calculate the respective nearest neighbour and emit it
  */
class NearestAirportStreamBuilder {


  def run() = {
    // local / embedded execution environment
    val env = StreamExecutionEnvironment.createLocalEnvironment()

    // Path to the airport data, read from HDFS in real world
    // map to triplet (UserId, Latitude, Longitude)
    val locationReports: DataStream[(String, Double, Double)] =
    env.readTextFile(Params.LocationData)
      .filter(line => !line.startsWith("uuid"))
      .map(line => {
        var lineElements = line.split(",")
        (lineElements(0), lineElements(1).toDouble, lineElements(2).toDouble)
      })

    //locationReports.print()

    // this step is not necessary but allows for batched calculation of nearest neighbours
    // we create a count based window

    var closestAirportStream = locationReports.map(locationReport => Params.NearestNeighbourFinder.find(locationReport))
    closestAirportStream.writeAsCsv(Params.getOutputfilePath())
    env.execute()
    logger.info("Hello World End")

  }

}
