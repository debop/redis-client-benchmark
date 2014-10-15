package redis.benchmark

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
 * JedisUtil
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object JedisUtil {

  def createJedisPoolConfig(maxTotal: Int = 32) = {
    val pool = new JedisPoolConfig()
    pool.setMaxTotal(maxTotal)
    pool
  }

  val jedisPool = new JedisPool(createJedisPoolConfig(), "localhost")

  def withJedisPool(block: Jedis => Unit): Unit = {
    val jedis = jedisPool.getResource
    block(jedis)
    jedisPool.returnResource(jedis)
  }
}
