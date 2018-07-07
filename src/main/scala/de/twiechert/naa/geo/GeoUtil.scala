package de.twiechert.naa.geo

object GeoUtil {


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
