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

## Integrating into a Project

## Using the SDK

