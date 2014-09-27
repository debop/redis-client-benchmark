redis-client-benchmark
======================

Benchmark of Redis Client Library (Jedis &amp; Rediscala)


테스트 환경 : Mac Book Pro i7, 16G
Redis : localhost, 2.8.16

### 대상 library
  1. [rediscala](https://github.com/etaty/rediscala) (using akka)
  2. [jedis](https://github.com/xetorthio/jedis)
  3. jedis with jedis pool (max pool size = 8) (scala parallel 사용)

### 결론 (내생각임)

  1. 작은 데이터를 취급할 때에는 rediscala 가 월등한 성능을 보여준다. 거의 10배 수준
  2. Jedis 를 사용할 때에는 JedisPool 을 꼭 사용하자. 특히 Pool 의 크기를 늘리면 더 좋은 성능을 낼 수 있을 것으로 예상됨.
rediscala 도 akka dispatcher 설정을 변경하면 된다. (rediscala는 akka 기본값을 사용한다)
  3. lrange 같은 데이터 양이 많은 경우는 전혀 반대의 결과가 나왔다. 원인은 rediscala 의 ByteString <-> String 의 성능이 좋지 않은 것으로 판단된다.
  4. rediscala 사용 시 대용량 데이터는 압축이나 FST 같은 속도가 빠른 value formatter 를 제작해서 사용하는 것이 좋다 (회사에서는 FST와 Snappy를 이용한 Value Formatter를 사용중이다)
  
  
