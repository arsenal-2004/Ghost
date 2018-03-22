/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    public TextView fragment;
    public Button challenge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        fragment = (TextView) findViewById(R.id.ghostText);
        AssetManager assetManager = getAssets();
        try {
            InputStream file = assetManager.open("words.txt");
            dictionary = new FastDictionary(file);
        }
        catch(IOException e) {e.printStackTrace();}
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        challenge = (Button) findViewById(R.id.challenge);
        challenge.setEnabled(true);
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public void challenge(View view) {
        TextView label = (TextView) findViewById(R.id.ghostText);
        String word = fragment.getText().toString();
        if (dictionary.isWord(word) && word.length() >= 4) {
            label.setText("You win!!!");
        }
        else {
            if (dictionary.getAnyWordStartingWith(word) != null) {
                label.setText("The computer has won this round.");
            }
            else
                label.setText("You win!!!");
        }
        challenge = (Button) findViewById(R.id.challenge);
        challenge.setEnabled(false);
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String word = fragment.getText().toString();
        if (dictionary.isWord(word) && word.length() >= 4) {
            label.setText("The computer has won this round.");
            return;
        }
        if (dictionary.getAnyWordStartingWith(word) == null) {
            label.setText("The computer has challenged you. It wins.");
            return;
        }
        else {
            fragment.setText(dictionary.getAnyWordStartingWith(word).substring(0, word.length()+1));
        }
        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        char c = (char) event.getUnicodeChar();
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            c = Character.toLowerCase(c);
            addTextToWord(c);
            userTurn = false;
            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void addTextToWord(char c) {
        fragment.setText(fragment.getText().toString() + c);
    }
}
