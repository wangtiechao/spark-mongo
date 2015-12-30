import org.apache.kafka.clients.producer.{ProducerConfig, KafkaProducer, ProducerRecord}

import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils

object KafkaWordCount {
  def main(args: Array[String]) {


   // val Array(zkQuorum, group, topics, numThreads) = args
    val numThreads = "1"
    val zkQuorum = "172.24.18.57:2181,172.24.18.135:2181,172.24.18.237:2181"
    val group = "testgroup"
    val topics = "mytopic"
    val sparkConf = new SparkConf().setAppName("KafkaWordCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

   // val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val topicMap = Map("mytopic" -> 1)
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L))
      .reduceByKeyAndWindow(_ + _, _ - _, Minutes(10), Seconds(2), 2)
    wordCounts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

