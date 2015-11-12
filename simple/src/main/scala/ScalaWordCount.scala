import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.hadoop.conf.Configuration
import org.bson.BSONObject
import org.bson.BasicBSONObject


object ScalaWordCount {

  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Scala Word Count")
    val mongoConfig = new Configuration()
    mongoConfig.set("mongo.input.uri","mongodb://localhost:27017/study.posts")
    val documents = sc.newAPIHadoopRDD(mongoConfig,classOf[com.mongodb.hadoop.MongoInputFormat],classOf[Object],classOf[BSONObject]) 
    val countsRDD = documents.flatMap(arg => {
      var str = arg._2.get("name").toString
      str = str.toLowerCase().replaceAll("[.,!?\n]", " ")
      str.split(" ")
    })
    .map(word => (word, 1))
    .reduceByKey((a, b) => a + b)

    val outputConfig = new Configuration()
    outputConfig.set("mongo.output.uri","mongodb://localhost:27017/study.pp")
    countsRDD.saveAsNewAPIHadoopFile(
    "hdfs://localhost:8020/root",
    classOf[Object],
    classOf[BSONObject],
    classOf[com.mongodb.hadoop.MongoOutputFormat[Object, BSONObject]],
    outputConfig)

  }

}

