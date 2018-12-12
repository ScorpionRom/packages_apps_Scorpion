/*
 *  Copyright (C) 2018 Scorpion
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package com.nest.settings.fragments;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;

public class NavigationBar extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String KEY_PULSE_SETTINGS = "pulse_settings";
    private static final String KEY_STOCK_NAVBAR = "stock_navbar";
    private static final String KEY_NAVIGATION_BAR_ENABLED = "navigation_bar";

    private PreferenceScreen mPulseSettings;
    private Preference mStockNavbar;
    private SwitchPreference mNavigationBar;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.settings_navigation);

        mPulseSettings = (PreferenceScreen) findPreference(KEY_PULSE_SETTINGS);

        mStockNavbar = (Preference) findPreference(KEY_STOCK_NAVBAR);

        mNavigationBar = (SwitchPreference) findPreference(KEY_NAVIGATION_BAR_ENABLED);
        final boolean defaultToNavigationBar = getResources().getBoolean(
                com.android.internal.R.bool.config_defaultToNavigationBar);
        mNavigationBar.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_ENABLED,
                defaultToNavigationBar ? 1 : 0) == 1));
        mNavigationBar.setOnPreferenceChangeListener(this);

        updateNavbar();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavigationBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_ENABLED, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCORPION;
    }

    private void updateNavbar() {
        boolean swipeUpGesture = Settings.Secure.getInt(getActivity().getContentResolver(),
                Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0) != 0;
        if (!swipeUpGesture) {
            mStockNavbar.setEnabled(true);
            mNavigationBar.setEnabled(true);
            mStockNavbar.setSummary(R.string.systemui_tuner_navbar_enabled_summary);
        } else {
            mStockNavbar.setEnabled(false);
            mNavigationBar.setEnabled(true);
            mStockNavbar.setSummary(R.string.systemui_tuner_navbar_disabled_summary);
        }
    }
}
