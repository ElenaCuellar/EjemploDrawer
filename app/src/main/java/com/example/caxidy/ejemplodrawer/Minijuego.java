package com.example.caxidy.ejemplodrawer;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class Minijuego extends Fragment {

    EditText oper,sol,total;
    Button botonComenzar, botonOk;
    ProgressBar barra;
    int resultado, operacionesSuperadas, progresoBarra;
    boolean juegoActivo;
    MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.minijuego_layout,container,false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);

        oper = (EditText) getView().findViewById(R.id.operacion);
        sol = (EditText) getView().findViewById(R.id.solucion);
        total = (EditText) getView().findViewById(R.id.total);
        botonComenzar = (Button) getView().findViewById(R.id.comenzar);
        botonOk = (Button) getView().findViewById(R.id.ok);
        oper.setEnabled(false);
        total.setEnabled(false);

        barra = (ProgressBar) getView().findViewById(R.id.progressBar);

        botonComenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comenzar();
            }
        });

        botonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validar();
            }
        });

        resultado=operacionesSuperadas=0;
        juegoActivo=false;

        mainActivity = (MainActivity) getView().getContext();

        //Recuperar la informacion con SharedPreferences:
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        oper.setText(sharedPref.getString("operacion","0"));
        sol.setText(sharedPref.getString("solucion","0"));
        total.setText(sharedPref.getString("total","0"));
    }

    @Override
    public void onPause() {

        super.onPause();

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("operacion", oper.getText().toString());
        editor.putString("solucion", sol.getText().toString());
        editor.putString("total", total.getText().toString());
        editor.commit();
    }

    public void comenzar(){
        if(!juegoActivo) {
            total.setText("");
            //activar ProgressBar
            TareaProgressbar progB = new TareaProgressbar();
            progB.execute();
            //poner operacion y habilitar boton de validar
            generarOperacion();
            botonOk.setEnabled(true);
            juegoActivo=true;
        }
    }
    public void generarOperacion(){
        //Generar la operacion y guardarla en la variable global 'resultado'
        int op1 = (int)(Math.random()*100+1);
        int op2 = (int)(Math.random()*100+1);
        int operador = (int)(Math.random()*3+1);
        switch (operador){
            case 1:
                resultado=op1+op2;
                oper.setText(op1+"+"+op2);
                break;
            case 2:
                if(op2>op1) {
                    resultado = op2 - op1;
                    oper.setText(op2 + "-" + op1);
                }
                else {
                    resultado = op1 - op2;
                    oper.setText(op1 + "-" + op2);
                }
                break;
            case 3:
                resultado=op1*op2;
                oper.setText(op1+"x"+op2);
                break;
            default: break;
        }
    }

    public void validar(){
        int tuSolucion;
        mainActivity.esconderTeclado(sol);
        try{
            tuSolucion = Integer.parseInt(sol.getText().toString());
            if(tuSolucion==resultado) {
                operacionesSuperadas++;
                total.setText(Integer.toString(operacionesSuperadas));
            }
            generarOperacion();
        }catch(NumberFormatException e){
            Snackbar.make(sol,R.string.numNoValido,Snackbar.LENGTH_SHORT).show();
        }
        sol.setText("");
    }

    //Clase del progressbar, para que se ejecute de forma asincrona
    class TareaProgressbar extends AsyncTask<String,Integer,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Snackbar.make(barra,R.string.barraProg,Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            for(int i=1;i<61;i++){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}

                //sacar el porcentaje del progreso por pantalla
                publishProgress(i);
            }
            return getString(R.string.timeOver);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            barra.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String cadena) {
            super.onPostExecute(cadena);
            //Fin de la partida
            juegoActivo=false;
            botonOk.setEnabled(false);
            operacionesSuperadas=0;
            oper.setText(R.string.timeOver);
            Snackbar.make(barra,cadena,Snackbar.LENGTH_SHORT).show();
        }

    }
}
