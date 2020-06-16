# API Rate Limiter
This is a simple Rate Limiting library that can limit API requests to your API.

Just annotate your API with @RateLimit and you are done !!!

Example - 
```java
@RateLimit(limit=2, duration = DurationUnit.MINUTE)
public void myMethod(){
   // do something...
}
```
By default, the TokenBucket algorithm is used, it can also be customized to use the Sliding Window Counter
```java
@RateLimit(limit=2, duration=DurationUnit.MINUTE, 
                strategy = Strategy.SLIDING_WINDOW_COUNTER,
                    differentiateByUser=true,
                    differentiateByTenant=true)
```
You can also achieve differentiation by Tenant and User, but then you need provide implementations for TenantDifferentiator and UserDifferentiator 