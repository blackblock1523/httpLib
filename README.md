# httpLib
原生实现http请求框架

## Gradle
```
allprojects {
    repositories {
    	...
    	maven { url 'https://jitpack.io' }
    }
}
```

```
dependencies {
    implementation 'com.github.blackblock1523:httpLib:1.0'
}
```

## How to use

#### java:

```java
		Request request = new Request.creator()
                .setUrl(url)
                .setMethod("GET") // GET or POST
                .setEncode(true)
                .setRequestType(RequestType.JSON)
                .setParams(map)
                .create();

		IResultListener listener = IResultListener getListener() {
            return new IResultListener() {
                @Override
                public void onSuccess(Response response) {
                    Log.e("http", "code: " + response.getCode());
                    Log.e("http", "result: " + response.getResult());
                }

                @Override
                public void onFailed(Exception ex, Response response) {
                    Log.e("http", "ex: " + ex.getMessage());
                    Log.e("http", "code: " + response.getResult());
                    Log.e("http", "result: " + response.getResult());
                }
            };
    	}

        HttpRequestManager.getInstance().execute(request, listener);
```
