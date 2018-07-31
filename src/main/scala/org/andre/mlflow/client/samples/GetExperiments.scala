package org.andre.mlflow.client.samples

import org.andre.mlflow.client.ApiClient

object GetExperiments {
  def main(args: Array[String]) {
    val client = new ApiClient(args(0))
    if (args.length > 1) {
      val expId = args(1)
      val exp = client.getExperiment(expId)
      println(exp)
    } else {
      val exps = client.getExperiments()
      for (exp <- exps) println(exp)
    }
  }
}
