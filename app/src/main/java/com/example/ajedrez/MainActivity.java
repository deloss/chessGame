package com.example.ajedrez;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ajedrez.Logica.Controller;
import com.example.ajedrez.Logica.Piece;
import com.example.ajedrez.Logica.Player;
import com.example.ajedrez.Logica.Tablero;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GridLayout tablero;
    private Tablero tab;
    private Piece[][] posiciones;
    private Pair<Integer, Integer> clickeado;
    private Pair<Integer, Integer> movimiento;
    private Controller controller;
    private ArrayList<Pair<Integer, Integer>> movPosibles;
    private Player turnPlayer;
    private boolean checkMate;
    private Button finishButton;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tablero = findViewById(R.id.tablero);
        controller = Controller.getInstance();
        inicializarPartida();



    }

    private void setOnClickListeners() {
        for(int i = 0; i < tablero.getChildCount(); i++) {
            final int indexI = i;
            LinearLayout view = (LinearLayout)tablero.getChildAt(i);
            for(int j = 0; j < view.getChildCount(); j++) {
                final int indexJ = j;
                ImageButton boton = (ImageButton) view.getChildAt(j);
                boton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //v.setBackgroundResource(R.color.colorAccent);
                        // fila
                        int fila = indexI;
                        //columna
                        int columna = indexJ;

                        if(posiciones[fila][columna] != null) {
                            if (clickeado == null) {
                                if(posiciones[fila][columna].getColor() == turnPlayer.getColor()) {
                                    clickeado = new Pair(fila, columna);
                                    movPosibles = mostrarMovimientosPosibles();
                                    if(movPosibles.isEmpty())
                                        clickeado = null;
                                }
                            }else{
                                if (clickeado.first == fila && clickeado.second == columna) {
                                    clickeado = null;
                                    removerMovimientosPosibles(movPosibles, null);
                                }else if(movimientoEsPosible(fila, columna)) {
                                    movimiento = new Pair(fila, columna);
                                    boolean resMov = realizarMovimiento(clickeado.first, clickeado.second, movimiento.first, movimiento.second);
                                    Pair<Integer, Integer> movHecho = null;
                                    if (!resMov) {
                                        Toast.makeText(MainActivity.this, R.string.moveNotPossible, Toast.LENGTH_SHORT).show();
                                        movHecho = new Pair<>(movimiento.first, movimiento.second);
                                    } else {
                                        //Toast.makeText(MainActivity.this, "Movimiento hecho", Toast.LENGTH_SHORT).show();
                                        controller.cambiarTurno();
                                        turnPlayer = controller.getTurnPlayer();
                                    /*if(controller.jugadorEnJaque())
                                        Toast.makeText(MainActivity.this, "Oponente esta en jaque", Toast.LENGTH_SHORT).show();
*/
                                    }
                                    removerMovimientosPosibles(movPosibles, movHecho);
                                    movPosibles = null;
                                    clickeado = null;
                                    movimiento = null;
                                    checkMate = controller.checkMate();
                                    if (checkMate) {
                                        disableTablero();
                                    }
                                }
                            }
                        }else {
                            if(clickeado != null && movimientoEsPosible(fila, columna)) {
                                    movimiento = new Pair(fila, columna);
                                    boolean resMov = realizarMovimiento(clickeado.first, clickeado.second, movimiento.first, movimiento.second);
                                    Pair<Integer, Integer> movHecho = null;
                                    if(!resMov) {
                                        Toast.makeText(MainActivity.this, R.string.moveNotPossible, Toast.LENGTH_SHORT).show();
                                        movHecho = new Pair<>(movimiento.first, movimiento.second);
                                    }else {
                                        //Toast.makeText(MainActivity.this, "Movimiento hecho", Toast.LENGTH_SHORT).show();
                                        controller.cambiarTurno();
                                        turnPlayer = controller.getTurnPlayer();
                                        /*if(controller.jugadorEnJaque())
                                            Toast.makeText(MainActivity.this, "Oponente esta en jaque", Toast.LENGTH_SHORT).show();
*/
                                    }
                                    removerMovimientosPosibles(movPosibles, movHecho);
                                    movPosibles = null;
                                    clickeado = null;
                                    movimiento = null;
                                    checkMate = controller.checkMate();
                                    if(checkMate) {
                                        disableTablero();
                                    }


                            }
                        }
                        setTextTurno();
                    }
                });
            }

        }
    }

    private boolean realizarMovimiento(int posX, int posY, int movX, int movY) {
        boolean ret = controller.moverFicha(posX, posY, movX, movY);
        if(ret)
            actualizarTablero(posiciones[movX][movY], clickeado.first, clickeado.second, movimiento.first, movimiento.second);
        return ret;
    }

    private void inicializarPartida() {
        tab = controller.nuevoTablero();
        finishButton = findViewById(R.id.terminarPartida);
        resetButton = findViewById(R.id.reiniciarPartida);
        turnPlayer = tab.getPlayer1();
        posiciones = tab.getPosiciones();
        movPosibles = null;
        setTextTurno();
        mostrarTablero();
        clickeado = null;
        movimiento = null;
        checkMate = false;
        setOnClickListeners();

    }

    private boolean movimientoEsPosible(int movX, int movY) {
        for(Pair<Integer, Integer> mov : movPosibles)
            if(movX == mov.first && movY == mov.second)
                return true;
        return false;
    }

    private void mostrarTablero() {
        for(int i = 0; i < tablero.getChildCount(); i++) {
            LinearLayout view = (LinearLayout)tablero.getChildAt(i);
            for(int j = 0; j < view.getChildCount(); j++) {
                ImageButton boton = (ImageButton) view.getChildAt(j);
                boton.setAdjustViewBounds(true);
                if(posiciones[i][j] != null) {
                    boton.setImageResource(posiciones[i][j].getResImagen());
                } else
                    boton.setImageResource(android.R.color.transparent);
                boton.setBackgroundColor(getColorPosicion(i, j));


            }

        }
    }

    public ArrayList<Pair<Integer, Integer>> mostrarMovimientosPosibles() {
        ArrayList<Pair<Integer, Integer>> movPosibles = controller.movimientosPosiblesFicha(posiciones[clickeado.first][clickeado.second]);
        for(Pair<Integer, Integer> mov : movPosibles) {
            getImageButton(mov.first, mov.second).setBackgroundColor(Color.RED);
        }
        return movPosibles;
    }

    public void disableTablero() {
        for(int i = 0; i < tablero.getChildCount(); i++) {
            LinearLayout view = (LinearLayout)tablero.getChildAt(i);
            for(int j = 0; j < view.getChildCount(); j++) {
                ImageButton boton = (ImageButton) view.getChildAt(j);
                boton.setClickable(false);
            }

        }
    }

    public void removerMovimientosPosibles(ArrayList<Pair<Integer, Integer>> movPosibles, Pair<Integer, Integer> movHecho) {
        for(Pair<Integer, Integer> mov : movPosibles) {
            if(mov != movHecho) {
                getImageButton(mov.first, mov.second).setBackgroundColor(getColorPosicion(mov.first, mov.second));
            }
        }
    }

    public int getColorPosicion(int x, int y) {
        if(x % 2 == 0 && y % 2 == 0 || x % 2 == 1 && y % 2 == 1)
            return Color.GRAY;
        else
            return Color.WHITE;
    }

    public void setTextTurno() {
        TextView turno = findViewById(R.id.turnoJugador);
        if(checkMate && turnPlayer == tab.getPlayer1())
            turno.setText(R.string.blacksWin);
        else if(checkMate && turnPlayer == tab.getPlayer2())
            turno.setText(R.string.whitesWin);
        else if(turnPlayer == tab.getPlayer1())
            turno.setText(R.string.whitesTurn);
        else
            turno.setText(R.string.blacksTurn);
    }

    public void actualizarTablero(Piece piece, int posX, int posY, int movX, int movY) {
        getImageButton(posX, posY).setImageResource(android.R.color.transparent);
        getImageButton(movX, movY).setImageResource(piece.getResImagen());

    }

    public ImageButton getImageButton(int x, int y) {
        return ((ImageButton)(((LinearLayout)(tablero.getChildAt(x))).getChildAt(y)));
    }

    public void terminarPartida(View v) {
        onBackPressed();
    }

    public void reiniciarPartida(View v) {
        inicializarPartida();
    }
}
