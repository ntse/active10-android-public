
# Project Setup

## Configuration file

This project requires a `config.properties` file for configuration. To set up your local environment, follow these steps:

1. Create a new file named `config.properties` in the `app/` directory.
2. Add the following properties to the file:
### DEV Environment
DEV_APP_ENDPOINT, DEV_APP_ENDPOINT_V2, DEV_PARAGON_ENDPOINT, DEV_NHS_LOGIN_URL, DEV_NHS_UPDATE_URL, DEV_PARAGON_HEADER, DEV_PARAGON_TOKEN
### UAT Environment
UAT_APP_ENDPOINT, UAT_APP_ENDPOINT_V2, UAT_PARAGON_ENDPOINT, UAT_NHS_LOGIN_URL, UAT_NHS_UPDATE_URL, UAT_PARAGON_HEADER, UAT_PARAGON_TOKEN
### PROD Environment
PROD_APP_ENDPOINT, PROD_APP_ENDPOINT_V2, PROD_PARAGON_ENDPOINT, PROD_NHS_LOGIN_URL, PROD_NHS_UPDATE_URL, PROD_PARAGON_HEADER, PROD_PARAGON_TOKEN
### CERTIFICATE PINNING
CERTIFICATE_1, CERTIFICATE_2, CERTIFICATE_3, CERTIFICATE_4, CERTIFICATE_5

## Backend

We do not provide the backend code for this project.

## Google Services Configuration

This project requires a `google-services.json` file for Firebase configuration. To set up your local environment, follow these steps:

1. Obtain the `google-services.json` file from your Firebase project.
2. Place the file in the appropriate directory: `app/google-services.json`.

## Licensee Configuration

We use plugin licensee to validate and generate list of licences used by dependencies.
Configuration is available in app/build.gradle file.
Gradle task which generates list of licences is `:app:licensee`.
It generates files in `app/build/reports/licensee` directory.
After changing dependencies you should run this task and copy generated file from `app/build/reports/licensee/androidProdRelease` to `app/src/main/assets/licenses` directory.

More information on: https://github.com/cashapp/licensee
