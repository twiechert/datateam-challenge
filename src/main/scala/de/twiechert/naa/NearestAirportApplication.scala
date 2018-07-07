package de.twiechert.naa

import de.twiechert.naa.streaming.NearestAirportStreamBuilder
import org.slf4j.{Logger, LoggerFactory}

object NearestAirportApplication {

  val logger: Logger = LoggerFactory.getLogger(NearestAirportApplication.getClass)

  def main(args: Array[String]): Unit = {
    new NearestAirportStreamBuilder().run()
  }

}
