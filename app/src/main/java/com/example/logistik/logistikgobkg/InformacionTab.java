package com.example.logistik.logistikgobkg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class InformacionTab extends Fragment {
    TextView textViewCteOrigen, texViewCteDestino, textViewCdOrigen, textViewCdDestino, textViewEmbalaje,
            textViewClasificacion, textViewDescripcion, textViewCantidad, textViewPeso, textViewVolumen, textViewFolio;
    String strIDViaje;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion, container, false);
        textViewFolio = (TextView) view.findViewById(R.id.textFolio);
        textViewCteOrigen = (TextView) view.findViewById(R.id.textCteOrigen);
        texViewCteDestino = (TextView) view.findViewById(R.id.textCteDestino);
        textViewCdOrigen = (TextView) view.findViewById(R.id.textCdOrigen);
        textViewCdDestino = (TextView) view.findViewById(R.id.textCdDestino);
        textViewEmbalaje = (TextView) view.findViewById(R.id.textEmbalaje);
        textViewClasificacion = (TextView) view.findViewById(R.id.textClasificacion);
        textViewDescripcion = (TextView) view.findViewById(R.id.textDescripcion);
        textViewCantidad = (TextView) view.findViewById(R.id.textCantidad);
        textViewPeso = (TextView) view.findViewById(R.id.textPeso);
        textViewVolumen = (TextView) view.findViewById(R.id.textVolumen);
        Bundle bundle = getActivity().getIntent().getExtras();
        strIDViaje = bundle.getString("IDViajeProceso");

        try {
            datosg(view);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        button = (Button) view.findViewById(R.id.btn_Datos);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    datosg(view);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // TODO: begin API
    public void datosg(View view) throws ExecutionException, InterruptedException, JSONException {

        //API DEBUG
       // String strURL = "https://api-bgk-debug.logistikgo.com/api/Viaje/GetDatosViaje";
        //API DEMO
        String strURL = "https://api-bkg-test.logistikgo.com/api/Viaje/GetDatosViaje";

        //strIDViaje = "130";
        JSONObject jdata = new JSONObject();
        JSONObject jParams = new JSONObject();

        try {
            jdata.put("strURL", strURL);

            jParams.put("strIDViaje", strIDViaje);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //VERIFICA SI HAY CONEXIÓN DE INTERNET
//        if(isConnected()){
//            tvIsConnected.setBackgroundColor(0xFF00CC00);
//            tvIsConnected.setText("You are connected");

        //REALIZA LA PETICIO
        JSONObject jResult = GetResponse(jdata, jParams);
        jResult = jResult.getJSONObject("jData");
        textViewFolio.setText(jResult.getString("Folio"));
        textViewCteOrigen.setText(jResult.getString("ClienteOrigen"));
        texViewCteDestino.setText(jResult.getString("ClienteDestino"));
        textViewCdOrigen.setText(jResult.getString("CiudadOrigen"));
        textViewCdDestino.setText(jResult.getString("CiudadDestino"));
        textViewEmbalaje.setText(jResult.getString("Embalaje"));
        textViewClasificacion.setText(jResult.getString("Clasificacion"));
        textViewDescripcion.setText(jResult.getString("Descripcion"));
        textViewCantidad.setText(jResult.getString("Cantidad"));
        textViewPeso.setText(jResult.getString("Peso"));
        textViewVolumen.setText(jResult.getString("Volumen"));

//        }
    }

    public JSONObject GetResponse(JSONObject jdata, JSONObject jParams) throws ExecutionException, InterruptedException, JSONException {
        JSONObject resJson = null;

        //Instantiate new instance of our class
        LoginActivity.HttpGetRequest getRequest = new LoginActivity.HttpGetRequest();

        resJson = getRequest.execute(jdata, jParams).get();

        return resJson;
    }

    public static JSONObject GetHttpResponse(String strURL, JSONObject jData, String strRequest_method, int read_timeout, int connection_timeout) {

        String inputLine;
        JSONObject jRes = null;


        try {
            URL urlCurrent = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) urlCurrent.openConnection();

            connection.setRequestMethod(strRequest_method);
            connection.setReadTimeout(read_timeout);
            connection.setConnectTimeout(connection_timeout);

            //POST
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //ENCABEZADOS DE LA PETICIÓN
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            //Connect to our url
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(jData.toString().getBytes("UTF-8"));
            os.close();

            int HttpResult = connection.getResponseCode();

            //VERIFICAR SI LA CONEXION SE REALIZO DE FORMA CORRECTA = 200
            if (HttpResult == HttpURLConnection.HTTP_OK) {

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                StringBuilder stringBuilder = new StringBuilder();
                //   String strResponseMessage = connection.getResponseMessage();
                //    JsonReader jsonReader = new JsonReader(streamReader);

                //LEER JSON MANUAL
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                //  StringBuilder _stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String _strRes = stringBuilder.toString();
                JSONObject obj = new JSONObject(_strRes);
                JSONObject paramMeta = obj.getJSONObject("jMeta");
                //     String __strResponse = paramMeta.getString("ResponseCode");

                String strResponse = paramMeta.getString("Response");

                if (strResponse.equals("OK")) {
                    jRes = obj.getJSONObject("jData");
                } else {
                    jRes = obj.getJSONObject("jDataError");
                }
            } else {
                String strResponse = connection.getResponseMessage();
                InputStreamReader streamError = new InputStreamReader(connection.getErrorStream());
                JsonReader jsonReader = new JsonReader(streamError);

                //LEER JSON
                jsonReader.beginObject(); // Start processing the JSON object
                while (jsonReader.hasNext()) { // Loop through all keys
                    String key = jsonReader.nextName(); // Fetch the next key
                    if (key.equals("Message")) { // VERIFICA EL NOMBRE DEL CAMPO
                        //   strRes = jsonReader.nextString();
                        break; // Break out of the loop
                    } else {
                        jsonReader.skipValue(); // Skip values of other keys
                    }
                }
                jsonReader.close();

                Log.d("ERROR", strResponse);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jRes;
    }

    // VERIFICAR SI EXISTE CONEXIÓN A INTERNET
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    //CLASS ASYNC REQUEST
    public static class HttpGetRequest extends AsyncTask<JSONObject, Void, JSONObject> {

        //VARIABLES DE CONFIGURACION DE LA CONEXION
        public static final String REQUEST_METHOD = "POST";
        public static final int READ_TIMEOUT = 150000;
        public static final int CONNECTION_TIMEOUT = 150000;

        @Override
        protected void onPreExecute() {
        }


        @Override
        protected JSONObject doInBackground(JSONObject... jObject) {
            String stringUrl = null;
            JSONObject resJson = null;

            try {
                stringUrl = jObject[0].getString("strURL");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String result = null;
            String inputLine;

            try {

                resJson = GetHttpResponse(stringUrl, jObject[1], REQUEST_METHOD, READ_TIMEOUT, CONNECTION_TIMEOUT);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resJson;
        }

    }
}
