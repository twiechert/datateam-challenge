package de.twiechert.naa.geo

import java.io.FileInputStream
import java.util.zip.GZIPInputStream
import de.twiechert.naa.config.Params
import de.twiechert.naa.geo.NaiveNearestNeighbourFinder.AirportTriplet
import jsat.linear.Vec
import jsat.linear.vectorcollection.BallTree
import jsat.linear.vectorcollection.BallTree.{ConstructionMethod, PivotSelection}

import scala.io.Source

/**
  * Trait for implementations that find the closest neighboring airport.
  */
trait NearestNeighbourFinder {

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
object BallTreeBasedNearestNeighbourFinder extends NearestNeighbourFinder {


  val ballTree: BallTree[AirportTriplet] = setup()

  def setup(): BallTree[AirportTriplet] = {
    // create a ball tree with Haversine distance metric
    val ballTree = new BallTree[AirportTriplet](new HaversineDistance(), ConstructionMethod.TOP_DOWN_FARTHEST, PivotSelection.MEDOID)

    // read data
    val bufferedSource = new GZIPInputStream(new FileInputStream(Params.AirportDataPath))
    var firstLine = true
    // insert into tree
    for (line <- Source.fromInputStream(bufferedSource).getLines()) {
      if (!(firstLine | {
        firstLine = false; false
      })) {
        val cols = line.split(",").map(_.trim)
        // insert data to tree
        ballTree.insert(new AirportTriplet(cols(0), cols(1).toDouble, cols(2).toDouble))
      }
    }
    ballTree
  }

  override def find(locationReportCoordinates: (String, Double, Double)): (String, String) = {
    // tree.findNearest((coordinates), 1)
    val result = ballTree.search(new AirportTriplet(locationReportCoordinates), 1).get(0).getVector
    (locationReportCoordinates._1, result.airportIdentifier)

  }
}


/**
  * This corresponds to the naive implementation of the nearest neighbour finder.
  * Thus, is complexity is O(n). These n comparisons would then be processed for every single element.
  */
object NaiveNearestNeighbourFinder extends NearestNeighbourFinder {

  val airportLocations: List[(String, Double, Double)] = setup()

  override def find(locationReportCoordinates: (String, Double, Double)): (String, String) = {
    val minDistanceAirport = this.airportLocations.map(airPortlocation =>
      (airPortlocation._1, HaversineDistance.haversineDistance(airPortlocation, locationReportCoordinates))
    ).minBy(distanceTuple => distanceTuple._2)
    (locationReportCoordinates._1, minDistanceAirport._1)
  }

  /**
    * Sets this implementation up, i.e. makes the airport locations available in-memory
    *
    * @return the setup lists of airport locations
    */
  def setup(): List[(String, Double, Double)] = {
    val bufferedSource = new GZIPInputStream(new FileInputStream(Params.AirportDataPath))
    var firstLine = true

    val airportLocations = (for (line <- Source.fromInputStream(bufferedSource).getLines(); if !(firstLine | {
        firstLine = false; false
    })) yield {
      val cols = line.split(",").map(_.trim)
      (cols(0), cols(1).toDouble, cols(2).toDouble)
    }).toList

    bufferedSource.close()
    airportLocations
  }


  /**
    * This class is needed, since the Balltree implementation requires an object of type Vec.
    *
    * @param airportIdentifier the three digit airport identifier or null
    * @param latitude          the reference object's latitude
    * @param longitude         the reference object's longitude
    */
  class AirportTriplet(var airportIdentifier: String, var latitude: Double, var longitude: Double) extends Vec {

    def this(coordinates: (String, Double, Double)) {
      this(null, coordinates._2, coordinates._3)
    }

    override def length(): Int = 2

    override def get(i: Int): Double =
      i match {
        case 0 => this.latitude
        case _ => this.longitude
      }

    override def set(i: Int, v: Double) = null

    override def isSparse: Boolean = false

    // not needed thus just copy
    override def clone: Vec = this
  }

}
