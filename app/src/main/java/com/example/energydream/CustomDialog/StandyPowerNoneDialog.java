package com.example.energydream.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.example.energydream.R;

public class StandyPowerNoneDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_standpower_exist;
    private Context context;
    private Button btn_fin;

    public StandyPowerNoneDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        btn_fin = (Button)findViewById(R.id.btn_standy_None);
        btn_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }


}
