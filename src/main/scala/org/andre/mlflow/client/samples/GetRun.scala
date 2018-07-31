package org.andre.mlflow.client.samples

import org.andre.mlflow.client.ApiClient

object GetRun {
  def main(args: Array[String]) {
    val client = new ApiClient(args(0))
    val runId = args(1)
    val run = client.getRun(runId)
    println(run)
  }
}
