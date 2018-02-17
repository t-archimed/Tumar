package com.example.kadyr.tumar;

import android.app.DialogFragment;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.kadyr.tumar.DataRepository.Client;

import java.util.List;

public class Clients extends AppCompatActivity implements AddClientFragment.onClickOK {

    ClientAdapter clientAdapter;
    SearchView clientName;
    List<Client> clients;
    ListView clientsList;

    public void setClientsList(){
        clients = Client.GetClients();
        clientAdapter.SetClients(clients);
        clientAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);


        clientsList = findViewById(R.id.clientsList);
        clientName = findViewById(R.id.searchCLient);
        List<Client> clients = Client.GetClients();
        clientAdapter = new ClientAdapter(this, clients);

        clientsList.setAdapter(clientAdapter);
    }



    @Override
    protected void onResume(){
        super.onResume();
        try{

            clients = Client.GetClients();
            Log.e("axa", "onResume-"+String.valueOf(clients.size()));
            if(!clientName.getQuery().toString().isEmpty())
                clientAdapter.getFilter().filter(clientName.getQuery().toString());

            // установка слушателя изменения текста
            clientName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
// do something on text submit
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    clients=Client.GetFilteredArray(s.toString());
                    clientAdapter.getFilter().filter(s.toString());

// do something when text changes
                    return false;
                }
            });


            clientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected item text from ListView
                    Client selectedItem = (Client) parent.getItemAtPosition(position);

                    // Display the selected item text on TextView

                }
            });


            clientsList.setAdapter(clientAdapter);
        }catch (Exception ex){

        }
    }

    public void OnClickAddCLient(View view){
        AddClientFragment newClient = new AddClientFragment();
        newClient.show(getFragmentManager(), "newClient");
    }


}
