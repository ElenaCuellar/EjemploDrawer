package com.example.caxidy.ejemplodrawer;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class Lienzo extends Fragment {
    MainActivity mainActivity;
    EditText valorR, valorG, valorB, valorPos;
    Button bGener, bAleator, bOkPosicion;
    Spinner sEstilo, sFigura, sPos;
    String[] estilos, figuras, posCirculo, posOvRec, posLinea;
    ArrayAdapter<String> adapterEstilo, adapterFigura, adapterPos;
    Float x, y, radio, x2, y2, izq, arriba, der, abajo;
    LinearLayout layoutCanvas;
    Mural fondo;
    Canvas canvasM;
    Bitmap baseDibujo;

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lienzo_layout,container,false);
    }

    @Override
    public void onActivityCreated(Bundle bundle){

        super.onActivityCreated(bundle);

        x=y=radio=x2=y2=izq=arriba=der=abajo=0f;

        bGener = (Button)getView().findViewById(R.id.bGenerar);
        bAleator = (Button)getView().findViewById(R.id.bAleatorio);
        bOkPosicion = (Button)getView().findViewById(R.id.bOkPos);

        valorR = (EditText) getView().findViewById(R.id.txR);
        valorG = (EditText) getView().findViewById(R.id.txG);
        valorB = (EditText) getView().findViewById(R.id.txB);
        valorPos = (EditText) getView().findViewById(R.id.txpos);

        mainActivity = (MainActivity) getView().getContext();

        estilos = new String[]{getString(R.string.estRe),getString(R.string.estBo),getString(R.string.estReBo)};
        adapterEstilo= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, estilos);
        adapterEstilo.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sEstilo=(Spinner)mainActivity.findViewById(R.id.spinnerEstilo);
        sEstilo.setAdapter(adapterEstilo);
        sEstilo.setSelection(0);

        figuras = new String[]{
                getString(R.string.opcircle),getString(R.string.opovalo),getString(R.string.oprectangle),getString(R.string.opline)};
        adapterFigura= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, figuras);
        adapterFigura.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sFigura=(Spinner)mainActivity.findViewById(R.id.spinnerFigura);
        sFigura.setAdapter(adapterFigura);
        sFigura.setSelection(0);

        posCirculo = new String[]{"X","Y",getString(R.string.opradio)};
        posOvRec = new String[]{getString(R.string.opizq),getString(R.string.oparriba),getString(R.string.opder), getString(R.string.opabajo)};
        posLinea = new String[]{"X1","Y1","X2","Y2"};
        adapterPos= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, posCirculo);
        adapterPos.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        sPos=(Spinner)mainActivity.findViewById(R.id.spinnerPos);
        sPos.setAdapter(adapterPos);
        sPos.setSelection(0);

        layoutCanvas = (LinearLayout) mainActivity.findViewById(R.id.fragmentoCanvas);
        baseDibujo = Bitmap.createBitmap(1200,800,Bitmap.Config.ARGB_8888);
        fondo = new Mural(mainActivity);
        layoutCanvas.addView(fondo);
        mainActivity.getSupportActionBar().hide();

        //Recuperar la informacion con SharedPreferences:
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        x=sharedPref.getFloat("x",0f);
        y=sharedPref.getFloat("y",0f);
        radio=sharedPref.getFloat("rad",0f);
        x2=sharedPref.getFloat("x2",0f);
        y2=sharedPref.getFloat("y2",0f);
        izq=sharedPref.getFloat("izq",0f);
        arriba=sharedPref.getFloat("arriba",0f);
        der=sharedPref.getFloat("der",0f);
        abajo=sharedPref.getFloat("abajo",0f);
        valorR.setText(sharedPref.getString("r","0"));
        valorG.setText(sharedPref.getString("g","0"));
        valorB.setText(sharedPref.getString("b","0"));
        valorPos.setText(sharedPref.getString("pos","0"));
        sEstilo.setSelection(sharedPref.getInt("sEstilo",0));
        sFigura.setSelection(sharedPref.getInt("sFigura",0));
        sPos.setSelection(sharedPref.getInt("sPosicion",0));

        //Eventos
        bGener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generar();
            }
        });

        bAleator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAleatorio();
            }
        });

        bOkPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ponerValorPos();
            }
        });

        valorPos.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    ponerValorPos();
                    return true;
                }
                return false;
            }
        });

        sFigura.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(id==0)
                    adapterPos= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, posCirculo);
                else if (id==1 || id==2)
                    adapterPos= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, posOvRec);
                else
                    adapterPos= new ArrayAdapter<> (mainActivity,android.R.layout.simple_list_item_1, posLinea);
                sPos.setAdapter(adapterPos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    @Override
    public void onPause() {

        super.onPause();

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat("x",x);
        editor.putFloat("y",y);
        editor.putFloat("rad",radio);
        editor.putFloat("x2",x2);
        editor.putFloat("y2",y2);
        editor.putFloat("izq",izq);
        editor.putFloat("arriba",arriba);
        editor.putFloat("der",der);
        editor.putFloat("abajo",abajo);
        editor.putString("r",valorR.getText().toString());
        editor.putString("g",valorG.getText().toString());
        editor.putString("b",valorB.getText().toString());
        editor.putString("pos",valorPos.getText().toString());
        editor.putInt("sEstilo",sEstilo.getSelectedItemPosition());
        editor.putInt("sFigura",sFigura.getSelectedItemPosition());
        editor.putInt("sPosicion",sPos.getSelectedItemPosition());
        editor.commit();
    }

    public void generar(){
        //Coger los datos del lienzo y generar el dibujo correspondiente
        canvasM = new Canvas(baseDibujo);
        fondo.dibujar(canvasM);
        reiniciarValoresPosicion();
    }

    public void ponerValorPos(){
        //Poner el valor en la variable correspondiente
        if(sFigura.getSelectedItem().equals(getString(R.string.opcircle))) {
            if (sPos.getSelectedItem().equals("X"))
                x = Float.parseFloat(valorPos.getText().toString());
            else if (sPos.getSelectedItem().equals("Y"))
                y = Float.parseFloat(valorPos.getText().toString());
            else
                radio = Float.parseFloat(valorPos.getText().toString());
        }
        else if(sFigura.getSelectedItem().equals(getString(R.string.opovalo)) ||sFigura.getSelectedItem().equals(getString(R.string.oprectangle))){
            if(sPos.getSelectedItem().equals(getString(R.string.opizq)))
                izq = Float.parseFloat(valorPos.getText().toString());
            else if (sPos.getSelectedItem().equals(getString(R.string.oparriba)))
                arriba = Float.parseFloat(valorPos.getText().toString());
            else if (sPos.getSelectedItem().equals(getString(R.string.opder)))
                der = Float.parseFloat(valorPos.getText().toString());
            else
                abajo = Float.parseFloat(valorPos.getText().toString());
        }
        else{
            if(sPos.getSelectedItem().equals("X1"))
                x = Float.parseFloat(valorPos.getText().toString());
            else if (sPos.getSelectedItem().equals("Y1"))
                y = Float.parseFloat(valorPos.getText().toString());
            else if (sPos.getSelectedItem().equals("X2"))
                x2 = Float.parseFloat(valorPos.getText().toString());
            else
                y2 = Float.parseFloat(valorPos.getText().toString());
        }
    }

    public void mostrarAleatorio(){
        //Generar un dibujo de forma aleatoria:
        //Generar un color
        valorR.setText(Integer.toString((int)(Math.random()*255)));
        valorG.setText(Integer.toString((int)(Math.random()*255)));
        valorB.setText(Integer.toString((int)(Math.random()*255)));
        //Escoger un estilo
        sEstilo.setSelection((int)(Math.random()*2));
        //Escoger una figura
        sFigura.setSelection((int)(Math.random()*3));
        //Escoger posicion
        if(sFigura.getSelectedItem().equals(getString(R.string.opcircle))) {
            x = (float)(Math.random()*600);
            y = (float)(Math.random()*600);
            radio = (float)(Math.random()*600);
        }
        else if(sFigura.getSelectedItem().equals(getString(R.string.opovalo)) ||sFigura.getSelectedItem().equals(getString(R.string.oprectangle))){
            izq = (float)(Math.random()*600);
            arriba = (float)(Math.random()*600);
            der = (float)(Math.random()*600);
            abajo = (float)(Math.random()*600);
        }
        else{
            x = (float)(Math.random()*600);
            y = (float)(Math.random()*600);
            x2 = (float)(Math.random()*600);
            y2 = (float)(Math.random()*600);
        }
        generar();
    }

    public void reiniciarValoresPosicion(){
        x=y=radio=x2=y2=izq=arriba=der=abajo=0f;
    }

    //Clase Mural
    class Mural extends View {
        Paint pincel;
        public Mural(Context context) {
            super(context);
            pincel = new Paint();
        }

        public void dibujar(Canvas canvasMural){
            //Color del pincel
            //Controlar que los canales RGB esten entre 0 y 255
            if(Integer.parseInt(valorR.getText().toString())<0 || Integer.parseInt(valorR.getText().toString()) > 255)
                valorR.setText("0");
            if(Integer.parseInt(valorG.getText().toString())<0 || Integer.parseInt(valorG.getText().toString()) > 255)
                valorG.setText("0");
            if(Integer.parseInt(valorB.getText().toString())<0 || Integer.parseInt(valorB.getText().toString()) > 255)
                valorB.setText("0");
            pincel.setARGB(255,
                    Integer.parseInt(valorR.getText().toString()), Integer.parseInt(valorG.getText().toString()), Integer.parseInt(valorB.getText().toString()));
            //Estilo
            if(sEstilo.getSelectedItemPosition()==0)
                pincel.setStyle(Paint.Style.FILL);
            else if (sEstilo.getSelectedItemPosition()==1)
                pincel.setStyle(Paint.Style.STROKE);
            else
                pincel.setStyle(Paint.Style.FILL_AND_STROKE);

            //Figura
            if(sFigura.getSelectedItemPosition()==0)
                canvasMural.drawCircle(x,y,radio,pincel);

            else if(sFigura.getSelectedItemPosition()==1) {
                RectF rectf = new RectF(izq, arriba, der, abajo);
                canvasMural.drawOval(rectf, pincel);
            }
            else if(sFigura.getSelectedItemPosition()==2)
                canvasMural.drawRect(izq, arriba, der, abajo,pincel);
            else
                canvasMural.drawLine(x,y,x2,y2,pincel);

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(baseDibujo,0,0,null);
        }
    }
}
