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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.content.SharedPreferences;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.util.scorpion.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;

import com.android.settings.SettingsPreferenceFragment;
import com.nest.settings.preferences.IconPackPreference;

public class RecentsSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String RECENTS_CLEAR_ALL_LOCATION = "recents_clear_all_location";
    private static final String RECENTS_COMPONENT_TYPE = "recents_component";
    private static final String IMMERSIVE_RECENTS = "immersive_recents"; 
    private static final String RECENTS_DATE = "recents_full_screen_date"; 
    private static final String RECENTS_CLOCK = "recents_full_screen_clock";

    private ListPreference mRecentsClearAllLocation;
    private SwitchPreference mRecentsClearAll;
    private ListPreference mRecentsComponentType;
    private ListPreference mImmersiveRecents;
    private SwitchPreference mClock;
    private SwitchPreference mDate;

    private SharedPreferences mPreferences; 
    private Context mContext;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.settings_recents);

        ContentResolver resolver = getActivity().getContentResolver();
        mContext = getActivity().getApplicationContext();

        // clear all recents
        mRecentsClearAllLocation = (ListPreference) findPreference(RECENTS_CLEAR_ALL_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.RECENTS_CLEAR_ALL_LOCATION, 3, UserHandle.USER_CURRENT);
        mRecentsClearAllLocation.setValue(String.valueOf(location));
        mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntry());
        mRecentsClearAllLocation.setOnPreferenceChangeListener(this);

        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(resolver,
                Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);

        // immersive recents
        mImmersiveRecents = (ListPreference) findPreference(IMMERSIVE_RECENTS);
        int mode = Settings.System.getInt(getContentResolver(),
        Settings.System.IMMERSIVE_RECENTS, 0);
            mImmersiveRecents.setValue(String.valueOf(mode));
        mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
        mImmersiveRecents.setOnPreferenceChangeListener(this);

        mClock = (SwitchPreference) findPreference(RECENTS_CLOCK);
        mDate = (SwitchPreference) findPreference(RECENTS_DATE);
        updateDisablestate(mode);

        IconPackPreference iconPackPref = (IconPackPreference) findPreference("recents_icon_pack");
        // Re-initialise preference
        iconPackPref.init();
    }

    public void updateDisablestate(int mode) { 
        if (mode == 0 || mode == 2) { 
           mClock.setEnabled(false); 
           mDate.setEnabled(false); 
        } else { 
           mClock.setEnabled(true); 
           mDate.setEnabled(true); 
        } 
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsClearAllLocation) {
            int location = Integer.valueOf((String) objValue);
            int index = mRecentsClearAllLocation.findIndexOfValue((String) objValue);
            Settings.System.putIntForUser(getActivity().getContentResolver(),
                    Settings.System.RECENTS_CLEAR_ALL_LOCATION, location, UserHandle.USER_CURRENT);
            mRecentsClearAllLocation.setSummary(mRecentsClearAllLocation.getEntries()[index]);
            return true;
        } else if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) objValue);
            int index = mRecentsComponentType.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            if (type == 1) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            Utils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mImmersiveRecents) {
            int mode = Integer.valueOf((String) objValue); 
            Settings.System.putIntForUser(getActivity().getContentResolver(), Settings.System.IMMERSIVE_RECENTS,
                    Integer.parseInt((String) objValue), UserHandle.USER_CURRENT);
            mImmersiveRecents.setValue((String) objValue);
            mImmersiveRecents.setSummary(mImmersiveRecents.getEntry());
            updateDisablestate(mode);
            mPreferences = mContext.getSharedPreferences("recent_settings", Activity.MODE_PRIVATE);
            if (!mPreferences.getBoolean("first_info_shown", false) && objValue != null) {
                getActivity().getSharedPreferences("recent_settings", Activity.MODE_PRIVATE)
                        .edit()
                        .putBoolean("first_info_shown", true)
                        .commit();
                openAOSPFirstTimeWarning();
            }
            return true;
        }
        return false;
    }

    private void openAOSPFirstTimeWarning() { 
        new AlertDialog.Builder(getActivity()) 
                .setTitle(getResources().getString(R.string.aosp_first_time_title)) 
                .setMessage(getResources().getString(R.string.aosp_first_time_message)) 
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
                        public void onClick(DialogInterface dialog, int whichButton) { 
                        } 
                }).show(); 
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCORPION;
    }
}
