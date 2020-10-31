package ristinolla.peli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PlayerNames extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_names);

        final EditText nimi1Txt = (EditText)findViewById(R.id.nimi1Txt);
        final EditText nimi2Txt = (EditText)findViewById(R.id.nimi2Txt);
        final Button btnAdd = (Button)findViewById(R.id.addBtn);
        final TextView herja = (TextView)findViewById(R.id.scdTxt);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                       //tapahtumankäsittelijä napinpainallukselle
                String nimi1 = nimi1Txt.getText().toString();
                String nimi2 = nimi2Txt.getText().toString();
                if (!nimi1.isEmpty() && !nimi2.isEmpty()){    //jos nimet sisältävät jotain tekstiä
                    herja.setText("");
                    Intent intent = new Intent(PlayerNames.this, MainActivity.class);  //explicit intentin avulla otetaan talteen tarvittavat muuttujat
                    intent.putExtra("key1", nimi1);
                    intent.putExtra("key2", nimi2);
                    startActivity(intent);                         //ja siirrytään seuraavaan activityyn
                }
                else{
                    herja.setText("Nimi ei voi olla tyhjä!");   //jos nimet ovat tyhjiä, tulostetaan herja
                }
            }
        });
    }
}