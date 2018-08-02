package org.andre.mlflow.client.samples

import org.andre.mlflow.client.ApiClient
import org.andre.mlflow.client.{RunCreate,RunUpdate,LogParam,LogMetric}

object CreateRun {
  def main(args: Array[String]) {
    val client = new ApiClient(args(0))
    val experiment_id = args(1)

    val sourceFile = new Exception().getStackTrace.head.getFileName
    val user = sys.env("USER")
    val start_time = System.currentTimeMillis

    // Create run
    val irun = RunCreate(experiment_id, "run_for_"+experiment_id, "LOCAL", sourceFile, start_time, user)
    val orun = client.createRun(irun)
    println(s"CreatedRun: $orun")
    val runId = orun.run_uuid

    // Log parameters
    client.logParameter(runId, "min_samples_leaf", "2")
    client.logParameter(runId, "max_depth", "3")

    // Log metrics
    client.logMetric(runId, "auc", 2.12)
    client.logMetric(runId, "accuracy_score", 3.12)
    client.logMetric(runId, "zero_one_loss", 4.12)

    // Update finished run
    val update = RunUpdate(runId, "FINISHED", start_time+1001)
    client.updateRun(update)

    // Get run details
    val run = client.getRun(runId)
    println(s"Run: $run")
  }
}
