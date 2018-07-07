package de.twiechert.naa.geo

import java.io.FileInputStream
import java.util.zip.GZIPInputStream

import de.twiechert.naa.config.Params
import de.twiechert.naa.geo.NaiveNearestNeighbourFinder.AirportTriplet
import jsat.linear.distancemetrics.DistanceMetric
import jsat.linear.{Vec, vectorcollection}
import jsat.linear.vectorcollection.BallTree
import jsat.linear.vectorcollection.BallTree.{ConstructionMethod, PivotSelection}

import scala.io.Source

trait NearestNeighbourFinder {

  /**
    * This method returns for an Array of positions reports the respective array
    * of closest airports.
    *
    * @param coordinates the array of location reports to return the closest airports each
    * @return the array of closest airports
    */
  def find(coordinates: Array[(String, Double, Double)]): Array[(String, String)] = {
    for (coordinatePair <- coordinates) yield this.find(coordinatePair)
  }

  /**
    * This method returns for a single positions report the respective closest airport.
    *
    * @param coordinate the location report to return the closest airport
    * @return the closest airport
    */
  def find(coordinate: (String, Double, Double)): (String, String)

}

/**
  * This corresponds to the optimized version of the nearest neighbour finder that relies on spatial indexing.
  *
  */
object SpatialIndexBasedNearestNeighbourFinder extends NearestNeighbourFinder {


  val balltree: BallTree[AirportTriplet] = setup()

  def setup(): BallTree[AirportTriplet] = {
    // create a ball tree with Haversine distance metric
    val balltree = new BallTree[AirportTriplet](new HaversineDistance(), ConstructionMethod.TOP_DOWN_FARTHEST, PivotSelection.MEDOID)

    // read data
    val bufferedSource = new GZIPInputStream(new FileInputStream(Params.AirportDataPath))
    var firstLine = true
    // insert into tree
    for (line <- Source.fromInputStream(bufferedSource).getLines())
    {
      if(!( firstLine | {firstLine = false; false })) {
        val cols = line.split(",").map(_.trim)
        // insert data to tree
        balltree.insert(new AirportTriplet(cols(0), cols(1).toDouble, cols(2).toDouble))
      }
    }


    balltree
  }


  override def find(locationReportCoordinates: (String, Double, Double)): (String, String) = {
    // tree.findNearest((coordinates), 1)
    val result = balltree.search(new AirportTriplet(locationReportCoordinates), 1).get(0).getVector
    (locationReportCoordinates._1, result.aiportIdentifier)

  }
}


/**
  * This corresponds to the naive implementation of the nearest neighbour finder.
  * Thus, is complexity is O(n). These n comparisons would then be processed for every single element.
  */
object NaiveNearestNeighbourFinder extends NearestNeighbourFinder {

  var airportLocations: List[(String, Double, Double)] = setup()

  override def find(locationReportCoordinates: (String, Double, Double)): (String, String) = {


    var minDistanceAirport = this.airportLocations.map(airPortlocation =>
      (airPortlocation._1, GeoUtil.haversineDistance(airPortlocation, locationReportCoordinates))
    ).minBy(distanceTuple => distanceTuple._2)

    val ret = (locationReportCoordinates._1, minDistanceAirport._1)

    ret
  }


  /**
    * Sets this implementation up, i.e. makes the
    *
    * @return
    */
  def setup(): List[(String, Double, Double)] = {
    val bufferedSource = new GZIPInputStream(new FileInputStream(Params.AirportDataPath))
    var firstLine = true

    val airportLocations = (for (line <- Source.fromInputStream(bufferedSource).getLines(); if {
      var c = firstLine; firstLine = false; !c
    }) yield {
      val cols = line.split(",").map(_.trim)
      (cols(0), cols(1).toDouble, cols(2).toDouble)
    }).toList

    bufferedSource.close
    airportLocations
  }


  class AirportTriplet(var aiportIdentifier: String, var latitude: Double, var longitude: Double) extends Vec {


    def this(coordinates: (String, Double, Double)) {
      this(coordinates._1, coordinates._2, coordinates._3);
    }


    override def length(): Int = 2

    override def get(i: Int): Double =
      i match {
        case 0 => this.latitude
        case _ => this.longitude
      }

    override def set(i: Int, v: Double) = {
      // NOT SUPPORTED
    }

    override def isSparse: Boolean = false

    // not needed thus just copy
    override def clone: Vec = this


  }


}
