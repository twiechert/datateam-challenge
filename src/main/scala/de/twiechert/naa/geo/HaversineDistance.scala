package de.twiechert.naa.geo

import java.{lang, util}

import jsat.linear.Vec
import jsat.linear.distancemetrics.DistanceMetric

class HaversineDistance extends DistanceMetric {



  override def dist(vec: Vec, vec1: Vec): Double = {
    GeoUtil.haversineDistance((vec.get(1), vec.get(2)), (vec1.get(1), vec1.get(2)))
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
