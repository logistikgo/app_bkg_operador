package com.example.logistik.logistikgobkg;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.logistik.logistikgobkg.Config.ConexionAPIs;
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

public class ViajeSubirEvicenciasTab extends Fragment{
    String IDViaje;
    String Titulo;
    String TipoArchivo = "EVIDENCIAS";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Uri filePath;
    private ImageView mImageView;
    private String urlCamera = "http://10.0.2.2:63518/api/Viaje/SaveEvidenciaDigital";
   private String urlDescription = "http://10.0.2.2:63518/api/Viaje/SaveComentarioEv_Digital";

   String RutaAPI, strCartaPorte, strRemision, strEvidencia;

    ImageView imageViewCartaPorte, imageViewRemision, imageViewEvidencia;
    ImageButton imageButtonCartaPorte, imageButtonRemision, imageButtonEvidencia, buttonCartaPorte, buttonRemision, buttonEvidencia;
    EditText edittextCartaPorte, editTextRemision, editTextEvidencia;
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
        RutaAPI = ConexionAPIs.RutaApi;

        Bundle bundle = getActivity().getIntent().getExtras();
        IDViaje = bundle.getString("IDViajeProceso");

        imageViewCartaPorte = (ImageView) view.findViewById(R.id.imageViewCartaPorte);
        imageViewRemision = (ImageView) view.findViewById(R.id.imageViewRemision);
        imageViewEvidencia = (ImageView) view.findViewById(R.id.imageViewEvidencia);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.no_images);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);

        imageViewCartaPorte.setImageDrawable(roundedBitmapDrawable);
        imageViewRemision.setImageDrawable(roundedBitmapDrawable);
        imageViewEvidencia.setImageDrawable(roundedBitmapDrawable);

        imageButtonCartaPorte = (ImageButton) view.findViewById(R.id.buttonCartaPorte);
        imageButtonRemision = (ImageButton) view.findViewById(R.id.buttonRemision);
        imageButtonEvidencia = (ImageButton) view.findViewById(R.id.buttonEvidencia);

        edittextCartaPorte = (EditText) view.findViewById(R.id.editTextCartaPorte);
        editTextRemision = (EditText) view.findViewById(R.id.editTextRemision);
        editTextEvidencia = (EditText) view.findViewById(R.id.editTextEvidencia);

        buttonCartaPorte = (ImageButton) view.findViewById(R.id.buttonSendCartaPorte);
        buttonRemision = (ImageButton) view.findViewById(R.id.buttonSendRemision);
        buttonEvidencia = (ImageButton) view.findViewById(R.id.buttonSendEvidencia);

        disableEditText(edittextCartaPorte, buttonCartaPorte);
        disableEditText(editTextRemision, buttonRemision);
        disableEditText(editTextEvidencia, buttonEvidencia);

        imageButtonCartaPorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "CARTA PORTE";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        imageButtonRemision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "REMISION";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });
        imageButtonEvidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Titulo = "EVIDENCIA";
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });

        buttonCartaPorte.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                Titulo = "CARTA PORTE";
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    strCartaPorte = edittextCartaPorte.getText().toString();
                    saveDescription(strCartaPorte);
                }
                return true;
            }
        });

        buttonRemision.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                Titulo = "REMISION";
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    strRemision = editTextRemision.getText().toString();
                    saveDescription(strRemision);
                }
                return true;
            }
        });

        buttonEvidencia.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                Titulo = "EVIDENCIA";
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    strEvidencia = editTextEvidencia.getText().toString();
                    saveDescription(strEvidencia);
                }
                return true;
            }
        });
        return view;
    }

    private void disableEditText(EditText editText, ImageButton imageButton) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setFocusableInTouchMode(false);

        imageButton.setEnabled(false);
        imageButton.setFocusable(false);
        imageButton.setFocusableInTouchMode(false);
    }

    private void enableEditText(EditText editText, ImageButton imageButton) {
        editText.setFocusable(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
        editText.setFocusableInTouchMode(true);

        imageButton.setEnabled(true);
        imageButton.setFocusable(true);
        imageButton.setFocusableInTouchMode(true);
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

    private void  saveDescription(String strData){
        new UploadDescription().execute(strData);
        //Toast.makeText(getActivity(), dato, Toast.LENGTH_SHORT).show();
    }

    private void sendPhoto(Bitmap bitmap) throws Exception {
        new UploadTask().execute(bitmap);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);

        switch (Titulo) {
            case "CARTA PORTE":
                imageViewCartaPorte.setImageDrawable(roundedBitmapDrawable);
                enableEditText(edittextCartaPorte, buttonCartaPorte);
                return;
            case "REMISION":
                imageViewRemision.setImageDrawable(roundedBitmapDrawable);
                enableEditText(editTextRemision, buttonRemision);
                return;
            case "EVIDENCIA":
                imageViewEvidencia.setImageDrawable(roundedBitmapDrawable);
                enableEditText(editTextEvidencia, buttonEvidencia);
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
            String url = urlCamera; //RutaAPI +  "api/Viaje/SaveEvidenciaDigital";
            String BOUNDARY = "--eriksboundry--";

            if (bitmaps[0] == null)
                return null;
            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();            //   bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // convert Bitmap to ByteArrayOutputStream
            //  InputStream in = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream
            //  bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(), R.mipmap.ic_launcher);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            //byte[] b = bitmapToByteArray(bitmap);




            HttpClient client = new HttpClient(url);

            try {
                client.connectForMultipart();

                client.addFormPart("Titulo", Titulo);
                client.addFormPart("TipoArchivo", TipoArchivo);
                client.addFormPart("IDViaje", IDViaje);
                client.addFilePart("file", ".png", baos.toByteArray());
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

    public class UploadDescription extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings){
            String url = urlDescription; //RutaAPI + "api/Viaje/SaveComentarioEv_Digital";
            HttpClient client = new HttpClient(url);
            JSONObject jsonObject = new JSONObject();

            try {
                client.connectForMultipart();

                jsonObject.put("strTitulo", Titulo);
                jsonObject.put("TipoArchivo", TipoArchivo);
                jsonObject.put("strIDBro_Viaje", IDViaje);
                jsonObject.put("strObservacion", strings[0]);
                client.addDescription(jsonObject);
                String response = null;

                client.finishMultipart();

                response = client.getResponse();
            }catch (Exception e) {
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
