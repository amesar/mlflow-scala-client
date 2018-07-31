package org.andre.mlflow.client

import scalaj.http.{Http,HttpResponse}
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import org.json4s.DefaultFormats

class ApiClient(apiUrl: String) {
  implicit val formats = DefaultFormats
  println(s"apiClient: apiUrl=$apiUrl")

  def createExperiment(name: String) : String = {
    val map = Map("name" -> name)
    val json = compact(render(map))
    post(s"experiments/create", json)
  }

  def getExperiments() : List[ExperimentDetails] = {
    val json = get(s"experiments/list")
    val exps = read[ExperimentList](json)
    exps.experiments
  }

  def getExperiment(experimentId: String) : Experiment = {
    val json = get(s"experiments/get?experiment_id=$experimentId")
    read[Experiment](json)
  }

  def getRun(runUuid: String) : Run = {
    val json = get(s"runs/get?run_uuid=$runUuid")
    read[Run](json)
  }

  def getAsMap(path: String) : Map[String,Any] = {
    val json = get(path)
    val obj = parse(json)
    obj.extract[Map[String, Any]] 
  }


  def get(path: String) : String = {
    val url = mkUrl(path)
    println("apiClient.get: url: "+url)
    val req = Http(url).method("GET")
    val rsp = req.asString
    checkError(rsp)
    println("apiClient.get: json: "+rsp.body)
    rsp.body
  }

  def post(path: String, data: String) : String = {
    val url = mkUrl(path)
    println("apiClient.post: url: "+url)
    println("apiClient.post: data: "+data)
    val req = Http(url).postData(data)
    val rsp = req.asString
    checkError(rsp)
    println("apiClient.post: json: "+rsp.body)
    rsp.body
  }

  def mkUrl(path: String) = apiUrl + "/" + path

  def checkError(rsp: HttpResponse[String]) {
    println("apiClient.checkError: rsp.code: "+rsp.code)
    if (rsp.isClientError || rsp.isServerError) {
      throw new ApiClientException(s"Code: ${rsp.code} StatusLine: ${rsp.statusLine} Error: ${rsp.body}")
    }
  }
}

class ApiClientException(msg: String) extends Exception(msg) {
}
