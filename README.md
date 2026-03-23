
# Project Setup

## Demo Fork Setup

This fork includes a checked-in [config.properties](/Users/sam/repos/active10-android-public/app/config.properties) with placeholder values so it can be built as a standalone PKCE demo without access to the original private backend configuration.

If you want the login flow to work end-to-end, update the `DEV_*` values in [config.properties](/Users/sam/repos/active10-android-public/app/config.properties) to point to your own demo backend.

The installable app IDs in this fork are:

- `dev.active10.pkce`
- `dev.active10.pkce.uat`
- `dev.active10.pkce.prod`

## Backend

We do not provide the backend code for this project.

## Google Services Configuration

This fork removes the Firebase Gradle plugin requirement and does not need `app/google-services.json` to compile.

## Licensee Configuration

We use plugin licensee to validate and generate list of licences used by dependencies.
Configuration is available in app/build.gradle file.
Gradle task which generates list of licences is `:app:licensee`.
It generates files in `app/build/reports/licensee` directory.
After changing dependencies you should run this task and copy generated file from `app/build/reports/licensee/androidProdRelease` to `app/src/main/assets/licenses` directory.

More information on: https://github.com/cashapp/licensee
