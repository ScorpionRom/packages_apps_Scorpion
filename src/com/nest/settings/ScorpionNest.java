/*
 * Copyright (C) 2018 Scorpion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nest.settings;

import com.android.internal.logging.nano.MetricsProto;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Surface;
import android.preference.Preference;
import com.android.settings.R;

import com.android.settings.SettingsPreferenceFragment;
import com.nest.settings.preferences.Utils;

public class ScorpionNest extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        final String KEY_DEVICE_PART = "device_extras_category";
        final String KEY_DEVICE_PART_PACKAGE_NAME = "com.dirtyunicorns.settings.device";
        addPreferencesFromResource(R.xml.scorpion);

        // Extras
        if (!Utils.isPackageInstalled(getActivity(), KEY_DEVICE_PART_PACKAGE_NAME)) {
            getPreferenceScreen().removePreference(findPreference(KEY_DEVICE_PART));
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.SCORPION;
    }
}
