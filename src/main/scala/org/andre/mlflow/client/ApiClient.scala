package org.andre.mlflow.client

import scalaj.http.{Http,HttpResponse}
import org.json4s.native.JsonMethods.parse
import org.json4s.jackson.Serialization.{read,write}
import org.json4s.DefaultFormats

class ApiClient(apiUrl: String) {
  implicit val formats = DefaultFormats
  println(s"apiClient: apiUrl=$apiUrl")

  def createExperiment(name: String) : String = {
    val imap = Map("name" -> name)
    val json = post(s"experiments/create", write(imap))
    val omap = jsonToMap(json)
    omap("experimentId").toString
  }

  def getExperiments() : Seq[ExperimentDetails] = {
    val json = get(s"experiments/list")
    val exps = read[ExperimentList](json)
    exps.experiments
  }

  def getExperiment(experimentId: String) : Experiment = {
    val json = get(s"experiments/get?experiment_id=$experimentId")
    read[Experiment](json)
  }


  def createRun(run: RunCreate) : RunCreateResult = {
    val json = post(s"runs/create", write(run))
    read[RunCreateResultWrapper](json).run.info
  }

  def updateRun(run: RunUpdate) {
    post(s"runs/update",  write(run))
  }

  def getRun(runUuid: String) : Run = {
    val json = get(s"runs/get?run_uuid=$runUuid")
    read[Run](json)
  }


  def logParameter(run_uuid: String, key: String, value: String) {
    post(s"runs/log-parameter", write(LogParam(run_uuid,key,value)))
  }

  def logMetric(run_uuid: String, key: String, value: Double, timestamp: Long) {
    post(s"runs/log-metric", write(LogMetric(run_uuid,key,value,timestamp)))
  }


  def getAsMap(path: String) : Map[String,Any] = {
    jsonToMap(get(path))
  }

  def jsonToMap(json: String) : Map[String,Any] = {
    val obj = parse(json)
    obj.extract[Map[String, Any]]
  }


  def get(path: String) : String = {
    val url = mkUrl(path)
    println(s"apiClient.get: url: $url")
    val req = Http(url).method("GET")
    val rsp = req.asString
    checkError(rsp)
    println(s"apiClient.get: rsp.body: ${rsp.body}")
    rsp.body
  }

  def post(path: String, json: String) : String = {
    val json2 = write(json) // NOTE: MLflow server expects "{\"name\":\"exp1\"}" instead of {"name":"exp1"}! This the result of python json.dump(jsonAsString).
    val url = mkUrl(path)
    println(s"apiClient.post: url: $url")
    println(s"apiClient.post: data: $json")
    val req = Http(url).postData(json2).header("Content-Type", "application/json")
    val rsp = req.asString
    checkError(rsp)
    println(s"apiClient.post: rsp.body: ${rsp.body}")
    rsp.body
  }

  def mkUrl(path: String) = s"$apiUrl/$path"

  def checkError(rsp: HttpResponse[String]) {
    println(s"apiClient.checkError: rsp.code: ${rsp.code}")
    if (rsp.isClientError || rsp.isServerError) {
      throw new ApiClientException(s"Code: ${rsp.code} StatusLine: ${rsp.statusLine} Error: ${rsp.body}")
    }
  }
}

class ApiClientException(msg: String) extends Exception(msg)
