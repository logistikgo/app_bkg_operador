package com.example.logistik.logistikgobkg;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, InformacionTab.OnFragmentInteractionListener {
    TextView textUsuario;
    View view;
    String Nameusuario;
    String IDViajeProceso;
    String StatusProceso;
//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view = navigationView.getHeaderView(0);

        textUsuario = (TextView) view.findViewById(R.id.textUsuario);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            Nameusuario = bundle.getString("NameUsuario");
            textUsuario.setText(Nameusuario);
            IDViajeProceso = bundle.getString("IDViajeProceso");
            StatusProceso = bundle.getString("StatusProceso");
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textUsuario.setText(savedInstanceState.getString("Name_Usuario"));
        Nameusuario = savedInstanceState.getString("NameUser");
        Toast.makeText(this, "Usuario " + Nameusuario, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("NameUser", Nameusuario);
        outState.putString("Name_Usuario", textUsuario.getText().toString());

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //MENU BAR
//

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_incio) {
            // Handle the camera action
        } else if (id == R.id.nav_viajecurso) {

            if (isConnected()) {
                if (isUbicacion()){
                intent = new Intent(MenuActivity.this, ViajeCursoActivity.class);
                intent.putExtra("IDViajeProceso", IDViajeProceso);
                intent.putExtra("StatusProceso", StatusProceso);
                }else {
                    Toast.makeText(this, "Favor de activar tu ubicacion para continuar", Toast.LENGTH_SHORT).show();
                    android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(this);
                    alertdialog.setTitle(Html.fromHtml("<font color='#FF7F27'>Tu Ubicacion esta desactivada</font>"));
                    alertdialog.setMessage("Favor de activar tu ubicacion para continuar");
                    alertdialog.setCancelable(false);
//                alertdialog.setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface alertdialog, int id) {
//                    }
//                });
                    alertdialog.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface alertdialog, int id) {
//                cancelar();
                        }
                    });
                    alertdialog.show();
                }
            } else {
                android.app.AlertDialog.Builder alertdialog = new android.app.AlertDialog.Builder(this);
                alertdialog.setTitle(Html.fromHtml("<font color='#FF7F27'>Los datos están desactivados</font>"));
                alertdialog.setMessage("Activa los datos o Wi-Fi en la configuración");
                alertdialog.setCancelable(false);
//                alertdialog.setPositiveButton("Configuración", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface alertdialog, int id) {
//                    }
//                });
                alertdialog.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface alertdialog, int id) {
//                cancelar();
                    }
                });
                alertdialog.show();
            }

        } else if (id == R.id.nav_ajustes) {

        } else if (id == R.id.nav_acercade) {
            // intent = new Intent(MenuActivity.this, AcercadeActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }
    public boolean isUbicacion() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
