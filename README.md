# Api Down Checker
> Is my API down or what?

[![Bintray](https://img.shields.io/bintray/v/schibstedspain/maven/api-down-checker.svg?maxAge=2592000)](https://bintray.com/schibstedspain/maven/api-down-checker/) [![Build Status](https://travis-ci.org/scm-spain/api-down-checker.svg?branch=master)](https://travis-ci.org/scm-spain/api-down-checker)

This library let's you easily get notified in your client code when your API is not working, but some other trusted endpoint is. We use this to determine when the API is down.

Automatically integrates with OkHttp or Retrofit.

- [Overview](#overview)
  - [Usage](#usage)
  - [Download](#download)
  - [Features](#features)
- [Advanced](#advanced)
  - [Under the hood](#under-the-hood)
  - [Customization](#customization)
  - [Contributions](#contributions)

# Overview

### Usage
Build an ApiDownChecker and add a ApiDownInterceptor to your OkHttpClient
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
  .addInterceptor(ApiDownChecker.create()
    .check("http://my.api/status") // It should return a 200 status code
    .buildInterceptor())
  .build();
```


You will receive an ApiDownException when performing a request:
```java
Request request = new Request.Builder()
  .get().url("http://my.api/method")
  .build();
try {
  okHttpClient.newCall(request).execute();
} catch (ApiDownException e) {
  // Your api is down, warn the user or something
}
```

And that's all! If you're using Retrofit you can handle the exception in your API call or your callback.


### Download
You can import the latest version from jCenter:
```
dependencies {
  compile 'com.schibsted.spain:api-down-checker:1.0.0'
}
```

You can also grab the JAR file from [GitHub releases](https://github.com/scm-spain/ApiDownChecker/releases).


### Features
- Interceptor automatically notifies you when the API is down when your requests fail.
- Set your trusted and untrusted endpoints.
- Checks connectivity to *Google.com* by default.
- Customize the "is ok" criteria of the endpoints.
- Caches the "is api down" result for 10 seconds to avoid doing too many requests.


# Advanced

### Under the hood

What do you do when a web page doesn't load? You check [google.com](www.google.com) to see if it's that web's problem or your connection.
The idea behind this library is the same. You might have some backend system to nofity you when the API is down. But in our experience it doesn't always work well. So this is our *workaround* for that.

This library is based on two [ApiValidator](https://github.com/scm-spain/ApiDownChecker/blob/master/apidownchecker/src/main/java/net/infojobs/apidownchecker/ApiValidator.java) instances, a **trusted** and an **untrusted** validator, which are consulted to determine if your API is down. The **trusted** validator is someone you trust will *always* work, like google's home page. The **untrusted** validator represents your api, and tells you whether your API is responding properly or not.

A validator is a simple interface that tells if it's working fine.
```java
public interface ApiValidator {
    boolean isOk();
}
```


### Customization

You can customize some aspect of the behavior:

##### Validators
The library includes a simple implementation of the [ApiValidator](https://github.com/scm-spain/ApiDownChecker/blob/master/apidownchecker/src/main/java/net/infojobs/apidownchecker/ApiValidator.java), the [HttpValidator](https://github.com/scm-spain/ApiDownChecker/blob/master/apidownchecker/src/main/java/net/infojobs/apidownchecker/HttpValidator.java), which receives an url and answers **isOk** if that url is responding a successful HTTP status code (2xx). If the api responds with an errored code or throws an exception (like Unknown host or Timeout) the validator gives a negative response.

```java
public class HttpValidator implements ApiValidator {

    // stuff...

    protected boolean validateResponse(Response response) {
        return response.isSuccessful();
    }
}
```

Maybe you need to do a different checking. Maybe your status endpoint always responds with a 200 status code and you need to read some value in the return body. Or maybe you must use some kind of special authentication. In that case, just implement your own ApiValidator or extend HttpValidator.

```java
public class MyApiValidator extends HttpValidator {

    public MyApiValidator(OkHttpClient httpClient) {
        super(httpClient, "http://my.api/status");
    }

    @Override
    protected boolean validateResponse(Response response) {
        // parse a json, read a header or something
    }
}
```

Pass the validators to the Builder
```java
ApiDownChecker checker = new ApiDownChecker.Builder()
  .check(myApiValidator)
  .trust(myTrustedValidator)
  .build();
```

> Note: when you pass a String as a parameter to `check()` or `trust()` a new HttpValidator is created for you.

There is a handy `.trustGoogle()` method that is just a better looking wrapper of `trust("https://google.com")`. It's actually the default behavior, so you don't need to add it.

##### OkHttpClient
By default a new OkHttpClient is used when building ApiDownChecker. You can use a custom implementation by using `withClient()` in the builder.

```java
OkHttpClient client = getSomeCustomOkHttpClient();
ApiDownChecker checker = new ApiDownChecker.Builder()
  .check("http://my.api")
  .withClient(client)
  .build();
```

Warning: note that this OkHttp client cannot be the same that the one used to consume your API if you want to use the automagical Interceptor. That would be a cyclic dependency, and the ApiDownException thrown by the interceptor would be capture by itself.

##### Logging
You can add a simple logger to follow the library operation. By default an empty logger is used, but you can add your own.

```java
ApiDownChecker checker = new ApiDownChecker.Builder()
  .check("http://my.api/status")
  .logWith(new Logger() {
      @Override
      public void log(String message) {
          Log.w(TAG, message);
      }
  })
  .build();
```

You might see something like:

> - Failure intercepted. Checking whether your API is down...
> - Untrusted validator is OK. False alarm.

or:

> - Failure intercepted. Checking whether your API is down...
> - Untrusted validator is not OK. Now checking trusted validator...
> - Trusted validator is OK. Your API seems to be down!!

### Contributions
For bugs, requests, questions and discussions please use the [Github Issues](https://github.com/scm-spain/ApiDownChecker/issues).


License
-------

    Copyright 2016 Schibsted Classified Media Spain S.L.


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
