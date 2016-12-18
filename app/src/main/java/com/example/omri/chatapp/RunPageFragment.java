package com.example.omri.chatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Omri on 18/12/2016.
 */

public class RunPageFragment extends Fragment implements View.OnClickListener {
    private TextView trainerNametxt;
    private TextView dateTimetxt;
    private TextView runLocationtxt;
    private TextView distancetxt;
    private TextView suitxt;
    private TextView leveltxt;
    private Button beTherebtn;
    public RunPageFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_run_page, container, false);
        trainerNametxt= (TextView)view.findViewById(R.id.trainer_name_txt);
        dateTimetxt= (TextView)view.findViewById(R.id.date_time_txt);
        runLocationtxt= (TextView)view.findViewById(R.id.run_location_text);
        distancetxt= (TextView)view.findViewById(R.id.distance_txt);
        suitxt= (TextView)view.findViewById(R.id.suit_txt);
        leveltxt= (TextView)view.findViewById(R.id.level_txt);
        beTherebtn= (Button)view.findViewById(R.id.be_there_btn);
        beTherebtn.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
