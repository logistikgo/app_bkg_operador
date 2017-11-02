package com.example.logistik.logistikgobkg;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ViajeSubirEvicenciasTab extends Fragment {
    ImageView imageEvidenceUno, imageEvidenceDos, imageEvidenceTres;
    ImageButton buttonEvidenceUno, buttonEvidenceDos, buttonEvidenceTres;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viaje_subirevidencias, container, false);

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.no_images);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        imageEvidenceUno = (ImageView) view.findViewById(R.id.imageEvidenceUno);
        imageEvidenceDos = (ImageView) view.findViewById(R.id.imageEvidenceDos);
        imageEvidenceTres = (ImageView) view.findViewById(R.id.imageEvidenceTres);

        imageEvidenceUno.setImageDrawable(roundedDrawable);
        imageEvidenceDos.setImageDrawable(roundedDrawable);
        imageEvidenceTres.setImageDrawable(roundedDrawable);

        buttonEvidenceUno = (ImageButton)view.findViewById(R.id.buttonEvidenceUno);


        buttonEvidenceUno.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v){
                activity = getActivity();
                Toast.makeText(activity, "Cambio de Status", Toast.LENGTH_SHORT);

                Fragment fragmentCameraEvidence = new CameraImageEvidence();
                //linearFragment.removeAllViews();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.Constraint, fragmentCameraEvidence).commit();
                fragmentManager.addOnBackStackChangedListener(null);
            }
        });
        return view;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
