package redis.benchmark


import akka.actor.ActorSystem
import org.scalameter.{Gen, PerformanceTest}
import redis.RedisClient
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * ListBenchmark
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object ListBenchmark extends PerformanceTest.Quickbenchmark {

  // val sizes = Gen.range("size")(100, 400, 100)
  val sizes = Gen.range("size")(1000, 4000, 1000)

  implicit val actorSystem = ActorSystem("redis-benchmark")

  val redis = RedisClient()
  val jedis = new Jedis("localhost")

  val rkey = "redis:benchmark:lkey"
  val jkey = "jedis:benchmark:lkey"
  val pkey = "jpool:benchmark:lkey"
  val value = "redis-benchmark-value"

  performance of "lpush" in {

    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.lpush(rkey, value + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }

    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          jedis.lpush(jkey, value + i.toString)
        }
      }
    }

    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.lpush(pkey, value + i.toString)
          }
        }
      }
    }
  }

  performance of "rpop" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.rpop[String](rkey)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          jedis.rpop(jkey)
        }
      }
    }

    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.rpop(pkey)
          }
        }
      }
    }
  }

  performance of "lrange" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          val start = 0 max i - 100
          val end = size min i + 100
          redis.lrange[String](rkey, start, end)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          val start = 0 max i - 100
          val end = size min i + 100
          jedis.lrange(jkey, start, end)
        }
      }
    }

    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            val start = 0 max i - 100
            val end = size min i + 100
            jedis.lrange(pkey, start, end)
          }
        }
      }
    }
  }
}