package com.test.lifcare.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.test.lifcare.R;

/**
 * Created by rajatbeck on 3/25/2017.
 */

public class BottomEditSheet extends BottomSheetDialogFragment {

    public BottomEditSheet() {
        updateClicked = false;
    }

    private static OnUpdate onUpdate;
    private static boolean updateClicked;

    public BottomEditSheet getInstance(String name, String number) {
        BottomEditSheet bottomEditSheet = new BottomEditSheet();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("number", number);
        bottomEditSheet.setArguments(bundle);
        return bottomEditSheet;
    }

    public static void setOnUpdate(OnUpdate onUpdate) {
        BottomEditSheet.onUpdate = onUpdate;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                onUpdate.update(updateClicked, name.getText().toString().trim(), number.getText().toString().trim());
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private TextInputLayout nameInputLayout, numberInputLayout;
    private EditText name, number;
    private Button btnUpdate;
    private String oldName, oldNumber;
    private TextView cancel;

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_list_fragment, null);
        dialog.setContentView(contentView);

        Bundle bundle = getArguments();
        oldName = bundle.getString("name");
        oldNumber = bundle.getString("number");

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        nameInputLayout = (TextInputLayout) contentView.findViewById(R.id.input_layout_name);
        numberInputLayout = (TextInputLayout) contentView.findViewById(R.id.input_layout_number);
        name = (EditText) contentView.findViewById(R.id.input_name);
        number = (EditText) contentView.findViewById(R.id.input_number);
        btnUpdate = (Button) contentView.findViewById(R.id.btn_update);
        cancel = (TextView) contentView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onUpdate.update(updateClicked, name.getText().toString().trim(), number.getText().toString().trim());
                dismiss();
            }
        });

        name.setText(oldName);
        number.setText(oldNumber);

        //Set callback
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateName()) {
                    if (validateNumber()) {
                        dismiss();
                        updateClicked = true;
                    }
                }
            }
        });
    }

    private boolean validateName() {
        if (name.getText().toString().isEmpty()) {
            nameInputLayout.setError("Please enter name");
            requestFocus(name);
            return false;

        } else {
            nameInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNumber() {
        if (number.getText().toString().isEmpty()) {
            numberInputLayout.setError("Please enter number");
            requestFocus(number);
            return false;

        } else {
            numberInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public interface OnUpdate {
        Void update(boolean updt, String name, String number);
    }
}

