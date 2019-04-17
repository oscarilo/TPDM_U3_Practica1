package ittepic.com.mx.tpdm_u3_practica1_oscar_ibaez_loreto;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class DBA {

    private ListView lista;
    private FirebaseFirestore servicioBaseDatos;
    private List<Recordatorio> recordatorio;
    private List<String> ramas;
    private Activity activityG;
    private Context contextG;

    public DBA(Activity activity, Context context) {
        servicioBaseDatos = FirebaseFirestore.getInstance();
        activityG = activity;
        contextG = context;

        lista = activityG.findViewById(R.id.lista);
    }

    public void consultarTodos() {
        servicioBaseDatos.collection("recordatorios").orderBy("prioridad")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                recordatorio = new ArrayList<>();
                ramas = new ArrayList<>();

                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        mensajes("SIN DATOS");
                    }

                    for (QueryDocumentSnapshot registro : task.getResult()) {
                        Map<String, Object> datos = registro.getData();

                        Recordatorio record = new Recordatorio(
                                "",
                                datos.get("titulo").toString(),
                                datos.get("descripcion").toString(),
                                datos.get("creacion").toString(),
                                datos.get("prioridad").toString(),
                                datos.get("caducidad").toString()
                        );
                        ramas.add(registro.getId());

                        recordatorio.add(record);
                    }
                    crearListView();

                }

            }// onComplete

        });

    } // consultarTodos

    private void crearListView() {
        String[] datos = null;
        ArrayAdapter<String> adapter = null;

        if (recordatorio.size() == 0) {

            datos = new String[1];
            adapter = new ArrayAdapter<>(contextG, android.R.layout.simple_list_item_1, datos);
            //return;
        } else {

            datos = new String[recordatorio.size()];

            for (int i = 0; i < datos.length; i++) {
                Recordatorio record = recordatorio.get(i);
                datos[i] = record.getTitulo() + "\n" + record.getCaducidad();
            }

            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    consultarDocumento(ramas.get(position));
                }
            });
            adapter = new ArrayAdapter<>(contextG, android.R.layout.simple_list_item_1, datos);
        }

        lista.setAdapter(adapter);

    }

    private void consultarDocumento(final String clave) {
        System.out.println("CLAVE: "+clave);
        DocumentReference docRef = servicioBaseDatos.collection("recordatorios").document(clave);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final String datos;
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    datos = "" + document.getString("titulo") +
                            ",,,," + document.getString("descripcion") +
                            ",,,," + document.getString("caducidad") +
                            ",,,," + document.getString("prioridad");
                    // System.out.println("DATOS METHOD "+aux);
                    metodoMensajeActualizar(clave, datos);
                } else {
                    Log.d("DATOS->", "Error ", task.getException());
                }
            }
        });
    }

    public void metodoMensajeInsertar() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(contextG);
        View vista = activityG.getLayoutInflater().inflate(R.layout.plantillainsertar, null);

        final EditText campoTitulo = vista.findViewById(R.id.titulo);
        final EditText campoDes = vista.findViewById(R.id.descripcion);
        final EditText campoCaducidad = vista.findViewById(R.id.caducidad);
        final Spinner campoPrioridad = vista.findViewById(R.id.prioridad);

        alerta.setTitle("Insertar recordatorio")
                .setView(vista)
                .setPositiveButton("Insertar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertarFirestore(campoTitulo.getText().toString(),
                                campoDes.getText().toString(),
                                campoCaducidad.getText().toString(),
                                campoPrioridad.getSelectedItem().toString()
                        );
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }// metodoMensaje

    private void insertarFirestore(String titulo, String descripcion, String caducidad, String prioridad) {


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = mdformat.format(calendar.getTime());

        if (titulo.isEmpty() || descripcion.isEmpty() || prioridad.isEmpty() || caducidad.isEmpty()) {
            mensajes("Llene todos los campos!");
            return;
        }

        Recordatorio record = new Recordatorio("", titulo, descripcion, strDate, prioridad, caducidad);

        servicioBaseDatos.collection("recordatorios")
                .add(record)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        mensajes("Recordatorio creado!");
                        consultarTodos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mensajes("Error al crear recordatorio!");
                    }
                });
    }

    private void metodoMensajeActualizar(final String clave, String datos) {
        AlertDialog.Builder alerta = new AlertDialog.Builder(activityG);
        View vista = activityG.getLayoutInflater().inflate(R.layout.plantillainsertar, null);

        System.out.println("CLAVE REGISTRO: " + clave);
        final EditText campoTitulo = vista.findViewById(R.id.titulo);
        final EditText campoDes = vista.findViewById(R.id.descripcion);
        final EditText campoCaducidad = vista.findViewById(R.id.caducidad);
        final Spinner campoPrioridad = vista.findViewById(R.id.prioridad);

        //String datos = consultarDocumento(clave);

        // System.out.println("DATOS ASD" + datos);
        final String[] val = datos.split(",,,,");

        switch (val[3]) {
            case "Normal":
                campoPrioridad.setSelection(0);
                break;

            case "Media":
                campoPrioridad.setSelection(1);
                break;
            case "Alta":
                campoPrioridad.setSelection(2);
                break;
        }

        campoTitulo.setText(val[0]);
        campoDes.setText(val[1]);
        campoCaducidad.setText(val[2]);

        campoTitulo.setSelection(campoTitulo.getText().length());


        alerta.setTitle("Actualizar recordatorio")
                .setView(vista)
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Actualizar
                        actualizarDocumento(clave,
                                campoTitulo.getText().toString(),
                                campoDes.getText().toString(),
                                campoCaducidad.getText().toString(),
                                campoPrioridad.getSelectedItem().toString());
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
        datos.put("titulo", titulo);
        datos.put("descripcion", des);
        datos.put("caducidad", cad);
        datos.put("prioridad", priori);
        servicioBaseDatos.collection("recordatorios").document(clave)
                .update(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensajes("Recordatorio actualizado!");
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

        servicioBaseDatos.collection("recordatorios").document(clave).delete()
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
        Toast.makeText(contextG, mensaje, Toast.LENGTH_LONG).show();
    }// mensajes
}
