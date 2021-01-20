package com.example.sanroque_consultor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.elo.device.DeviceManager;
import com.elo.device.enums.BcrEnableControl;
import com.elo.device.inventory.Inventory;
import com.elo.device.peripherals.BarCodeReader;
import com.example.sanroque_consultor.Clases.Producto;
import com.example.sanroque_consultor.ParsearXML.ParserXmlElo;
import com.example.sanroque_consultor.apiadapter.ActivityMonitor;
import com.example.sanroque_consultor.apiadapter.ApiAdapter;
import com.example.sanroque_consultor.apiadapter.ApiAdapterFactory;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.IllegalFormatCodePointException;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ConsultorPrecioActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private boolean boleananimationview = true;
    private boolean boleananimationbusqeudaview = false;
    private boolean boleanlinearprecio = false;

    private OkHttpClient Pickinghttp;
    private String sucursal = "2";
    private String m_ip = "200.40.253.210";
    private Handler m_handler = new Handler(); // Main thread
    private Request RequestPicking;
    private ProgressDialog dialog;
    private Button btnon, btnoff;
    private EditText editcodigobarramanual;

    private TextView txtdescripcion1, txtdescripcion2, txtprecioproducto, txtcodigoproducto, txtcodigobarraproducto;


    ActionBar actionBar;
    ConstraintLayout constrain;
    LinearLayout linearprecio;
    ImageView imgescaner;
    LottieAnimationView animationview;
    LottieAnimationView animationbusquedaview;
    boolean visible = false;


    private Inventory inventory;
    private ApiAdapter apiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultor_precio);

        constrain = findViewById(R.id.constainlayot);
        actionBar = getSupportActionBar();
        linearprecio = findViewById(R.id.linear_precio);
        animationview = findViewById(R.id.animation_view);
        animationbusquedaview = findViewById(R.id.animationbusqueda_view);

        btnon = findViewById(R.id.btn_on);
        btnoff = findViewById(R.id.btn_off);
        editcodigobarramanual = findViewById(R.id.edit_codigo_barra_manual);

        txtdescripcion1 = findViewById(R.id.txt_descripcion_1);
        txtdescripcion2 = findViewById(R.id.txt_descripcion_2);
        txtprecioproducto = findViewById(R.id.txt_precio_producto);
        txtcodigoproducto = findViewById(R.id.txt_codigo_producto);
        txtcodigobarraproducto = findViewById(R.id.txt_codigo_barra_producto);

        constrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidebarras();
            }
        });

        btnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiAdapter.setBarCodeReaderEnabled(true);

                //  showlinearprecio();

             // EnableDialog(true,"cargando",false);

            }
        });

        btnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiAdapter.setBarCodeReaderEnabled(false);
                //  hidelinearprecio();
               // delayedHide(AUTO_HIDE_DELAY_MILLIS);
               // EnableDialog(true,"mostrando",false);

            }
        });


        editcodigobarramanual.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean procesado = false;

                if (i == KeyEvent.KEYCODE_ENTER || i == KeyEvent.KEYCODE_TAB){
                    presionarboton();
                    procesado = true;
                }



                return procesado;
            }
        });


        editcodigobarramanual.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {

                    presionarboton();
                    procesado = true;

                }
                return procesado;
            }
        });



        inventory();
        hidebarras();
        cargardatospreference();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnBcrOff();
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_RESUME);

        //updatePaperStatus();

    }

    @Override
    protected void onPause() {
        super.onPause();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_PAUSE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiAdapter.getActivityMonitor().onActivityEvent(ActivityMonitor.EVENT_ON_STOP);
    }

    public void turnBcrOff() {
        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {
            if (inventory.barCodeReaderSupportsVComMode()) {

                // Can't do much otherwise
                apiAdapter.setBarCodeReaderEnabled(false);
                apiAdapter.setBarCodeReaderCallback(null);

            } else {
                if (apiAdapter.isBarCodeReaderEnabled()) {
                    apiAdapter.setBarCodeReaderEnabled(false);
                }
            }
        }
    }

    private void inventory() {

        inventory = DeviceManager.getInventory(this);



        if (!inventory.isEloSdkSupported()) {
            Toast.makeText(this, "Platform not recognized or supported, sorry", Toast.LENGTH_LONG).show();
        }

        // productInfo = DeviceManager.getPlatformInfo();
        // EloPlatform platform = productInfo.eloPlatform;

        apiAdapter = ApiAdapterFactory.getInstance(this).getApiAdapter(inventory);

        if (apiAdapter == null) {
            Log.d("TAF", "Cannot find support for this platform");
            Toast.makeText(this, "Cannot find support for this platform", Toast.LENGTH_LONG).show();
        }

        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    turnBcrOn();

                }
            });
        }

    }


    private void presionarboton() {

        String codigoimprimir = editcodigobarramanual.getText().toString().trim();
        if (!codigoimprimir.equals("")) {
            request(codigoimprimir);
            editcodigobarramanual.setText("");
            ocultarteclado();
        }
        if (editcodigobarramanual.isFocused()) {
            ocultarteclado();
        }
    }

    public void ocultarteclado() {

        View view = this.getCurrentFocus();

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        hidebarras();
    }


    public void turnBcrOn() {
        if (inventory.barCodeReaderEnableControl() == BcrEnableControl.FULL) {
            if (inventory.barCodeReaderSupportsVComMode()) {

                // Can't do much otherwise
                apiAdapter.setBarCodeReaderCallback(callback);
                apiAdapter.setBarCodeReaderEnabled(true);

            } else {
                if (!apiAdapter.isBarCodeReaderEnabled()) {
                    apiAdapter.setBarCodeReaderEnabled(true);
                }
            }
        }
    }

    private BarCodeReader.BarcodeReadCallback callback = new BarCodeReader.BarcodeReadCallback() {
        @Override
        public void onBarcodeRead(byte[] bytes) {
            String output;

            try {
                output = new String(bytes, "US-ASCII");
            } catch (UnsupportedEncodingException e) {
                output = "--UnReadable--";
            }
            final String outputCopy = output;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    request(outputCopy);

                    // txtbarcoderreader.setText(outputCopy);
                }
            });

        }
    };


    public void request(final String codigoverificar) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final String codigocaptrado = codigoverificar;

                //final com.example.sanroque_consultor.Clases.Producto[] prod = {new Producto()};

                Pickinghttp = new OkHttpClient();
                MediaType mediaType = MediaType.parse("text/xml");

                RequestBody body = RequestBody.create(mediaType,
                        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" +
                                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                                "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n " +
                                " <soap:Body>\r\n  " +
                                "  <ObtenerDatosArticuloEtiquetas xmlns=\"http://tempuri.org/\">\r\n " +
                                "     <p_suc>" + sucursal + "</p_suc>\r\n    " +
                                "  <p_producto>" + codigocaptrado + "</p_producto>\r\n " +
                                "   </ObtenerDatosArticuloEtiquetas>\r\n " +
                                " </soap:Body>\r\n" +
                                "</soap:Envelope>\r\n\r\n");

                RequestPicking = new Request.Builder()
                        .url("http://" + m_ip + "/WSSREtiquetas/EtiquetaService.asmx")
                        .post(body)
                        .addHeader("Content-Type", "text/xml")
                        .addHeader("User-Agent", "PostmanRuntime/7.18.0")
                        .addHeader("Accept", "*/*")
                        .addHeader("Cache-Control", "no-cache")
                        .addHeader("Postman-Token", "a76f7625-a2c8-4806-853a-877dff25011f,faebb6f7-d421-44eb-8a54-eb1786e95136")
                        .addHeader("Host", m_ip)
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Content-Length", "428")
                        .addHeader("Connection", "keep-alive")
                        .addHeader("cache-control", "no-cache")
                        .build();

                EnableDialog(true, "cargando", false);


                Pickinghttp.newCall(RequestPicking).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        EnableDialog(false, "limpiando", false);
                        DisplayPrintingStatusMessage("Conexion Fallo");
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {

                        if (response.isSuccessful()) {
                            try {

                                final String myResponse = response.body().string();

                                ParserXmlElo parserXmlPicking = new ParserXmlElo(ConsultorPrecioActivity.this);

                                Document doc = toXmlDocument(myResponse);
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                Source xmlSource = new DOMSource(doc);
                                Result outputTarget = new StreamResult(outputStream);
                                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

                                Producto a = parserXmlPicking.parsear(is);

                                mostrar_datos_view(a);

                               // DisplayPrintingStatusMessage(a.getDescArticulo_1().toString());

                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                EnableDialog(false, "mostrando", false);

                            } catch (XmlPullParserException | ParserConfigurationException | SAXException | TransformerException e) {
                                e.printStackTrace();
                                EnableDialog(false, "limpiando", false);
                            }


                        } else {

                            DisplayPrintingStatusMessage("Error con la conexion Wifi.. Reintentar");
                            EnableDialog(false, "limpiando", false);

                        }
                    }

                });
            }

        });

    }

    void hidebarras() {
        constrain.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (actionBar != null) {
            actionBar.hide();
        }
        visible = true;
    }


    public void EnableDialog(final boolean value, final String mensaje, final Boolean cancelar) {
        m_handler.post(new Runnable() {
            @Override
            public void run() {
                if (mensaje.equals("cargando")) {

                    animationview.setVisibility(View.INVISIBLE);
                    animationbusquedaview.setVisibility(View.VISIBLE);
                    linearprecio.setVisibility(View.INVISIBLE);

                    boleananimationview = false;
                    boleananimationbusqeudaview = true;
                    boleanlinearprecio = false;

                    Log.e("ENTRO A","ENTRO1");

                } else if (mensaje.equals("mostrando")){

                    animationview.setVisibility(View.INVISIBLE);
                    animationbusquedaview.setVisibility(View.INVISIBLE);
                    linearprecio.setVisibility(View.VISIBLE);

                    boleananimationview = false;
                    boleananimationbusqeudaview = false;
                    boleanlinearprecio = true;

                    Log.e("ENTRO B","ENTRO1");
                    ocultando();

                }else{
                    //limpiando
                    animationview.setVisibility(View.VISIBLE);
                    animationbusquedaview.setVisibility(View.INVISIBLE);
                    linearprecio.setVisibility(View.INVISIBLE);

                    boleananimationview = true;
                    boleananimationbusqeudaview = false;
                    boleanlinearprecio = false;

                    Log.e("ENTRO C","ENTRO1");
                }
            }
        });
    }


    private final Handler mHideHandler = new Handler();

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {

            animationview.setVisibility(View.VISIBLE);
            animationbusquedaview.setVisibility(View.INVISIBLE);
            linearprecio.setVisibility(View.INVISIBLE);

            boleananimationview = true;
            boleananimationbusqeudaview = false;
            boleanlinearprecio = false;

            Log.e("ENTRO H2","ENTRO1");
        }
    };

    private void cargardatospreference() {
        Bundle parametros = getIntent().getExtras();
        if (parametros != null) {
            sucursal = (parametros.getString("suc"));
            m_ip = (parametros.getString("ip"));

        } else {
            sucursal = (parametros.getString("suc"));
            m_ip = (parametros.getString("ip"));
            Toast.makeText(getApplicationContext(), "No hay datos a mostrar", Toast.LENGTH_LONG).show();
        }
    }



    private void ocultando() {
        Log.e("ENTRO H","ENTRO1");
        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 20000);
    }

    private static Document toXmlDocument(String str) throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(str)));
        return document;

    }

    public void DisplayPrintingStatusMessage(final String MsgStr) {

        m_handler.post(new Runnable() {
            public void run() {
                showToast(MsgStr);//2018 PH
            }// run()
        });

    }

    public void showToast(final String toast) {
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private void mostrar_datos_view(final Producto a) {


        m_handler.post(new Runnable() {
            public void run() {

                if (a != null) {

                    try {
                        String oferta = "";
                        txtdescripcion1.setText(a.getDescArticulo_1());
                        txtdescripcion2.setText(a.getDescArticulo_2());
                        txtcodigoproducto.setText(a.getCodProd());
                        txtcodigobarraproducto.setText(a.getCodBarras());
                        txtprecioproducto.setText(a.getPrecio());
                        oferta = (a.getTxt_oferta());


                        if (a.getOff_available().toString().equals("N")) {

                            oferta = "";
                            linearprecio.setBackground(ContextCompat.getDrawable(ConsultorPrecioActivity.this, R.drawable.ic_tag_60x30_consultor_impreso));

                        } else {
                            oferta = a.getTxt_oferta();
                            linearprecio.setBackground(ContextCompat.getDrawable(ConsultorPrecioActivity.this, R.drawable.ic_tag_60x30_amarillo));
                        }
                        // showlinearprecio();

                       // DisplayPrintingStatusMessage("correcto");

                    } catch (Exception e) {
                        DisplayPrintingStatusMessage("No disponible");
                        txtdescripcion1.setText("");
                        txtdescripcion2.setText("");
                        txtcodigoproducto.setText("");
                        txtcodigobarraproducto.setText("");
                        txtprecioproducto.setText("");

                        // hidelinearprecio();
                    }


                }
            }// run()
        });


    }

    private void iniciaranimaciones(boolean activiad) {

        //handleranimaciones.removeCallbacks(runableanimaciones);

        if (activiad) {

            animationview.setVisibility(View.INVISIBLE);
            animationbusquedaview.setVisibility(View.VISIBLE);


        } else {

        }


    }






    private void reinciairanimaciones(){
        animationview.setVisibility(View.VISIBLE);
        animationbusquedaview.setVisibility(View.INVISIBLE);
        linearprecio.setVisibility(View.INVISIBLE);

        boleananimationview = true;
       boleananimationbusqeudaview = false;
      boleanlinearprecio = false;
    }


    private void hide() {
        // Hide UI first

        animationview.setVisibility(View.INVISIBLE);


    }

    private void show() {
        // Show the system bar
        linearprecio.setVisibility(View.VISIBLE);


    }


/*
    void showlinearprecio() {

        linearprecio.setVisibility(View.VISIBLE);
        animationview.setVisibility(View.INVISIBLE);

    }

    void hidelinearprecio() {
        linearprecio.setVisibility(View.INVISIBLE);
        animationview.setVisibility(View.VISIBLE);
    }
    */

}