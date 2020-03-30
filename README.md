# STAR SDK Android
## Introduction
This is the Android version of the Secure Tag for Approach Recognition (STAR) SDK. The idea of the sdk is, to provide a SDK, which enables an easy way to provide methods for contact tracing. This projekt was built within 72 hours at the HackZurich Hackathon 2020.

## Architecture
There exists a central discovery server on [Github](https://raw.githubusercontent.com/SecureTagForApproachRecognition/discovery/master/discovery.json). This server provides the necessary information for the SDK to initialize itself. After the SDK loaded the base url for its own backend it will load the infected list from there, as well as post if a user is infected.

The backend should hence gather all the infected list  from the other backends and provide a collected list from all sources. As long as the keys are generated with the SDK we can validate them accross different apps.

## Further Documentation

There exists a documentation repository in the [STAR](https://github.com/SecureTagForApproachRecognition) Organization. It includes Swager YAMLs for the backend API defintions, as well as some more technical details on how the keys are generated and how the validation mechanism works

## Function overview

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

