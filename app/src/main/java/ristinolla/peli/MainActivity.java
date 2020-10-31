package ristinolla.peli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] napit = new Button[4][4]; //luodaan kaksiulotteinen taulukko nappeja varten
    private Button buttonSaannot;
    private boolean p1Vuoro = true;  //boolean muuttujan avulla tarkistetaan kumman pelaajan vuoro
    private int vuoronLasku;         //vuoronlaskulla pidetään kirjaa siitä, montako vuoroa on käytetty
    private int p1Pisteet=0;           //pelaajan 1 pisteet laskuri
    private int p2Pisteet=0;          //pelaajan 2 pisteet laskuri
    private TextView textViewP1;    //tekstikentät jossa pisteet näytetään
    private TextView textViewP2;
    private String p1nimi;                  //muuttujat joihin haetaan edellisen activityn pelaajanimet
    private String p2nimi;
    private MediaPlayer mp;                   //määritellään mediaplayer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewP1 = findViewById(R.id.text_view_p1);  //asetetaan oikeat tekstikentät
        textViewP2 = findViewById(R.id.text_view_p2);
        Intent intent = getIntent();                   //haetaan explicit intentin avulla nimet ja asetetaan muuttujiin ja tekstikenttiin
        p1nimi = (String)intent.getSerializableExtra("key1");
        p2nimi = (String)intent.getSerializableExtra("key2");
        updatePointsText(); //asetetaan aloituspisteet


        for (int i = 0; i < 4; i++) {   //käydään taulukko läpi
            for (int j = 0; j < 4; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                napit[i][j] = findViewById(resID); //
                napit[i][j].setOnClickListener(this); //asetetaan kaikkiin taulukon nappeihin listener
            }
        }
        buttonSaannot = (Button) findViewById(R.id.button_rules);
        buttonSaannot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avaaDialogi();
            }
        });
        Button buttonReset = findViewById(R.id.button_reset);   //luodaan reset nappi ja asetetaan listener
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //kun reset nappia painetaan, kutsutaan resetGame-metodia
                resetGame();
            }
        });
    }
    @Override
    public void onClick(View v) {  //kun pelialueen nappeja painetaan, tarkistetaan ensin että onhan nappi "tyhjä"

        if (!((Button) v).getText().toString().equals("")) {
            playSound(1); //soitetaan virheääni
            return;  //jos nappi ei ole tyhjä, palataan
        }

        playSound(2); //soitetaan merkkiääni kun asetetaan kirjain
        if (p1Vuoro) {    //jos nappi on tyhjä, seuraavaksi tarkistetaan kumman vuoro on ja merkitään oikea kirjain X tai O nappiin
            ((Button) v).setText("X");
        } else {
            ((Button) v).setText("O");
        }
        vuoronLasku++;     //kasvatetaan käytettyjä vuoroja
        if (tarkistaVoitto()) {  //tarkistetaan tarkistavoitto metodilla onko peli jo päättynyt, jos true, niin tarkistetaan voittaja viimeisen vuoron perusteella

            if (p1Vuoro) {    //jos viimeisimmän vuoron on käyttänyt pelaaja 1, on hän voittaja
                playerWins(1);
            } else {
                playerWins(2);  //muussa tapauksessa voittaja on pelaaja 2
            }
        } else if (vuoronLasku == 16) {  //jos kaikki vuorot on käytetty, eikä voittaja ole ratkennut on peli mennyt tasan
            playerWins(0);
        } else {
            p1Vuoro = !p1Vuoro; //jos vuoroja on vielä jäljellä, vaihdetaan pelaajaa
        }
    }
    private boolean tarkistaVoitto() {   //tarkistetetaan voittaja - metodi
        String[][] field = new String[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                field[i][j] = napit[i][j].getText().toString(); //luetaan kaikkien nappien senhetkinen tekstien sisältö aputaulukkoon for loopissa.
            }
        }
        for (int i = 0; i < 4; i++) {   //tarkistetaan for loopissa kaikki vaakarivit taulukosta    0.0  0.1  0.2  0.3
            if (field[i][0].equals(field[i][1]) //ovatko arvot samat                           //   1.0  1.1  1.2  1.3
                    && field[i][0].equals(field[i][2])                                          //  2.0  2.1  2.2  2.3
                    && field[i][0].equals(field[i][3])                                           // 3.0  3.1  3.2  3.3
                    && !field[i][0].equals("")) {  //varmistetaan ettei arvot ole kuitenkaan tyhjiä
                return true;   //palautetaan tosi, jos koko rivi on samaa arvoa ja ei kuitenkaan tyhjää. (peli siis voitettu)
            }
        }
        for (int i = 0; i < 4; i++) {                //saman taulukon läpi käynti myös pystyrivein
            if (field[0][i].equals(field[1][i])
                    && field[0][i].equals(field[2][i])
                    && field[0][i].equals(field[3][i])
                    && !field[0][i].equals("")) {
                return true;  //palautetaan tosi, peli voitettu
            }
        }
        if (field[0][0].equals(field[1][1])    //käydään läpi taulukko kulmasta kulmaan ristiin vasemmalta oikealle
                && field[0][0].equals(field[2][2])
                && field[0][0].equals(field[3][3])
                && !field[0][0].equals("")) {
            return true;   //palautetaan tosi, peli voitettu
        }
        if (field[0][3].equals(field[1][2])   //kulmasta kulmaan ristiin oikealta vasemmalle
                && field[0][3].equals(field[2][1])
                && field[0][3].equals(field[3][0])
                && !field[0][3].equals("")) {
            return true;  //palautetetaan tosi, peli voitettu
        }
        for(int i=0; i<3; i++) {                           //sisäkkäiset for-loopit, käydään läpi koko taulukko
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals(field[i][j + 1])   //tarkistetaan taulukko "neliöissä", kolmessa neliö "rivissä"
                        && field[i][j].equals(field[i + 1][j])
                        && field[i][j].equals(field[i + 1][j + 1])
                        && !field[i][j].equals("")) {
                    return true;  //palautetetaan tosi, peli voitettu
                }

            }
        }


            return false;  //muussa tapauksessa, jos ehdot eivät täyty, palautetaan epätosi (peli jatkuu, tai meni tasan)
    }
    private void playerWins(int i) {  //parametrin avulla if-lauseissa saadaan selville voittaja
        int a = i;

        if(a==1) {      //pelaaja 1 voittaa
            playSound(3); //soitetaan voiton merkkiääni
            p1Pisteet++;  //kasvatetaan p1 pisteitä
            Toast.makeText(this, p1nimi + " voittaa!", Toast.LENGTH_SHORT).show();  //näytetään Toast
        }
        else if(a==2) {     //pelaaja 2 voittaa
            playSound(3); // soitetaan voiton merkkiääni
            p2Pisteet++;  //kasvatetaan p2 pisteitä
            Toast.makeText(this, p2nimi + " voittaa!", Toast.LENGTH_SHORT).show();  //näytetään Toast
        }
        else{                //tasapeli
            playSound(4); //soitetaan tasapelin merkkiääni
            Toast.makeText(this, "Tasapeli!", Toast.LENGTH_SHORT).show(); //näytetään toast
        }
        updatePointsText(); //kutsutaan pisteiden päivitysmetodia
        resetBoard(); //alustetaan pelilauta uudelle pelille
    }

    private void updatePointsText() {
        textViewP1.setText(  p1nimi + ": " + p1Pisteet);
        textViewP2.setText(  p2nimi + ": " + p2Pisteet);
    }
    private void resetBoard() {  //alustetaan pelilauta, tyhjätään nappien tekstit for loopissa
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                napit[i][j].setText("");
            }
        }
        vuoronLasku = 0;    //nollataan vuoron laskemismuuttuja
        p1Vuoro = true;     //asetetaan aloitusvuoro pelaajalle 1
    }
    private void playSound(int i){          //playsound metodi alustaa mediaplayerin parametrin avulla ja soittaa oikean äänen
        int a = i;
        if(a==1){
            mp = MediaPlayer.create(this, R.raw.wrong);
        }
        else if(a==2){
            mp = MediaPlayer.create(this, R.raw.button02);
        }
        else if(a==3){
            mp = MediaPlayer.create(this, R.raw.victory);
        }
        else if(a==4){
            mp = MediaPlayer.create(this, R.raw.tasapeli);
        }
        else{
            mp = MediaPlayer.create(this, R.raw.reset);
        }
        mp.start();
    }
    public void avaaDialogi(){
        Rules rules = new Rules();
        rules.show(getSupportFragmentManager(), "säännöt");
    }
    private void resetGame() {  //resetoidaan koko peli
        playSound(0); //soitettaan resetin merkkiääni
        p1Pisteet = 0;  //nollataan pisteet
        p2Pisteet = 0;
        updatePointsText();  //päivitetään tekstikenttiin
        resetBoard();  //kutsutaan alusta pelilauta metodia
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vuoronLasku", vuoronLasku);
        outState.putInt("p1Pisteet", p1Pisteet);
        outState.putInt("p2Pisteet", p2Pisteet);
        outState.putBoolean("p1Vuoro", p1Vuoro);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        vuoronLasku = savedInstanceState.getInt("vuoronLasku");
        p1Pisteet = savedInstanceState.getInt("p1Pisteet");
        p2Pisteet = savedInstanceState.getInt("p2Pisteet");
        p1Vuoro = savedInstanceState.getBoolean("p1Vuoro");
    }
}
