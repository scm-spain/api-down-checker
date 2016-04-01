# Is my API down?

> - Is my api down yet?
> - No
> - Is my api down yet?
> - No
> - Is my api down yet?
> - YES!

## Why
What do you do when a web page doesn't load? You check google.com to see if it's the web's problem or your connection.

// TODO

## How
You only need to add a OkHttp Interceptor to your client.
```java
ApiDownChecker checker = new ApiDownChecker.Builder()
  .check("http://my.api/status")
  .trustGoogle()
  .build();

OkHttpClient okHttpClient = new OkHttpClient.Builder()
  .addInterceptor(ApiDownInterceptor.create()
    .checkWith(checker)
    .build())
  .build();
```

When any request fails due to an exception or an unsuccesful http status code, the ApiDownChecker will try to contact both your API and some trusted url *(like Google.com)*. If only yours fail then your API is down.
If so, the Interceptor will throw an `ApiDownException`

```java
Request request = new Request.Builder()
  .get().url("http://my.api/method")
  .build();
try {
  client.newCall(request).execute();
} catch (ApiDownException e) {
  // Your api is down, tell the user or something
}
```


WORK IN PROGRESS
