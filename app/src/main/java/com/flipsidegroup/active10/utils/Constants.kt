package com.flipsidegroup.active10.utils

import java.util.*



object Constants {

    const val DEFAULT_FONT_SCALE = 1.0f
    const val MAX_FONT_SCALE = 1.3f

    const val WIDTH_MARGIN_MULTIPLIER = 0.3

    const val PRIVACY_POLICY = "https://www.nhs.uk/oneyou/privacy-policy#Pb7uz120PbrKeV1o.97"
    const val TERMS_AND_CONDITIONS =
        "https://www.nhs.uk/oneyou/apps-terms-and-conditions#161QJmuKK0mFd8rG.97"
    const val MIGRATION_FILE_NAME = "activities.json"
    const val DEVICE_OS = "android"

    const val ANOTHER_REASON_ID = 0

    const val SEPTEMBER_2019_TIMESTAMP = 1567296000000
    const val DEVICE_ANDROID = "android"
    const val IN_IS_NEW_USER = "IN_IS_NEW_USER"
    const val SIGN_IN_MAIN_CONTENT = "sign_in_main_content"
    const val SIGN_IN_INFO_CONTENT = "sign_in_info_content"
    const val STAY_UPDATED_CONTENT = "stay_updated_content"
    const val IS_ONBOARDING = "is_onboarding"
    const val FLOW_TYPE = "flow_type"
    const val IS_WHITE_TOOLBAR = "is_white_toolbar"
    const val IS_SIGN_IN_FLOW_ROOT = "IS_SIGN_IN_FLOW_ROOT"
    const val OPEN_WITH_WEB_VIEW = "OPEN_WITH_WEB_VIEW"
    const val PARENT_STEP_TYPE = "parent_step_type"
    const val IS_LOGIN_SUCCESS = "is_login_success"

    object Instruction {
        const val INSTRUCTION_WALK_MIN = 8
        const val INSTRUCTION_BRISK_MIN = 2
    }

    object Paragon {
        const val HEADER_KEY = "x-dst-oneyou"
    }

    object FirebaseAnalytics {
        const val BRISK_MODULE_ANALYTICS = "brisk_module_analytics"
        const val INITIALIZE_BRISK_COUNTER_FAILURE = "com.phe.active10.failure.brisk.init"
        const val RETRIEVE_TODAY_ACTIVITY_FAILURE = "com.phe.active10.failure.todays.activity.get"
        const val RETRIEVE_PAST_ACTIVITY_FAILURE = "com.phe.active10.failure.past.activity.get"
        const val RETRIEVE_LOST_ACTIVITY_FAILURE = "com.phe.active10.failure.lost.activity.try"
        const val REGISTER_TO_SENSORS_FOR_LIVE_CADENCE =
            "com.phe.active10.failure.sensor.registration"
        const val LOAD_LOTTIE_ANIMATION_FAILURE = "load_lottie_animation"

        const val DEVICE_ID = "device_id"
        const val APP_VERSION = "app_version"
        const val OS = "os"
        const val ACCOUNT_TYPE = "account_type"
        const val ACCOUNT_TYPE_NHS = "nhs_login"
        const val ACCOUNT_TYPE_GUEST = "guest"
        const val REWARD_SLUG = "reward_slug"
        const val EARNED_REWARD_EVENT = "earned_reward"

        const val EVENT_APP_UPDATE = "app_update_popup_shown"
        const val EVENT_APP_UPDATE_CONFIRMED = "app_update_popup_cancelled"
        const val EVENT_APP_UPDATE_CANCELLED = "app_update_popup_confirmed"

        const val EVENT_ABOUT_YOU = "about_you"
        const val EVENT_ABOUT_YOU_SKIPPED = "about_you_skipped"
        const val USER_GENDER_MALE = "M"
        const val USER_GENDER_FEMALE = "F"
        const val KEY_GENDER = "gender"
        const val KEY_ACTIVITY_LEVEL = "activity_level"
        const val KEY_AGE = "age"

        const val EVENT_DAILY_TARGET = "daily_target"
        const val EVENT_USER_GOAL = "users_goals"
        const val KEY_TARGET = "target"
        const val KEY_CREATED_AT = "created_at"
        const val KEY_GOALS = "goals"
        const val KEY_GOAL = "goal"
        const val CUSTOM_GOAL = "users_custom_goal"

        const val EVENT_APP = "app_event"
        const val KEY_EVENT_VALUE = "event_value"
        const val KEY_EVENT = "event"

        const val EVENT_ACTIVITIES_PER_HOUR = "activities_per_hour"
        const val EVENT_HISTORIC_ACTIVITIES_PER_HOUR = "historic_activities_per_hour"
        const val KEY_LATITUDE = "lat"
        const val KEY_LONGITUDE = "lng"
        const val KEY_DATE = "date"
        const val KEY_NON_BRISK_MINUTE = "non_brisk_minute"
        const val KEY_BRISK_MINUTE = "brisk_minute"
        const val HISTORICAL_RANGE = "historical_range"

        const val WIDGETS = "widgets_installed"
        const val SMALL_WIDGETS = "small_widget"
        const val MEDIUM_WIDGETS = "medium_widget"
        const val SMALL = "small"
        const val SMALL_DIFF = "small_diff"
        const val MEDIUM = "medium"
        const val UPDATE_SMALL_WIDGETS = "small_update"
        const val UPDATE_MEDIUM_WIDGETS = "medium_update"
        const val MEDIUM_DIFF = "medium_diff"
        const val WIDGET_INSTALLED_KEY = "widgets_installed"

        const val EVENT_ARTICLE_READ = "article_read"
        const val EVENT_POPUP_CANCELED = "popup_canceled"
        const val ARTICLE_TITLE = "article_title"
        const val ARTICLE_SLUG = "article_slug"
        const val SOURCE = "source"
        const val SLUG = "slug"

        const val EVENT_DISCOVER_SEARCH = "discover_search"
        const val PARAM_SEARCH_TERM = "search_term"
        const val PARAM_RESULT_NUMBER = "result_number"

        const val EVENT_NOTIFICATION_SENT = "notification_sent"
        const val EVENT_NOTIFICATION_TAPPED = "notification_tapped"
        const val PARAM_NOTIFICATION_NAME = "notification_name"

    }

    object CMSAnalytics {
        const val CMS_FAILURE = "com.phe.active10.failure.cms"
    }

    object RetrieveLostData {
        const val NUMBER_OF_RETRIEVE_DATA_TRIES = 3
        const val RETRIEVE_LOST_DATA_YEAR = 2021
        const val RETRIEVE_LOST_DATA_MONTH = Calendar.JANUARY
        const val RETRIEVE_LOST_DATA_DAY = 5
    }

    object CoachAppIntroduction {
        const val COACH_PACKAGE_NAME = "com.phe.couchto5K"
        const val POPUP_SMASHING_IT = "smashing_it"
        const val POPUP_RUNNING_NOT_FOR_ME = "running_not_for_me"
        const val POPUP_C25K_NO_THANKS = "c25k_no_thanks"
        const val POPUP_DOWNLOAD_C25K = "download_c25k"
    }

    object ScreenInfoParameters {
        const val FIRST_BUTTON_TITLE = "first_button_title"
        const val SECOND_BUTTON_TITLE = "second_button_title"
        const val INTERNAL_LINK = "internal_link"
        const val EXTERNAL_LINK = "external_link"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }

    object NHSLogin {
        const val EMAIL_PREFERENCES_NAME = "active10_mailing_list"
    }
}