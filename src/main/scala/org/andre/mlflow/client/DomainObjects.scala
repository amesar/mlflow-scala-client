package org.andre.mlflow.client

case class RunInfo(
  run_uuid: String,
  experiment_id: String,
  name: String,
  source_type: String,
  source_name: String,
  user_id: String,
  status: String,
  start_time: String,
  end_time: String,
  artifact_uri: String)

case class Param(
  key: String, 
  value: String)

case class Metric(
  key: String, 
  value: String, 
  timestamp: String)

class RunData(
  params: List[Param],
  metrics: List[Metric])

case class RunWrapper(
  info: RunInfo, 
  data: RunData)

case class Run(
  run: RunWrapper)

case class ExperimentDetails(
  experiment_id: String, 
  name: String, 
  artifact_location: String)

case class Experiment(
  experiment: ExperimentDetails, 
  runs: List[RunInfo])

case class ExperimentList(
  experiments: List[ExperimentDetails])
