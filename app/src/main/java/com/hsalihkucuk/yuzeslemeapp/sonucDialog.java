package com.hsalihkucuk.yuzeslemeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class sonucDialog extends DialogFragment {
    Button okBtn;
    TextView resultTextView;

    @Nullable
    @Override
    public View
    onCreateView(@NonNull LayoutInflater inflater,
                 @Nullable ViewGroup container,
                 @Nullable Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragmentsonucdialog, container,false);
        String resultText = "";

        okBtn = view.findViewById(R.id.result_ok_button);
        resultTextView = view.findViewById(R.id.result_text_view);

        Bundle bundle = getArguments();
        resultText = bundle.getString(YuzAlgilama.RESULT_TEXT);
        resultTextView.setText(resultText);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        return view;
    }
}
