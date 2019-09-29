package com.exaphis.avta.fragments;

import android.content.Context;

import com.exaphis.avta.TimeDurationPickerCallback;

import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;
import mobi.upod.timedurationpicker.TimeDurationPicker;

public class PickerDialogFragment extends TimeDurationPickerDialogFragment {
    TimeDurationPickerCallback callback;

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        if (ctx instanceof TimeDurationPickerCallback) {
            callback = (TimeDurationPickerCallback) ctx;
        }
        else {
            throw new RuntimeException(ctx.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    protected long getInitialDuration() {
        return 0;
    }

    @Override
    protected int setTimeUnits() {
        return TimeDurationPicker.HH_MM;
    }

    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {
        callback.updateDuration(duration);
    }
}
