package org.andre.mlflow.client.samples

import org.andre.mlflow.client.ApiClient

object CreateExperiment {
  def main(args: Array[String]) {
    val client = new ApiClient(args(0))
    val expName = args(1)
    val expId = client.createExperiment(expName)
    println(s"$expName $expId")
  }
}
