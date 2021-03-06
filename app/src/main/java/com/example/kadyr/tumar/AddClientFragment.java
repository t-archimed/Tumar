package com.example.kadyr.tumar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kadyr.tumar.DataRepository.Client;

import java.io.File;
import java.io.IOException;

/**
 * Created by Kadyr on 2/17/2018.
 */

public class AddClientFragment extends android.app.DialogFragment implements View.OnClickListener {

    private onClickOK mListener;
    ImageView avatar;
    final int DefaultTag = 0;
    final int ChangedTag = 1;

    public interface onClickOK{
        public void setClientsList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (AddClientFragment.onClickOK) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " должен реализовывать интерфейс OnFragmentInteractionListener");
        }
    }

    View v;
    int idEditingClient;
    String nameEditingClient;
    Bitmap oldPicture=null;
    boolean Regim;
    final boolean Add = false;
    final boolean Edit = true;
    final int CameraCode = 15;
    final int GalleryCode = 25;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_add_client, null);

        Button btnOk = v.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        Button btnCancel = v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        avatar = v.findViewById(R.id.pictureClient);
        avatar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CameraCode);
            }
        });

        ImageView gallery = v.findViewById(R.id.pictureFromGallery);
        gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GalleryCode);
            }
        });


        avatar.setTag(R.drawable.ic_camera_alt_black_24dp);

        if(getArguments()!=null) {

            nameEditingClient = getArguments().getString("nameClient");
            Regim=Edit;
            Client client = Client.GetClientbyName(nameEditingClient);
            idEditingClient = client.getId();
            Bitmap bmp= client.getPicture();
            if(bmp!=null) {
                avatar.setImageBitmap(bmp);
                oldPicture=bmp;
                avatar.setTag(null);
            }

            EditText name = v.findViewById(R.id.nameClient);
            EditText telephone = v.findViewById(R.id.telephoneClient);
            name.setText(client.getName());
            telephone.setText(client.getTelephone());

        } else{
            Regim=Add;
        }



        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CameraCode){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            avatar.setImageBitmap(bitmap);
            avatar.setTag(null);
        }
        if(requestCode==GalleryCode){
            Uri uri = data.getData();

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            avatar.setImageBitmap(bitmap);

        }


    }

    public void onClick(View v1){

            if(v1.getId()==R.id.btnOk){
                if(Regim==Add){
                String name = ((EditText)v.findViewById(R.id.nameClient)).getText().toString();
                String telephone = ((EditText)v.findViewById(R.id.telephoneClient)).getText().toString();
                ImageView imageView = v.findViewById(R.id.pictureClient);
                Bitmap picture=null;
                if(imageView.getTag()==null){
                    imageView.buildDrawingCache();
                    picture = imageView.getDrawingCache();
                }
                Client newClient = new Client(0, name, picture, telephone);

                try{
                    newClient.Insert();
                    mListener.setClientsList();
                    Toast.makeText(getActivity(),"Данные добавлены",Toast.LENGTH_LONG).show();

                    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                } catch(Exception ex){
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();

                }
                 }

                if(Regim==Edit){
                    String name = ((EditText)v.findViewById(R.id.nameClient)).getText().toString();
                    String telephone = ((EditText)v.findViewById(R.id.telephoneClient)).getText().toString();
                    ImageView imageView = v.findViewById(R.id.pictureClient);
                    Bitmap picture=null;
                    if(imageView.getTag()==null){
                        imageView.buildDrawingCache();
                        picture = imageView.getDrawingCache();
                    }

                    Client newClient = new Client(idEditingClient, name, picture, telephone);

                    try{
                        newClient.Update();
                        mListener.setClientsList();
                        Toast.makeText(getActivity(),"Данные изменены",Toast.LENGTH_LONG).show();
                        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
                    } catch(Exception ex){
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
               }



        if(v1.getId()==R.id.btnCancel){ getActivity().getFragmentManager().beginTransaction().remove(this).commit();}


    }

    public void onDismiss(DialogInterface dialog) {

        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }


}
