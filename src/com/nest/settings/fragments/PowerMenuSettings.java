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

import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;
import com.android.settings.R;
import android.support.annotation.NonNull;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;


public class PowerMenuSettings extends SettingsPreferenceFragment
                implements Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKDOWN_IN_POWER_MENU = "lockdown_in_power_menu";
    private static final int MY_USER_ID = UserHandle.myUserId();

    private SwitchPreference mPowerMenuLockDown;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.settings_power);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

        final PreferenceScreen prefSet = getPreferenceScreen();
        final LockPatternUtils lockPatternUtils = new LockPatternUtils(getActivity());

        mPowerMenuLockDown = (SwitchPreference) findPreference(KEY_LOCKDOWN_IN_POWER_MENU);
        if (lockPatternUtils.isSecure(MY_USER_ID)) {
            mPowerMenuLockDown.setChecked((Settings.Secure.getInt(getContentResolver(),
                    Settings.Secure.LOCKDOWN_IN_POWER_MENU, 0) == 1));
            mPowerMenuLockDown.setOnPreferenceChangeListener(this);
        } else {
            prefSet.removePreference(mPowerMenuLockDown);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mPowerMenuLockDown) {
            boolean value = (Boolean) objValue;
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.LOCKDOWN_IN_POWER_MENU, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCORPION;
    }
}
