package com.example.caxidy.ejemplodrawer;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Minijuego fragmentJuego;
    Lienzo fragmentLienzo;
    FragmentTransaction transaccion;
    int FRAGMENTO_MINIJ=1, FRAGMENTO_LIENZO=2, fragmentoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Se selecciona el fragment que aparecera por defecto...
        transaccion = getFragmentManager().beginTransaction();
        fragmentJuego = new Minijuego();
        transaccion.replace(R.id.content_main,fragmentJuego);
        transaccion.addToBackStack(null);
        transaccion.commit();
        fragmentoSeleccionado=FRAGMENTO_MINIJ;
    }

    @Override
    public void onSaveInstanceState(Bundle savedBundle){
        savedBundle.putInt("fragmento",fragmentoSeleccionado);
    }
    @Override
    public void onRestoreInstanceState(Bundle restoredBundle){

        transaccion = getFragmentManager().beginTransaction();

        if(restoredBundle.getInt("fragmento")==1){
            fragmentJuego = new Minijuego();
            transaccion.replace(R.id.content_main,fragmentJuego);
            transaccion.addToBackStack(null);
            transaccion.commit();
            fragmentoSeleccionado=FRAGMENTO_MINIJ;
        }
        else {
            fragmentLienzo = new Lienzo();
            transaccion.replace(R.id.content_main, fragmentLienzo);
            transaccion.addToBackStack(null);
            transaccion.commit();
            fragmentoSeleccionado=FRAGMENTO_LIENZO;
        }

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Son las opciones del drawer
        int id = item.getItemId();

        //Creacion de objeto FragmentTransaction para gestionar los Fragments de forma dinamica
        transaccion = getFragmentManager().beginTransaction();

        if (id == R.id.minijuego) {
            //Cambia al fragment del minijuego
            fragmentJuego = new Minijuego();
            transaccion.replace(R.id.content_main,fragmentJuego);
            transaccion.addToBackStack(null);
            transaccion.commit();
            fragmentoSeleccionado=FRAGMENTO_MINIJ;
        } else if (id == R.id.paint) {
            //Cambia al fragment del lienzo
            fragmentLienzo = new Lienzo();
            transaccion.replace(R.id.content_main, fragmentLienzo);
            transaccion.addToBackStack(null);
            transaccion.commit();
            fragmentoSeleccionado=FRAGMENTO_LIENZO;
        }
        else
            transaccion.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Ocultar el teclado virtual
    public void esconderTeclado(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
