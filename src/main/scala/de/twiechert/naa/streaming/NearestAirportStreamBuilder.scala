package de.twiechert.naa.streaming

import de.twiechert.naa.NearestAirportApplication.logger
import de.twiechert.naa.config.Params
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._

/**
  * This stream application processedsthe following steps:
  * - reading the location report stream from the provided csv file
  * (it is NOT necessary to key the stream since elements are independent and the type of distribution across cluster is not relevant)
  * - determines for every input event the respective closest airport
  */
class NearestAirportStreamBuilder {


  def run() = {
    // local / embedded execution environment
    logger.info("Starting Stream Application")

    val env = StreamExecutionEnvironment.createLocalEnvironment()

    // Path to the airport data, read from HDFS in real world; map to triplet (UserId, Latitude, Longitude)
    val locationReports: DataStream[(String, Double, Double)] =
    env.readTextFile(Params.LocationData)
      .filter(line => !line.startsWith("uuid"))
      .map(line => {
        val lineElements = line.split(",")
        (lineElements(0), lineElements(1).toDouble, lineElements(2).toDouble)
      })

    var closestAirportStream = locationReports.map(locationReport => Params.NearestNeighbourFinder.find(locationReport))
    closestAirportStream.writeAsCsv(Params.getOutputfilePath())
    env.execute()
    logger.info("Stream Execution Ended")
  }

}
