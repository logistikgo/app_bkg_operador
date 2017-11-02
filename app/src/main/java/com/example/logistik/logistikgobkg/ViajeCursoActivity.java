package com.example.logistik.logistikgobkg;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class ViajeCursoActivity extends AppCompatActivity {
    String strIDViaje;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viaje_curso);
        Bundle bundle = this.getIntent().getExtras();
        strIDViaje = bundle.getString("IDViajeProceso");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_viaje_curso);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//
                onBackPressed();
                finish();
            }
        });
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viaje_curso, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //MENU BAR
//        if (id == R.id.action_acercade) {
//            Intent intent = new Intent(ViajeCursoActivity.this, AcercadeActivity.class);
//            startActivity(intent);
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (strIDViaje.equals("0")) {
                ViajeFinalizadoFragment viajeFinalizadoViaje = new ViajeFinalizadoFragment();
                return viajeFinalizadoViaje;
            }
            else {
                switch (position) {
                    case 0:
                            ViajeCursoTab viajeCursoTab = new ViajeCursoTab();
                            return viajeCursoTab;
                    case 1:
                            InformacionTab informacionTab = new InformacionTab();
                            return informacionTab;
                    case 2:
                        ViajeSubirEvicenciasTab SubirEvidenciasTab = new ViajeSubirEvicenciasTab();
                        return SubirEvidenciasTab;

                    default:
                        return null;
                }
            }

//            switch (position) {
//                case 0:
//                    if (strIDViaje.equals("0")) {
//                        ViajeFinalizadoFragment viajeFinalizadoViaje = new ViajeFinalizadoFragment();
//                        return viajeFinalizadoViaje;
//                    } else {
//                        ViajeCursoTab viajeCursoTab = new ViajeCursoTab();
//                        return viajeCursoTab;
//                    }
//                case 1:
//                    if (strIDViaje.equals("0")) {
//                        ViajeFinalizadoFragment viajeFinalizadoInformacion = new ViajeFinalizadoFragment();
//                        return viajeFinalizadoInformacion;
//                    } else {
//                        InformacionTab informacionTab = new InformacionTab();
//                        return informacionTab;
//                    }
//                default:
//                    return null;
//            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_travel);
                case 1:
                    return getString(R.string.tab_data);
                case 2:
                    return getString(R.string.tab_upload_evidence);
            }
            return null;
        }
    }
}
