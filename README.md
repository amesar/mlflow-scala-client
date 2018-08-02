# mlflow-scala-client

First pass for a Scala client for [MLflow](https://mlflow.org) REST API.
See also the MLflow [Python API](https://mlflow.org/docs/latest/python_api/index.html)
and [REST API](https://mlflow.org/docs/latest/rest_api.html).

## Requirements

* Scala 2.11.8
* sbt
* mlflow 3.1+ - to fix double encoding of JSON requests

## Build
```
sbt assembly
```

## Scala Client API

See [ApiClient.scala](src/main/scala/org/andre/mlflow/client/ApiClient.scala) 
and [DomainObjects.scala](src/main/scala/org/andre/mlflow/client/DomainObjects.scala).

```
def createExperiment(name: String) : String 

def getExperiments() : List[ExperimentDetails] 

def getExperiment(experimentId: String) : Experiment 


def createRun(run: RunCreate) : RunCreateResult 

def updateRun(run: RunUpdate)

def getRun(runUuid: String) : Run 


def logParameter(run_uuid: String, key: String, value: String) 

def logMetric(run_uuid: String, key: String, value: Double, timestamp: Long = System.currentTimeMillis)

`
def getExperimentByName(experimentName: String) : Option[ExperimentDetails] 

def getOrCreateExperimentId(experimentName: String) : String 

```

## Usage

See [CreateRun.scala](src/main/scala/org/andre/mlflow/client/samples/CreateRun.scala).

```
import org.andre.mlflow.client.{ApiClient,RunCreate,RunUpdate,LogParam,LogMetric}

object MLflowClientSample {
  def main(args: Array[String]) {
    val apiUri = "http://localhost:5000"
    val client = new ApiClient(apiUri)

    val experimentId = client.createExperiment("MyExperimentName")
    val experiment = client.getExperiment(experimentId)
    val experiments = client.getExperiments()
    println("Experiments:\n"+experiments)

    // Placeholder - train your model...

    // Create run
    val irun = RunCreate(experiment_id, "MyRun", "LOCAL", "MLflowClientSample.scala", System.currentTimeMillis, "john_doe")
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
    val update = RunUpdate(runId, "FINISHED", System.currentTimeMillis)
    client.updateRun(update)

    // Get run details
    val run = client.getRun(runId)
    println(s"Run: $run")
  }
}
```

## Run API client samples

**Initialize**
```
JAR=target/scala-2.11/amm-MLflowClient-assembly-0.1-SNAPSHOT.jar
API_URI=http://localhost:5000
```

### Sample Track Training Run

[CreateRun.scala](src/main/scala/org/andre/mlflow/client/samples/CreateRun.scala) does the following:
* Creates run from an existing experiment
* Logs some parameters
* Logs some metrics
* Updates finished run
* Gets run details

```
scala -cp $JAR org.andre.mlflow.client.samples.CreateRun $API_URI 2
```

TODO: Add Scala try-with-resources analog of Python context manager aka "with".

### Experiments

#### Get experiments
```
scala -cp $JAR org.andre.mlflow.client.samples.GetExperiments $API_URI

ExperimentDetails(0,Default,/Users/andre/work/mlflow/mlruns/0)
ExperimentDetails(1,train_diabetes,/Users/andre/work/mlflow/mlruns/1)
ExperimentDetails(2,train_wine_quality,/Users/andre/work/mlflow/mlruns/2)

```

#### Get an experiment
```
scala -cp $JAR org.andre.mlflow.client.samples.GetExperiments $API_URI 1

Experiment(ExperimentDetails(1,train_diabetes,/Users/andre/work/mlflow/mlruns/1),List(RunInfo(1929e0d25334468c8a387e67d623f329,1,Run 0,LOCAL,train_diabetes.py,andre,FINISHED,1531879670980,1531879671635,/Users/andre/work/mlflow/mlruns/1/1929e0d25334468c8a387e67d623f329/artifacts), RunInfo(6a70ccd7daa5491b9337b456ff981733,1,Run 3,LOCAL,train_diabetes.py,andre,FINISHED,1531879677247,1531879677777,/Users/andre/work/mlflow/mlruns/1/6a70ccd7daa5491b9337b456ff981733/artifacts), RunInfo(a5598e3b1cbe4850b90e5ba025cc848d,1,Run 2,LOCAL,train_diabetes.py,andre,FINISHED,1531879675213,1531879675693,/Users/andre/work/mlflow/mlruns/1/a5598e3b1cbe4850b90e5ba025cc848d/artifacts), RunInfo(fca5b1b96a2b48ca995d3e02984ca880,1,Run 1,LOCAL,train_diabetes.py,andre,FINISHED,1531879673157,1531879673682,/Users/andre/work/mlflow/mlruns/1/fca5b1b96a2b48ca995d3e02984ca880/artifacts)))
```

#### Create an experiment
```
scala -cp $JAR org.andre.mlflow.client.samples.CreateExperiment $API_URI MyExperimentName
```

### Runs
#### Get a run

```
scala -cp $JAR org.andre.mlflow.client.samples.GetRun $API_URI 15140c510d764170b23df796880a9926

Run(RunWrapper(RunInfo(15140c510d764170b23df796880a9926,0,Run 10,PROJECT,/Users/andre/work/mlflow/sklearn,andre,FINISHED,1532236897593,1532236901101,/Users/andre/work/mlflow/mlruns/0/15140c510d764170b23df796880a9926/artifacts),org.andre.mlflow.client.RunData@18bf3d14))


```
