# STAR SDK Android
## Introduction
This is the Android version of the Secure Tag for Approach Recognition (STAR) SDK. The idea of the sdk is, to provide a SDK, which enables an easy way to provide methods for contact tracing. This project was built within 71 hours at the HackZurich Hackathon 2020.

## Architecture
There exists a central discovery server on [Github](https://raw.githubusercontent.com/SecureTagForApproachRecognition/discovery/master/discovery.json). This server provides the necessary information for the SDK to initialize itself. After the SDK loaded the base url for its own backend it will load the infected list from there, as well as post if a user is infected.

The backend should hence gather all the infected list  from other backends and provide a collected list from all sources. As long as the keys are generated with the SDK we can validate them across different apps.

## Further Documentation

There exists a documentation repository in the [STAR](https://github.com/SecureTagForApproachRecognition) Organization. It includes Swager YAMLs for the backend API definitions, as well as some more technical details on how the keys are generated and how the validation mechanism works

## Function overview

### Initialization
Name | Description | Function Name
---- | ----------- | -------------
initWithAppId | Initializes the SDK and configures it |  `public static void init(Context context, String appId)`

### Methods 
Name | Description | Function Name
---- | ----------- | -------------
start | Starts Bluetooth tracing | `public static void start(Context context)`
stop | Stops Bluetooth tracing | `public static void stop(Context context)`
sync | Pro-actively triggers sync with backend to refresh exposed list | `public static void sync(Context context)`
status | - `number_of_handshakes` : `Number` <br /> - `tracking_active` : `Bool` <br /> - `was_contact_exposed` : `Bool` <br /> - `last_sync_update` <br /> - `am_i_exposed` <br /> - `error` (permission, bluetooth disabled, no network, ...) : `Enum` | `public static TracingStatus getStatus(Context context)`
i_was_exposed : Custom object with additional data | This method must be called upon positive test or whatever | `public static void sendIWasExposed(Context context, Object customData, CallbackListener<Void> callback)`
i_am_healed | Resets `am_i_exposed` and regenerates key | `public static void sendIWasHealed(Context context, Object customData, CallbackListener<Void> callback)`
reset | Removes all SDK related data (key and database) and de-initializes SDK | `public static void reset(Context context)`

### Broadcast
Name | Description | Function Name
---- | ----------- | -------------
status update | Status was updated; new status can be fetched with the `status` method | Register for Broadcast with the `IntentFilter` returned by `public static IntentFilter getUpdateIntentFilter()`


## Building a AAR
To build an aar file that you can include in your project use:
```sh
$ ./gradlew assemble
```
The library is generated under sdk/build/outputs/aar

## Integrating into a Project
Include the builded aar file by adding it to your project. Make sure that you also include the following dependencies:
```groovy
implementation 'androidx.core:core:1.2.0'
implementation "androidx.security:security-crypto:1.0.0-beta01"
implementation 'androidx.work:work-runtime:2.3.4'

implementation 'com.squareup.retrofit2:retrofit:2.6.2'
implementation 'com.squareup.retrofit2:converter-gson:2.6.2'
```

## Using the SDK

### Initialization
In your Application.onCreate() you have to initialize the SDK with:
```java
STARTracing.init(getContext(), "com.exmaple.your.app");
```
The provided app name has to be registered in the discovery service on [Github](https://raw.githubusercontent.com/SecureTagForApproachRecognition/discovery/master/discovery.json)

### Start / Stop tracing
To start and stop tracing use
```java
STARTracing.start(getContext());
STARTracing.stop(getContext());
```
Make sure that the user has the permission Manifest.permission.ACCESS_FINE_LOCATION granted, Bluetooth is enabled and BatteryOptimization is disabled. BatteryOptimization can be checked with
```java
PowerManager powerManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
boolean batteryOptDeact = powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName());
```
and for asking the user to disable the optimization use:
```java
startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
					Uri.parse("package:" + getContext().getPackageName())));
```

### Checking the current tracing status
```java
TracingStatus status = STARTracing.getStatus(getContext());
```
The TracingStatus object contains all information of the current tracing status.

To get notified when the status changes, you can register a broadcast receiver with
```java
getContext().registerReceiver(broadcastReceiver, STARTracing.getUpdateIntentFilter());
```

### Report user exposed
```java
STARTracing.sendIWasExposed(getContext(), null, new CallbackListener<Void>() {
				@Override
				public void onSuccess(Void response) {
				}

				@Override
				public void onError(Throwable throwable) {
				}
			});
```

### Sync with backend for exposed user
The SDK automatically registers a periodic Job to sync with the backend for new exposed users. If you want to trigger a sync manually (eg. upon a push from your backend) you can use:
```java
STARTracing.sync(getContext());
```
Make sure you do not call this method on the UI thread, because it will perform the sync synchronously.
