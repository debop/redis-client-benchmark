package redis.benchmark

import akka.actor.ActorSystem
import org.scalameter.{Gen, PerformanceTest}
import redis.RedisClient
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * GetSetBenchmark
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object GetSetBenchmark extends PerformanceTest.Quickbenchmark {

  // val sizes = Gen.range("size")(100, 400, 100)
  val sizes = Gen.range("size")(1000, 4000, 1000)

  implicit val actorSystem = ActorSystem("redis-benchmark")

  val redis = RedisClient()
  val jedis = new Jedis("localhost")

  val rkey = "redis:benchmark:key"
  val jkey = "jedis:benchmark:key"
  val pkey = "jpool:benchmark:key"
  val value = "redis-benchmark-value"

  performance of "key set" in {

    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.set(rkey + ":" + i.toString, value + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }

    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size).foreach { i =>
          jedis.set(jkey + ":" + i.toString, value + i.toString)
        }
      }
    }

    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.set(pkey + ":" + i.toString, value + i.toString)
          }
        }
      }
    }
  }

  performance of "key get" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.get(rkey + ":" + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size).foreach { i =>
          jedis.get(jkey + ":" + i.toString)
        }
      }
    }
    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.get(pkey + ":" + i.toString)
          }
        }
      }
    }
  }

  performance of "key set/get" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.set(rkey + ":" + i.toString, value + i.toString)
          redis.get(rkey + ":" + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size).foreach { i =>
          jedis.set(jkey + ":" + i.toString, value + i.toString)
          jedis.get(jkey + ":" + i.toString)
        }
      }
    }
    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size).par.foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.set(pkey + ":" + i.toString, value + i.toString)
            jedis.get(pkey + ":" + i.toString)
          }
        }
      }
    }
  }
}
