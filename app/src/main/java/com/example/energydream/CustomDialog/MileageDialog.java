package com.example.energydream.CustomDialog;

/**
 * Created by 이명남 on 2018-12-27.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.energydream.R;

public class MileageDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_milge;
    private Context context;
    private Button fin_Btn;
    private TextView tx_milege;
    private double milege;

    public MileageDialog(@NonNull Context context, double milege) {
        super(context);
        this.context = context;
        this.milege = milege;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);


        tx_milege = (TextView)findViewById(R.id.tx_milge_tx);
        tx_milege.setText(String.format("%.2f", milege) + " 마일리지를 적립했습니다.");
        fin_Btn = (Button)findViewById(R.id.btn_milge);
        fin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
    }

}
