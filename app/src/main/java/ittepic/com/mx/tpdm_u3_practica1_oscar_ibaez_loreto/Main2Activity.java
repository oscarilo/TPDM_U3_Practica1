package ittepic.com.mx.tpdm_u3_practica1_oscar_ibaez_loreto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class Main2Activity extends AppCompatActivity {
    DBA databaseAdministrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseAdministrator = new DBA(this, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseAdministrator.metodoMensajeInsertar();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }// onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.prestamos:
                Intent prestamos = new Intent(this, Main3Activity.class);
                startActivity(prestamos);
        }
        return super.onOptionsItemSelected(item);
    }// onOptionsItemSelected

    @Override
    protected void onStart() {
        super.onStart();
        databaseAdministrator.consultarTodos();
    }// onStart

}
