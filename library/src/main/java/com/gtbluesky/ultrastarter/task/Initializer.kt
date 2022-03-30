package com.gtbluesky.ultrastarter.task

import android.content.Context

interface Initializer<T> {

    fun create(context: Context): T

    fun dependencies(): List<Class<out Initializer<*>>>?

    fun dependenciesCount(): Int

    fun needPrivacyGrant(): Boolean
}