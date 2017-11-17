package com.example.logistik.logistikgobkg;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.logistik.logistikgobkg.Htpp.HttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

public class ViajeSubirEvicenciasTab extends Fragment {
    String IDViaje;
    String Titulo;
    String TipoArchivo = "EVIDENCIAS";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri filePath;
    private ImageView mImageView;
   // private String url = "http://10.0.2.2:63513/api/Viaje/SaveEvidenciaDigital";
    private String url = "https://api-bkg-test.logistikgo.com/api/Viaje/SaveEvidenciaDigital";

    ImageView imageViewCartaPorte, imageViewRemision, imageViewEvidencia;
    ImageButton buttonCartaPorte, buttonRemision, buttonEvidencia;
    Activity activity;
//  Linea 51
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viaje_subirevidencias, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        IDViaje = bundle.getString("IDViajeProceso");

        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.no_images);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        imageViewCartaPorte = (ImageView) view.findViewById(R.id.imageViewCartaPorte);
        imageViewRemision = (ImageView) view.findViewById(R.id.imageViewRemision);
        imageViewEvidencia = (ImageView) view.findViewById(R.id.imageViewEvidencia);

        imageViewCartaPorte.setImageDrawable(roundedDrawable);
        imageViewRemision.setImageDrawable(roundedDrawable);
        imageViewEvidencia.setImageDrawable(roundedDrawable);

        buttonCartaPorte = (ImageButton) view.findViewById(R.id.buttonCartaPorte);
        buttonRemision = (ImageButton) view.findViewById(R.id.buttonRemision);
        buttonEvidencia = (ImageButton) view.findViewById(R.id.buttonEvidencia);


        buttonCartaPorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "CARTA PORTE";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        buttonRemision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "REMISION";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        buttonEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "EVIDENCIA";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView nImage = (ImageView) getActivity().findViewById(R.id.imageView);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {

            filePath = data.getData();
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                sendPhoto(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendPhoto(Bitmap bitmap) throws Exception {
        new UploadTask().execute(bitmap);


        switch (Titulo) {
            case "CARTA PORTE":
                imageViewCartaPorte.setImageBitmap(bitmap);
                return;
            case "REMISION":
                imageViewRemision.setImageBitmap(bitmap);
                return;
            case "EVIDENCIA":
                imageViewEvidencia.setImageBitmap(bitmap);
                return;
        }
    }
    public static byte[] bitmapToByteArray(Bitmap bm) {
        // Create the buffer with the correct size
        int iBytes = bm.getWidth() * bm.getHeight() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(iBytes);

        // Log.e("DBG", buffer.remaining()+""); -- Returns a correct number based on dimensions
        // Copy to buffer and then into byte array
        bm.copyPixelsToBuffer(buffer);
        // Log.e("DBG", buffer.remaining()+""); -- Returns 0
        return buffer.array();
    }

    public class UploadTask extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {

            String BOUNDARY = "--eriksboundry--";

            if (bitmaps[0] == null)
                return null;
            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();            //   bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            //  InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
            //  bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.mipmap.ic_launcher);

           // ByteArrayOutputStream baos = new ByteArrayOutputStream();
           // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = bitmapToByteArray(bitmap);

            HttpClient client = new HttpClient(url);

            try {
                client.connectForMultipart();

                client.addFormPart("Titulo", Titulo);
                client.addFormPart("TipoArchivo", TipoArchivo);
                client.addFormPart("IDViaje", IDViaje);
                client.addFilePart("file", ".png", b);
                String response = null;

                client.finishMultipart();

                response = client.getResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //  Toast.makeText(MainActivity.this, R.string.uploaded, Toast.LENGTH_LONG).show();
        }

    }
}
