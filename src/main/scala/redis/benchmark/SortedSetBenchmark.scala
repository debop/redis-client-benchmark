package redis.benchmark

import akka.actor.ActorSystem
import org.scalameter.{Gen, PerformanceTest}
import redis.RedisClient
import redis.clients.jedis.Jedis

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * SortedSetBenchmark
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object SortedSetBenchmark extends PerformanceTest.Quickbenchmark {

  // val sizes = Gen.range("size")(100, 400, 100)
  val sizes = Gen.range("size")(10000, 40000, 10000)

  implicit val actorSystem = ActorSystem("redis-benchmark")

  val redis = RedisClient()
  val jedis = new Jedis("localhost")

  val rkey = "redis:benchmark:zkey"
  val jkey = "jedis:benchmark:zkey"
  val pkey = "jedis:benchmark:zkey"
  val value = "redis-benchmark-value"

  performance of "zadd" in {

    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.zadd(rkey, (i, value + i.toString))
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }

    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          jedis.zadd(jkey, i, value + i.toString)
        }
      }
    }

    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.zadd(pkey, i, value + i.toString)
          }
        }
      }
    }
  }

  performance of "zscore" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.zscore(rkey, value + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          jedis.zscore(jkey, value + i.toString)
        }
      }
    }
    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.zscore(pkey, value + i.toString)
          }
        }
      }
    }
  }

  performance of "zrevrank" in {
    measure method "by Rediscala" in {
      using(sizes) in { size =>
        val futures = (0 until size) map { i =>
          redis.zrevrank(rkey, value + i.toString)
        }
        Await.ready(Future.sequence(futures), 5 minutes)
      }
    }
    measure method "by Jedis" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          jedis.zrevrank(jkey, value + i.toString)
        }
      }
    }
    measure method "by JedisPool" in {
      using(sizes) in { size =>
        (0 until size) foreach { i =>
          JedisUtil.withJedisPool { jedis =>
            jedis.zrevrank(pkey, value + i.toString)
          }
        }
      }
    }
  }
}
