package com.example.logistik.logistikgobkg;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class ViajeCursoTab extends Fragment implements OnMapReadyCallback {
    Button button;
    GoogleMap mMap;
    MapView mapView;
    View view;
    private Marker marcador;
    // double lat = 0.0;
    double coordLng = 0.0;
    double coordLat = 0.0;
    String strIDBro_Viaje;
    String StatusProceso;
    LatLng lastCoordenadas;
    Date lastUpdate = new Date();
    int secondLastUpdate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_viaje_curso, container, false);

        Bundle bundle = getActivity().getIntent().getExtras();
        StatusProceso = bundle.getString("StatusProceso");
        strIDBro_Viaje = bundle.getString("IDViajeProceso");
        button = (Button) view.findViewById(R.id.btn_viaje_curso);
        if (bundle != null) {
            button.setText(StatusProceso);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    setStatus(view);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        if (bundle != null)
//            textstaus.setText(bundle.getString("StatusProceso"));


//        button = (Button) view.findViewById(R.id.btn_viaje_curso);
//        button.setText("Llegada Origen");
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (button.getText() == "Llegada Origen") {
//                    button.setText("CARGA");
//                }
//                else if (button.getText() == "CARGA"){
//                    button.setText("RUTA");
//                }
//                else if (button.getText() == "RUTA"){
//                    button.setText("DECARGA");
//                }
//            }
//        });
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // mMap = (GoogleMap)view.findViewById(R.id.map);
//        if(mMap != null){
//            mMap.getMapAsync(this);
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        miUbicacion();
    }

    private void agregarMarcador(double lat, double lng) {

        LatLng coordenadas = new LatLng(lat, lng);
        CameraUpdate miubicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) marcador.remove();
        marcador = mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_trailer_map)).flat(true).rotation(4));
        mMap.animateCamera(miubicacion);
    }

    private void actualizarUbicacion(Location location) {
        if (location != null) {
            //  lat = location.getLatitude();
            coordLng = location.getLongitude();
            coordLat = location.getLatitude();
            agregarMarcador(coordLat, coordLng);
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            actualizarUbicacion(location);
            SaveCoordenadas(location);
            //   Toast.makeText(getActivity(), "Longitud:" + coordLng + "Latitud:" + coordLat, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //return;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        actualizarUbicacion(location);
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    // TODO: begin API
    public void setStatus(View view) throws ExecutionException, InterruptedException, JSONException {

        if (isConnected()) {
            AlertDialog.Builder alertdialog = new AlertDialog.Builder(getActivity());
            alertdialog.setTitle("ALERTA");
            alertdialog.setMessage("¿ Estas seguro de cambiar el status?");
            alertdialog.setCancelable(false);
            alertdialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertdialog, int id) {
                    //region CAMBIA STATUS

                    //API debug
                    // String strURL = "http://192.168.1.54:63510/api/Viaje/Bro_SetStatus";
                    String strURL = "https://api-bgk-debug.logistikgo.com/api/Viaje/Bro_SetStatus";

                    JSONObject jdata = new JSONObject();
                    JSONObject jParams = new JSONObject();

                    try {
                        jdata.put("strURL", strURL);

                        jParams.put("strIDBro_Viaje", strIDBro_Viaje);
                        jParams.put("coordLat", coordLat);
                        jParams.put("coordLng", coordLng);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //REALIZA LA PETICIO
                    JSONObject jResult = null;
                    try {
                        jResult = GetResponse(jdata, jParams);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String StatusSiguiente = null;
                    try {
                        StatusSiguiente = jResult.getString("StatusProceso");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (StatusSiguiente.equals("")) {
                        Intent intent = new Intent(getActivity(), MenuActivity.class);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder alerBuilder = new AlertDialog.Builder(getActivity());
                        alerBuilder.setTitle("ALERTA");
                        alerBuilder.setMessage("Estatus cambiado correctamente");
                        alerBuilder.setCancelable(false);

                        alerBuilder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface alertdialog, int id) {
                            }
                        });
                        button.setText(StatusSiguiente);
                    }

//                if (StatusSiguiente != "FINALIZADO") {
//
//                } else {
//                    Intent intent = new Intent(getActivity(), MenuActivity.class);
//                    startActivity(intent);
//                }
                    //endregion
                }
            });
            alertdialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface alertdialog, int id) {
//                cancelar();
                }
            });
            alertdialog.show();
        } else {
            android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(getActivity());
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

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public JSONObject GetResponse(JSONObject jdata, JSONObject jParams) throws ExecutionException, InterruptedException, JSONException {
        JSONObject resJson = null;

        //Instantiate new instance of our class
        ViajeCursoTab.HttpGetRequest getRequest = new ViajeCursoTab.HttpGetRequest();

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
            //  connection.setRequestProperty("Host", "localhost:63510");
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

                //LEER JSON MANUAL

                BufferedReader reader = new BufferedReader(streamReader);

                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String _strRes = stringBuilder.toString();
                JSONObject obj = new JSONObject(_strRes);
                JSONObject paramMeta = obj.getJSONObject("jMeta");

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

    public void SaveCoordenadas(Location location) {
        //region SAVE COORDENADAS

        //API debug
        String strURL = "https://api-bgk-debug.logistikgo.com/api/Maps/SaveCoordenadasBro";

        JSONObject jdata = new JSONObject();
        JSONObject jParams = new JSONObject();

        double fLng = location.getLongitude();
        double fLat = location.getLatitude();
        LatLng currentCoordenadas = new LatLng(fLng, fLat);


        // String strDViaje = strIDViaje;

        try {
            jdata.put("strURL", strURL);

            jParams.put("strIDBro_Viaje", strIDBro_Viaje);
            jParams.put("fLat", fLat);
            jParams.put("fLng", fLng);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //REALIZA LA PETICIO
//        JSONObject jResult = null;
//        try {
//            if (lastCoordenadas != null) {
//                double distance = SphericalUtil.computeDistanceBetween(currentCoordenadas, lastCoordenadas);
//                distance = distance / 1000;
//                //  Toast.makeText(getActivity(), Double.toString(distance), Toast.LENGTH_SHORT).show();
//
//                if (secondLastUpdate > 30 && distance > 5) {
//                    GetResponse(jdata, jParams);
//                }
//            }
//

        try {
            JSONObject jResult = GetResponse(jdata, jParams);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//            lastCoordenadas = new LatLng(fLng, fLat);
//            lastUpdate = new Date();
//            secondLastUpdate = lastUpdate.getMinutes();

//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        //endregion
    }

}
