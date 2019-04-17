package ittepic.com.mx.tpdm_u3_practica1_oscar_ibaez_loreto;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {
    FirebaseFirestore servicioBaseDatos;

    private List<Prestamo> prestamo;
    private List<String> ramas;

    ListView lista2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        servicioBaseDatos = FirebaseFirestore.getInstance();

        lista2 = findViewById(R.id.lista2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                metodoMensajeInsertar();
            }
        });
    }// onCreate

    @Override
    protected void onStart() {
        super.onStart();
        consultarTodos();
    }// onStart

    public void metodoMensajeInsertar() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        View vista = getLayoutInflater().inflate(R.layout.plantillaprestar, null);

        final EditText campoObjeto = vista.findViewById(R.id.objeto);
        final EditText campoDes = vista.findViewById(R.id.descripcion);
        final EditText campofecha = vista.findViewById(R.id.fecha);
        final EditText campoPersona = vista.findViewById(R.id.persona);

        alerta.setTitle("Agregar prestamo")
                .setView(vista)
                .setPositiveButton("Insertar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertarFirestore(campoObjeto.getText().toString(),
                                campoDes.getText().toString(),
                                campofecha.getText().toString(),
                                campoPersona.getText().toString()

                        );
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }// metodoMensaje

    private void insertarFirestore(String objeto, String descripcion, String fechaentrega, String persona) {


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = mdformat.format(calendar.getTime());

        if (objeto.isEmpty() || descripcion.isEmpty() || fechaentrega.isEmpty() || persona.isEmpty()) {
            mensajes("Llene todos los campos!");
            return;
        }

        Prestamo record = new Prestamo("", objeto, descripcion, strDate, fechaentrega, persona);

        servicioBaseDatos.collection("prestamos")
                .add(record)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mensajes("Prestamo creado!");
                        consultarTodos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mensajes("Error al crear prestamo!");
                    }
                });
    }

    public void consultarTodos() {
        servicioBaseDatos.collection("prestamos")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                prestamo = new ArrayList<>();
                ramas = new ArrayList<>();

                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        mensajes("SIN DATOS");
                    }

                    for (QueryDocumentSnapshot registro : task.getResult()) {
                        Map<String, Object> datos = registro.getData();

                        Prestamo record = new Prestamo(
                                "",
                                datos.get("objeto").toString(),
                                datos.get("descripcion").toString(),
                                datos.get("fechaprestamo").toString(),
                                datos.get("fechaentrega").toString(),
                                datos.get("persona").toString()
                        );
                        ramas.add(registro.getId());

                        prestamo.add(record);
                    }
                    crearListView();

                }

            }// onComplete

        });

    } // consultarTodos

    private void crearListView() {
        String[] datos = null;
        ArrayAdapter<String> adapter = null;

        if (prestamo.size() == 0) {

            datos = new String[1];
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
            //return;
        } else {

            datos = new String[prestamo.size()];

            for (int i = 0; i < datos.length; i++) {
                Prestamo record = prestamo.get(i);
                datos[i] = record.getObjeto() + "\n" + record.getFechaprestamo();
            }

            lista2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    consultarDocumento(ramas.get(position));
                }
            });
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        }

        lista2.setAdapter(adapter);

    }

    private void consultarDocumento(final String clave) {
        System.out.println("CLAVE: " + clave);
        DocumentReference docRef = servicioBaseDatos.collection("prestamos").document(clave);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String datos;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    datos = "" + document.getString("objeto") +
                            ",,,," + document.getString("descripcion") +
                            ",,,," + document.getString("fechaprestamo") +
                            ",,,," + document.getString("persona");
                    // System.out.println("DATOS METHOD "+aux);
                    metodoMensajeActualizar(clave, datos);
                } else {
                    Log.d("DATOS->", "Error ", task.getException());
                }
            }
        });
    }

    private void metodoMensajeActualizar(final String clave, String datos) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        View vista = getLayoutInflater().inflate(R.layout.plantillaprestar, null);

        System.out.println("CLAVE REGISTRO: " + clave);
        final EditText campoObjeto = vista.findViewById(R.id.objeto);
        final EditText campoDes = vista.findViewById(R.id.descripcion);
        final EditText campofecha = vista.findViewById(R.id.fecha);
        final EditText campoPersona = vista.findViewById(R.id.persona);


        //String datos = consultarDocumento(clave);

        // System.out.println("DATOS ASD" + datos);
        final String[] val = datos.split(",,,,");


        campoObjeto.setText(val[0]);
        campoDes.setText(val[1]);
        campofecha.setText(val[2]);
        campoPersona.setText(val[3]);

        campoObjeto.setSelection(campoObjeto.getText().length());


        alerta.setTitle("Actualizar prestamo")
                .setView(vista)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Actualizar
                        actualizarDocumento(clave,
                                campoObjeto.getText().toString(),
                                campoDes.getText().toString(),
                                campofecha.getText().toString(),
                                campoPersona.getText().toString());
                    }
                })
                .setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //System.out.println("ELIMINAR");
                        eliminarDocumento(clave);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }// metodoMensaje

    private void actualizarDocumento(String clave, String titulo, String des, String cad, String priori) {

        Map<String, Object> datos = new HashMap<>();
        datos.put("objeto", titulo);
        datos.put("descripcion", des);
        datos.put("fechaentrega", cad);
        datos.put("persona", priori);
        servicioBaseDatos.collection("prestamos").document(clave)
                .update(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensajes("Prestamo actualizado!");
                        consultarTodos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mensajes("Error al actualizar!");
                    }
                });
    }

    private void eliminarDocumento(String clave) {

        servicioBaseDatos.collection("prestamos").document(clave).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensajes("Borrado correctamente!");
                        consultarTodos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void mensajes(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }// mensajes

}// class
