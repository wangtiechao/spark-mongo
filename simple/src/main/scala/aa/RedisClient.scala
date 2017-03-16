package aa

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.exceptions.JedisException
import redis.clients.jedis.{Jedis, JedisCluster, HostAndPort}

object RedisClient extends Serializable {
  val redisHost = "10.3.245.70"
  val redisPort = 6379
  val redisTimeout = 30000
  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort, redisTimeout)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }

  }
  sys.addShutdownHook(hook.run)

}
