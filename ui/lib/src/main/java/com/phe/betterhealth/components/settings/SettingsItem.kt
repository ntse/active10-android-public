package com.phe.betterhealth.components.settings

enum class SettingsViewType {
    SECTION,
    MENU_ITEM,
    DESCRIPTION,
    SWITCH,
    CHECKBOX,
    ABOUT,
    VERSION,
    RESET_BUTTON
}

sealed interface SettingsItem {
    val type: SettingsViewType
    val id: Int
}

data class SettingsSection(
    override val id: Int,
    val title: String,
    val value: String? = null,
    val callback: (() -> Unit)? = null,
    override val type: SettingsViewType = SettingsViewType.SECTION,
) : SettingsItem

data class SettingsMenuItem(
    override val id: Int,
    val name: String,
    val value: String? = null,
    val valueSecond: String? = null,
    val callback: () -> Unit,
    val divider: Boolean = true,
    val description: String? = null,
    val enabled: Boolean = true,
    val markItem: Boolean = false,
    val contentDescription: String? = null,
    val subtitle: String? = null,
    override val type: SettingsViewType = SettingsViewType.MENU_ITEM,
    val isTitleBold: Boolean = false,
) : SettingsItem

data class SettingsMenuDescriptionItem(
    override val id: Int,
    val description: String,
    val callback: () -> Unit,
    override val type: SettingsViewType = SettingsViewType.DESCRIPTION,
    val showTopMargin: Boolean = false
) : SettingsItem

data class SettingsMenuSwitchItem(
    override val id: Int,
    val name: String,
    val value: Boolean,
    val callback: (checked: Boolean) -> Unit,
    override val type: SettingsViewType = SettingsViewType.SWITCH,
    val description: String? = null,
    val enabled: Boolean = true,
    val isDividerVisible: Boolean = true,
    val isTitleBold: Boolean = false,
    val isTextSwitchStateVisible: Boolean = false,
) : SettingsItem

data class SettingsMenuCheckboxItem(
    override val id: Int,
    val name: String,
    val value: Boolean,
    val callback: (checked: Boolean) -> Unit,
    val enabled: Boolean = true,
    override val type: SettingsViewType = SettingsViewType.CHECKBOX,
) : SettingsItem

data class SettingsAbout(
    override val id: Int,
    val header: String,
    val content: String,
    val termsButton: String,
    val termsCallback: () -> Unit,
    val policyButton: String,
    val policyCallback: () -> Unit,
    val accessibilityButton: String,
    val accessibilityCallback: () -> Unit,
    override val type: SettingsViewType = SettingsViewType.ABOUT,
) : SettingsItem

data class SettingsVersion(
    override val id: Int,
    val name: String,
    val code: Int,
    override val type: SettingsViewType = SettingsViewType.VERSION,
    val callback: (() -> Unit)? = null,
) : SettingsItem

data class SettingsResetButton(
    override val id: Int,
    val buttonLabel: String,
    val buttonDescription: String? = null,
    val buildVersion: String? = null,
    val callback: () -> Unit,
    override val type: SettingsViewType = SettingsViewType.RESET_BUTTON,
) : SettingsItem
