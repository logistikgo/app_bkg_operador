package com.example.logistik.logistikgobkg;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logistik.logistikgobkg.Config.ConexionAPIs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    EditText editUsuario;
    EditText editContrasena;
    String strUsuario;
    String strContrasena;
    String RutaAPI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       RutaAPI = ConexionAPIs.RutaApi;

        editUsuario = (EditText) findViewById(R.id.editUsuario);
        editContrasena = (EditText) findViewById(R.id.editContrasena);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(this, "ACCESS GRANTED REQUEST", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "ACCESS GRANTED", Toast.LENGTH_SHORT).show();
        }

    }

    public void onMenuClick(View view) throws ExecutionException, InterruptedException, JSONException {

        if (isConnected()) {
            strUsuario = editUsuario.getText().toString();
            strContrasena = editContrasena.getText().toString();

            if (ValidateForm(new EditText[]{editUsuario, editContrasena})) {
                //API DEBUG
//                String strURL = "https://api-bgk-debug.logistikgo.com/api/Usuarios/ValidarUsuario";
                //API DEMO
                String strURL = RutaAPI + "api/Usuarios/ValidarUsuario";
                //API DEBUG VISUAL STUDIO
                JSONObject jdata = new JSONObject();
                JSONObject jParams = new JSONObject();

                try {
                    jdata.put("strURL", strURL);
                    jParams.put("strUsuario", strUsuario);
                    jParams.put("strContrasena", strContrasena);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //REALIZA LA PETICION
                JSONObject jResult = GetResponse(jdata, jParams);

                //  if (jResult.getString("Response").equals("OK")) {
//                    jRes = obj.getJSONObject("jData");
//                }
                if (jResult.getJSONObject("jMeta").getString("Response").equals("OK")) {
                    jResult = jResult.getJSONObject("jData");
                    String NombreUsuario = jResult.getString("Nombre");
                    Toast.makeText(this, "Bienvenido " + NombreUsuario, Toast.LENGTH_SHORT).show();
                    Context currentContext = this;
                    Intent activity_login = new Intent(currentContext, MenuActivity.class);
                    activity_login.putExtra("NameUsuario", NombreUsuario);
                    activity_login.putExtra("IDViajeProceso", jResult.getString("IDViajeProceso"));
                    activity_login.putExtra("StatusProceso", jResult.getString("StatusProceso"));
                    startActivity(activity_login);
                    overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    finish();
                } else {
                    Toast.makeText(this, jResult.getJSONObject("jMeta").getString("Message"), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(this);
            alertdialog.setTitle(Html.fromHtml("<font color='#FF7F27'>Los datos están desactivados</font>"));
            alertdialog.setMessage("Activa los datos o Wi-Fi en la configuración");
            alertdialog.setCancelable(false);
//            alertdialog.setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface alertdialog, int id) {
//                }
//            });
            alertdialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertdialog, int id) {
//                cancelar();
                }
            });
            alertdialog.show();
        }
    }

    public static boolean ValidateForm(EditText[] fields) {
        boolean bRes = true;
        for (int i = 0; i < fields.length; i++) {
            EditText currentField = fields[i];
            if (currentField.getText().toString().isEmpty()) {
                currentField.setError("Campo requerido");
                bRes = false;
                break;
            }
        }
        return bRes;
    }

    public JSONObject GetResponse(JSONObject jdata, JSONObject jParams) throws ExecutionException, InterruptedException, JSONException {
        JSONObject resJson = null;

        //Instantiate new instance of our class
        HttpGetRequest getRequest = new HttpGetRequest();

        resJson = getRequest.execute(jdata, jParams).get();

        return resJson;
    }

    public static JSONObject GetHttpResponse(String strURL, JSONObject jData, String strRequest_method, int read_timeout, int connection_timeout) {

        String inputLine;
        JSONObject jRes = null;
//
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
            //connection.setRequestProperty("Host", "localhost:63510");

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

                //LEER JSON MANUAL
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String _strRes = stringBuilder.toString();
                jRes = new JSONObject(_strRes);
                //   jRes = obj.getJSONObject("jMeta");

                //  jRes = obj.getJSONObject("jData");
                // String strResponse = paramMeta.getString("Response");

//                if (strResponse.equals("OK")) {
//                    jRes = obj.getJSONObject("jData");
//                }
            } else {
                InputStreamReader streamReader = new InputStreamReader(connection.getErrorStream());

                StringBuilder stringBuilder = new StringBuilder();

                //LEER JSON MANUAL
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String _strRes = stringBuilder.toString();
                jRes = new JSONObject(_strRes);
                //   JSONObject paramMeta = obj.getJSONObject("jMeta");
                //      throw new IOException(paramMeta.getString("Message"));
//                String strResponse = connection.getResponseMessage();
//                InputStreamReader streamError = new InputStreamReader(connection.getErrorStream());
//
//                JsonReader jsonReader = new JsonReader(streamError);
//
//                //LEER JSON
//                jsonReader.beginObject(); // Start processing the JSON object
//                while (jsonReader.hasNext()) { // Loop through all keys
//                    String key = jsonReader.nextName(); // Fetch the next key
//                    if (key.equals("Message")) { // VERIFICA EL NOMBRE DEL CAMPO
//
//                        break; // Break out of the loop
//                    } else {
//                        jsonReader.skipValue(); // Skip values of other keys
//                    }
//                }
//                jsonReader.close();
//
//                Log.d("ERROR", strResponse);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return jRes;
    }

    // VERIFICAR SI EXISTE CONEXIÓN A INTERNET
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
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

            try {

                resJson = GetHttpResponse(stringUrl, jObject[1], REQUEST_METHOD, READ_TIMEOUT, CONNECTION_TIMEOUT);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resJson;
        }

    }
}
