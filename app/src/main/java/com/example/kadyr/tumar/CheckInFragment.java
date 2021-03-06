package com.example.kadyr.tumar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kadyr.tumar.DataRepository.Checkining;
import com.example.kadyr.tumar.DataRepository.Client;
import com.example.kadyr.tumar.DataRepository.Room;
import com.example.kadyr.tumar.DataRepository.RoomPrice;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckInFragment extends android.app.DialogFragment implements View.OnClickListener {


    List<Client> clients ;
    ClientAdapter clientAdapter;
    EditText clientName ;
    EditText dayCount;
    ListView clientList;
    int Regim;
    int idEditingCheckining;
    RoomPrice actualPrice ;
    Room room;
    View v;
    Date dt = new Date();


    private OnCheckInFragmentListener mListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chaeck_in, null);
        Button btnOK = v.findViewById(R.id.btnOk);
        btnOK.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(Regim==Constants.RegimAdd)  DoCheckIn();
                                                else EditCheckin();
                                                CloseFragment();
                                            }});
        Button btnCancel = v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseFragment();

            }});

        int idRoom = getArguments().getInt("idRoom");
        Log.d("axa",String.valueOf(idRoom));
        room = Room.GetRoom(idRoom);
        TextView vd = v.findViewById(R.id.viewTitle);
        vd.setText("Заселение.  Комната - "+room.getName());
        TextView dateIn = v.findViewById(R.id.dateIn ) ;
        dayCount=v.findViewById(R.id.dayCount);

        dateIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                       DatePickerDialog tpd = new DatePickerDialog(getActivity(), myCallBack,
                        dt.getYear()+1900, dt.getMonth(), dt.getDate());
        tpd.show();
            }
        });

        if(getArguments().getInt(Constants.RegimKey)==Constants.RegimAdd){
            Regim=Constants.RegimAdd;
            dateIn.setText(String.valueOf(dt.getDate()) + "/" + (dt.getMonth()+1) + "/" + (dt.getYear()+1900));
            dayCount.setText("3") ;
        } else{
            Regim = Constants.RegimEdit;
            Checkining lastCheckin = Checkining.GetLastCheckining(idRoom);
            idEditingCheckining = lastCheckin.getId();
            dt = lastCheckin.getDateCheckin();
            dateIn.setText(String.valueOf(dt.getDate()) + "/" + (dt.getMonth()+1) + "/" + (dt.getYear()+1900));

            dayCount.setText(String.valueOf(lastCheckin.getDayCount()));

            EditText totalSum = v.findViewById(R.id.totalSum);
            totalSum.setText(String.valueOf(lastCheckin.getSum()));

            EditText paidSum = v.findViewById(R.id.paidSum);
            paidSum.setText(String.valueOf(lastCheckin.getSum()-lastCheckin.getDebt()));

            String nameClient = Client.GetClient(lastCheckin.getIdClient()).getName();
            EditText etNameClient = v.findViewById(R.id.nameClient);
            etNameClient.setText(nameClient);
        }

        clientName = v.findViewById(R.id.nameClient) ;
        clientList = v.findViewById(R.id.clientList);
        clientList.setVisibility(View.INVISIBLE);
        actualPrice = RoomPrice.GetActualRoomPrice(room.getIdRoomType(),
                CommonFunctions.StringToDate(dateIn.getText().toString()));
        SetTotalSum();
        return v;
    }

    interface OnCheckInFragmentListener {

        void onFloorUpdate();

    }


    @Override
    public void onResume() {
        super.onResume();
        try {

            clients = Client.GetClients();
            clientAdapter = new ClientAdapter(getActivity(), clients);
            if(!clientName.getText().toString().isEmpty())
                clientAdapter.getFilter().filter(clientName.getText().toString());

            // установка слушателя изменения текста
            clientName.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) { }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    clients=Client.GetFilteredArray(s.toString());
                    clientAdapter.SetClients(clients);
                    clientAdapter.getFilter().filter(s.toString());
                }
            });

            clientName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                         clientList.setVisibility(hasFocus? View.VISIBLE:
                                                            View.INVISIBLE);
                }
            });

            clientList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    Client selectedItem = (Client) parent.getItemAtPosition(position);

                    // Display the selected item text on TextView
                    clientName.setText(selectedItem.getName());
                }
            });
            dayCount.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    SetTotalSum();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });


            clientList.setAdapter(clientAdapter);
        }
        catch (SQLException ex){}
    }


    private void SetTotalSum(){
        EditText totalSum = v.findViewById(R.id.totalSum);
        String dayCountString = dayCount.getText().toString();
        if(dayCountString.isEmpty()) totalSum.setText("0") ;
        else
            totalSum.setText(String.valueOf(actualPrice.Price*Integer.valueOf(dayCountString)));
        if(Regim==Constants.RegimAdd){
            EditText paidSum = v.findViewById(R.id.paidSum);
            paidSum.setText(totalSum.getText().toString());
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCheckInFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnCheckInFragmentListener");
        }
    }



    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            TextView tv = v.findViewById(R.id.dateIn);
            String s = String.valueOf(dayOfMonth) + "/" + monthOfYear + "/" + year;
            tv.setText(s);
            actualPrice = RoomPrice.GetActualRoomPrice(room.getIdRoomType(),CommonFunctions.StringToDate(s));
            SetTotalSum();
        }
    };


    public void onClick(View v) {

    }

    public void EditCheckin(){
        TextView tv = v.findViewById(R.id.dateIn);
        Date docDate = CommonFunctions.StringToDate(tv.getText().toString());
        EditText etDayCnt = v.findViewById(R.id.dayCount);
        int dayCount = Integer.valueOf(etDayCnt.getText().toString());

        EditText etTotalSum = v.findViewById(R.id.totalSum);
        double sumTotal = Double.valueOf(etTotalSum.getText().toString());

        EditText etPaidSum = v.findViewById(R.id.paidSum);
        double sumPaid = Double.valueOf(etPaidSum.getText().toString());

        EditText etNameClient = v.findViewById(R.id.nameClient);
        String nameClient = etNameClient.getText().toString();

        if(room !=null)
            try {
                room.EditCheckin(idEditingCheckining, docDate, dayCount, sumTotal, sumPaid, nameClient);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        mListener.onFloorUpdate();
    }


    public void DoCheckIn(){

        TextView tv = v.findViewById(R.id.dateIn);
        String s=tv.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        Date docDate = null;
        try{
            docDate= format.parse(s);
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }

        EditText etDayCnt = v.findViewById(R.id.dayCount);
        int dayCount = Integer.valueOf(etDayCnt.getText().toString());

        EditText etTotalSum = v.findViewById(R.id.totalSum);
        double sumTotal = Double.valueOf(etTotalSum.getText().toString());

        EditText etPaidSum = v.findViewById(R.id.paidSum);
        double sumPaid = Double.valueOf(etPaidSum.getText().toString());

        EditText etNameClient = v.findViewById(R.id.nameClient);
        String nameClient = etNameClient.getText().toString();

        if(room !=null)
            try {
                room.DoCheckin(docDate, dayCount, sumTotal, sumPaid, nameClient);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
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