package aa

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream.toPairDStreamFunctions

import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.serializer.StringDecoder
import kafka.message.MessageAndMetadata
import kafka.serializer.DefaultDecoder
import kafka.common.TopicAndPartition
import net.sf.json.JSONObject

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisException
import redis.clients.jedis.Pipeline
import redis.clients.jedis.Response
import redis.clients.jedis.{Jedis, JedisCluster, HostAndPort}

import aa.RedisClient
import org.apache.hadoop.mapred.TextOutputFormat
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.IntWritable

import scala.io.Source
import org.apache.spark.SparkContext

import scala.collection.JavaConversions._
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io._
import cc._
import bb._
import org.apache.commons.codec.binary.Base64
import sun.misc.{BASE64Encoder, BASE64Decoder}

import org.apache.spark.broadcast.Broadcast
import java.text.SimpleDateFormat;
import  java.util.Date;

object UserClickCountAnalytics {

 def ip2long(ip: String): Long ={
    val Array(a, b, c, d) = ip.split( """\.""")
    val retVal = (a.toLong << 24) + (b.toLong << 16) + (c.toLong << 8) + (d.toLong)
    printf(s"value=$retVal\n")
    retVal
  }

  def main(args: Array[String]): Unit = {
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // Create a StreamingContext with the given master URL
    val conf = new SparkConf().setMaster(masterUrl).setAppName("UserClickCountStat").set("spark.cores.max","1").set("spark.executor.memory", "1g")
   // val sc = new SparkContext("local", "iprange")

   val jedisone = RedisClient.pool.getResource
   jedisone.select(1)
   val kk=jedisone.get("bb")
   RedisClient.pool.returnResource(jedisone)
   // val file=sc.textFile("hdfs://10.3.245.70:9000/in/iprange")
    //val iprange=file.flatMap(line => line.split(" "))
    //val testjson = JSONObject.fromObject(kk)
    val testjson = new JSONObject()
    testjson.put("bb","aaa")
    val ssc = new StreamingContext(conf, Seconds(3))
    //ssc.checkpoint("checkpoint")
    //val jsonStr = """{ "bb": { "key": 84896, "value": 54 }}"""
    //val rdd = ssc.parallelize(Seq(jsonStr))
    //rdd.cache()
    // Kafka configurations
    val topics = Set("dsp")
    val brokers = "node1:9092,node2:9092,node3:9092"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")
    val dbIndex = 1
    val clickHashKey = "app::users::click"

    var now:Date = new Date()
    var  dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    var hehe = dateFormat.format( now )
    println("rrrrr"+hehe)
    var i = 0
    while (i < 1000000) {
        testjson.put("bb"+i.toString,i.toString)
    i += 1
    }
    val broadcastList = ssc.sparkContext.broadcast(testjson)
    var now1:Date = new Date()
   var he = dateFormat.format( now1 )
   println("qqqqqqq"+he)
    // Create a direct stream
    //val zkQuorum = "kandan.kafka:2181"
    val zkQuorum = "node1:2181,node2:2181,node3:2181"
    val group = "testgroup"
    val topicMap = Map("dsp" -> 1)
    //val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)
    val events = kafkaStream.flatMap(line => {


    val base64decoder = new sun.misc.BASE64Decoder
    val tanx = Bidinfo.BidInfo.parseFrom(base64decoder.decodeBuffer(line._2))
    val dd=tanx.getIp
    val d="aa"
    val oo="222.216.107.187"
     // val sparkRegex="(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)".r
     //val data = sparkRegex findFirstIn line._2
    val a = broadcastList.value.getString("bb")
    Some(d)
    })

   // val events = kafkaStream.map(r => r._2).map(r => {
    //val tanxdata = PersonMsg.Person.parseFrom(r)
  // val aa="ddd"
   // println("ttttt"+r)
    //val aa=tanxdata.getName
   // aa
   // })
   // println("ddddddd")
    events.print()
    // Compute user click times
    //val userClicks = events.map(x => (x.getString("uid"), x.getInt("click_count"))).reduceByKey(_ + _)
   // val userClicks = events.map(x => (x,1)).reduceByKey(_ + _)
    val userClicks = events.map(x => (x, 1)).reduceByKey(_ + _)
    userClicks.print()
    userClicks.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        val redisHost = "10.3.245.70"
        val redisPort = 6379
        val jedisone = RedisClient.pool.getResource
        //val jedis = jedisone.pipelined()
        jedisone.select(1)
        partitionOfRecords.foreach(pair => {

          val uid = pair._1
          val clickCount = pair._2
         // val jedis = RedisClient.pool.getResource
         // val jedis = new Jedis(redisHost, redisPort)
         // jedis.select(dbIndex)

          jedisone.hincrBy("aa", uid, clickCount)
         // RedisClient.pool.returnResource(jedis)
        })
       RedisClient.pool.returnResource(jedisone)

      })

    })

    ssc.start()
    ssc.awaitTermination()
  }
}
