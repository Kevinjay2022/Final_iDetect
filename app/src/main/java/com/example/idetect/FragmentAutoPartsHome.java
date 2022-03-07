package com.example.idetect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


public class FragmentAutoPartsHome extends Fragment {

    private CardView DashBoardBTN, CustomersBTN;
    private LinearLayout DashOut, CustOut;
    public FragmentAutoPartsHome(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragAutoPartsHome = inflater.inflate(R.layout.fragment_auto_parts_home, container, false);

        DashBoardBTN = fragAutoPartsHome.findViewById(R.id.DashboardCardBTN);
        CustomersBTN = fragAutoPartsHome.findViewById(R.id.customerCardBTN);
        DashOut = fragAutoPartsHome.findViewById(R.id.dashboardExpandable);
        CustOut = fragAutoPartsHome.findViewById(R.id.customerExpandableView);

        DashBoardBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DashOut.getVisibility()==View.GONE){
                    DashOut.setVisibility(View.VISIBLE);
                }else{
                    DashOut.setVisibility(View.GONE);
                }
            }
        });
        CustomersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CustOut.getVisibility()==View.GONE){
                    CustOut.setVisibility(View.VISIBLE);
                }else{
                    CustOut.setVisibility(View.GONE);
                }
            }
        });



        return fragAutoPartsHome;
    }

}