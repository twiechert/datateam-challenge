package de.twiechert.naa.geo

import org.scalatest.{FlatSpec, Matchers}

class NearestAirportFinderTest extends FlatSpec with Matchers {

  "NaiveNearestNeighbourFinder and SpatialIndexBasedNearestNeighbourFinder" should "return the nearest neighbour airports.." in {

    val positionReports = Seq(
      ("tu_berlin_position_guid", 52.5166257, 13.323462),
      ("lmu_munich_guid", 48.1518523, 11.581511),
      ("russia_worldcup_luzhniki", 55.714443, 37.552238)
    )

    val expedtedResults = Seq(
      ("tu_berlin_position_guid", "TXL"),
      ("lmu_munich_guid", "OBF"),
      ("russia_worldcup_luzhniki", "VKO")

    )

    for(finder <- Seq(NaiveNearestNeighbourFinder, SpatialIndexBasedNearestNeighbourFinder)) {
      val calculatedReports = positionReports.map(positionReport => finder.find(positionReport))
      calculatedReports shouldEqual expedtedResults
    }
  }

}
