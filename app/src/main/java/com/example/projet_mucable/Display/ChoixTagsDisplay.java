package com.example.projet_mucable.Display;

// Ajout et suppression des tags, 4.4 cdc

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projet_mucable.CustomAdapter;
import com.example.projet_mucable.R;
import com.example.projet_mucable.TagAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChoixTagsDisplay extends AppCompatActivity {

    View tag_view;
    String tagChosen = "NAN";
    SQLiteDatabase CDB;
    String[] tag_listDB;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choixtags_display);

        CDB = openOrCreateDatabase("CDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null );
        Cursor cTag = CDB.query(
                "t_TagColor",
                null,
                null,
                null,
                null,
                null,
                null
        );
        int tailleReq = cTag.getCount();
        tag_listDB = new String[tailleReq];
        int i = 0;

        cTag.moveToFirst();
        while(!cTag.isAfterLast()){
            //Log.d("testtest", " tag :"+c2.getString(1));
            tag_listDB[i] = cTag.getString(1);
            i++;
            cTag.moveToNext();
        }
        cTag.close();

        setupElements();
    }

    Map<String, String> getTagsColor(){

        Map<String,String> tagColMap = new HashMap<>();

        // on prends toutes les lignes de tags
        for(String tl : tag_listDB){
            // on recupere les tags de chaque lignes
            String[] tmpTags = tl.split(" - ");
            for(String t : tmpTags){
                // on verifier qu'ils sont pas nuls
                if(t != null && !t.equals("") && !t.equals("NAN") && !t.equals("NAN_NULL")){
                    tagColMap.put(t,"");
                }
            }
        }

        // de tout les tags recupérés on récupere la couleur
        for(String tag: tagColMap.keySet()){
            Cursor c = CDB.rawQuery("SELECT Couleur FROM t_TagColor WHERE Nom LIKE '"+tag+"'", null);
            c.moveToFirst();
            //Log.d("testtest",c.getString(0));
            tagColMap.put(tag,c.getString(0));
            c.close();
        }

        return tagColMap;
    }

    void setupElements() {
        setupListView();
    }

    void setupListView() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tags = preferences.getString("TAG_LIST", "EMPTY_NULL");
        final String[] tag_list = tag_listDB;

        if ( !tag_list[0].equals("EMPTY_NULL") ) {

            // création de l'objet SimpleCursorAdapter...
            TagAdapter adapter = new TagAdapter(getApplicationContext(), tag_listDB, getTagsColor());

            // ...qui va remplir l'objet ListView
            final ListView tags_listview = (ListView) findViewById(R.id.tags_listview);
            tags_listview.setAdapter(adapter);

            tags_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if ( !tagChosen.equals("NAN") ) {
                        tag_view.setBackgroundResource(0);
                    }
                    tag_view = view;
                    tag_view.setBackgroundResource(R.drawable.border);
                    tagChosen = tag_list[position];
                }
            });
        }
    }

    public void onClickChoiceTag(View view) {

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if ( !tagChosen.equals("NAN") ) {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogDarker));
            builder.setMessage("Êtes vous sur(e) de vouloir choisir le tag "+tagChosen+" ?")
                    .setCancelable(true)
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor NEW_TAGLIST = preferences.edit();
                            NEW_TAGLIST.putString("TAG_CHOSEN", tagChosen);
                            NEW_TAGLIST.commit();
                            finish();
                        }
                    })
                    .setNegativeButton("Non", null );
            AlertDialog alert = builder.create();
            alert.show();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogDarker));
            builder.setMessage("Vous n'avez choisit aucun tag !")
                    .setCancelable(true)
                    .setPositiveButton("Ok", null);
            AlertDialog alert = builder.create();
            alert.show();

        }

    }

}