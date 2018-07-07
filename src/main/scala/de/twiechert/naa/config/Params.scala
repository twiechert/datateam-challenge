package de.twiechert.naa.config

import java.text.SimpleDateFormat
import java.util.Calendar

import de.twiechert.naa.geo.{NaiveNearestNeighbourFinder, BallTreeBasedNearestNeighbourFinder}

object Params {

  val AirportDataPath = "/home/twiechert/IdeaProjects/datateam-challenge/sample_data/optd-sample-20161201.csv.gz"
  val LocationData = "/home/twiechert/IdeaProjects/datateam-challenge/sample_data/sample_data.csv.gz"
  val OutputPath = "/home/twiechert/IdeaProjects/datateam-challenge/out"
  val DateOutputFormat =  new SimpleDateFormat("HH_mm_ss")

  val NearestNeighbourFinder =  BallTreeBasedNearestNeighbourFinder // NaiveNearestNeighbourFinder
  def getOutputfilePath() = {
    val fileName =  DateOutputFormat.format(Calendar.getInstance().getTime())
    s"$OutputPath/$fileName"
  }
}
