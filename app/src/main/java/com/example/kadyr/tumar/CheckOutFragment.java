package com.example.kadyr.tumar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.kadyr.tumar.DataRepository.Client;
import com.example.kadyr.tumar.DataRepository.Room;


import java.util.Date;

public class CheckOutFragment extends android.app.DialogFragment implements View.OnClickListener {


    private OnCheckOutFragmentListener mListener;

    Room room;
    View v;
    Date dt = new Date();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_check_out, null);



        Button btnOK = v.findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoCheckOut();
                CloseFragment();
            }});
        Button btnCancel = v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseFragment();

            }});


        String idRoom = getArguments().getString("idRoom");
        room = Room.GetRoom(Integer.valueOf(idRoom));



        TextView tv = v.findViewById(R.id.dateIn);
        tv.setText(String.valueOf(dt.getDate()) + "/" + (dt.getMonth()+1) + "/" + (dt.getYear()+1900));



        TextView vd = v.findViewById(R.id.viewTitle);
        vd.setText("Выселение.  Комната - "+room.getName());
        TextView dateIn = v.findViewById(R.id.dateIn ) ;
        dateIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatePickerDialog tpd = new DatePickerDialog(getActivity(), myCallBack,
                        dt.getYear()+1900, dt.getMonth(), dt.getDate());
                tpd.show();
            }
        });
        return v;
    }

    interface OnCheckOutFragmentListener {

        void onFloorUpdate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCheckOutFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }



    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            TextView tv = v.findViewById(R.id.dateIn);
            tv.setText(String.valueOf(dayOfMonth) + "/" + monthOfYear + "/" + year);

        }
    };


    public void onClick(View v) {
    }


    public void DoCheckOut(){
        if(room !=null)
            room.CheckOut(PublicVariables.CurrentUser.GetId(),new Date());
        mListener.onFloorUpdate();
    }
    public void CloseFragment(){
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}