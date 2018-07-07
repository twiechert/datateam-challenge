package de.twiechert.naa.geo

import java.{lang, util}

import jsat.linear.Vec
import jsat.linear.distancemetrics.DistanceMetric

/**
  * Represents the Haversine distance metric. Needed to implement all interface methods in order to be plugged into the Balltree.
  */
class HaversineDistance extends DistanceMetric {

  override def dist(vec: Vec, vec1: Vec): Double = {
    HaversineDistance.haversineDistance((vec.get(0), vec.get(1)), (vec1.get(0), vec1.get(1)))
  }

  override def dist(a: Int, b: Int, vecs: util.List[_ <: Vec], list1: util.List[lang.Double]): Double = this.dist(vecs.get(a), vecs.get(b))

  override def dist(a: Int, vec: Vec, vecs: util.List[_ <: Vec], list1: util.List[lang.Double]): Double = this.dist(vecs.get(a), vec)

  override def dist(a: Int, b: Vec, list: util.List[lang.Double], vecs: util.List[_ <: Vec], cache: util.List[lang.Double]): Double = this.dist(vecs.get(a), b)

  override def isSymmetric = true

  override def isSubadditive: Boolean = true

  override def isIndiscemible: Boolean = true

  override def metricBound: Double = 1.0D / 0.0

  override def supportsAcceleration() = false

  override def getAccelerationCache(list: util.List[_ <: Vec]): util.List[lang.Double] = null

  override def getQueryInfo(vec: Vec): util.List[lang.Double] = null

}


object HaversineDistance {

  private val EARTH_RADIUS = 6371 // Approx Earth radius in KM

  def haversin(`val`: Double): Double = Math.pow(Math.sin(`val` / 2), 2)

  def haversineDistance(coordinateA: (String, Double, Double), coordinateB: (String, Double, Double)): Double = {
    this.haversineDistance((coordinateA._2, coordinateA._3), (coordinateB._2, coordinateB._3))
  }

  def haversineDistance(coordinateA: (Double, Double), coordinateB: ( Double, Double)): Double = {

    var startLat = coordinateB._1
    var startLong = coordinateB._2
    var endLat = coordinateA._1
    var endLong = coordinateA._2

    val dLat = Math.toRadians(endLat - startLat)
    val dLong = Math.toRadians(endLong - startLong)


    startLat = Math.toRadians(startLat)
    endLat = Math.toRadians(endLat)

    val a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    EARTH_RADIUS * c // <-- d
  }

}