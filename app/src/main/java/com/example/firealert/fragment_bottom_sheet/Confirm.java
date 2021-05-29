

package com.example.firealert.fragment_bottom_sheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.firealert.HomeActivity;
import com.example.firealert.R;
import com.example.firealert.WarningActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Confirm extends BottomSheetDialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.activity_confirm,container,false);
        Button btn_Cancel=view.findViewById(R.id.btn_Cancel);
        Button btn_Confirm=view.findViewById(R.id.btn_Confirm);

        btn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onDestroyView();
            }
        });

        btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),HomeActivity.class);
                intent.putExtra("notification", View.VISIBLE);
                intent.putExtra("Class", "Confirm");
                startActivity(intent);
                onDestroyView();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}