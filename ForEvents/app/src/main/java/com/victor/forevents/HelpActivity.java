package com.victor.forevents;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class HelpActivity extends AppCompatActivity {

    public EditText mensaje_ayuda;
    public Button btn_enviar;

    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        mensaje_ayuda = (EditText) findViewById(R.id.mensaje_ayuda);
        btn_enviar = (Button) findViewById(R.id.btn_enviar);

        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mensaje_ayuda.getText().toString().isEmpty()) {
                    sendEmail();

                }else{
                    Toast.makeText(HelpActivity.this,"Debes introducir contenido en el mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmail() {
        String mail = "foreventstfg@gmail.com";
        String asunto = "Ayuda a usuario";
        String mensaje = mensaje_ayuda.getText().toString();

        //Enviamos el email
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail,asunto, mensaje);
        javaMailAPI.execute();

        mensaje_ayuda.setText("");

    }
}
