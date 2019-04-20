package com.example.ajedrez.Logica;

import android.util.Pair;

import com.example.ajedrez.R;

import java.util.ArrayList;

public class Rook extends Piece {
    private boolean seMovio;
    public Rook(int x, int y, Tablero tablero, int color){
        super(x, y, tablero, color);
        seMovio = false;
        if(color == 0)
            resImagen = R.drawable.whiterook;
        else
            resImagen = R.drawable.blackrook;
    }

    @Override
    public ArrayList<Pair<Integer, Integer>> movimientosPosibles() {
        Piece[][] posiciones = tablero.getPosiciones();
        ArrayList<Pair<Integer, Integer>> movimientosPosibles = new ArrayList<>();
        //reviso la columna
        for (int i = x + 1; i < posiciones.length; i++) {
            // es una ficha contraria, no puede atravezarla
            if (posiciones[i][y] != null && posiciones[i][y].color != color) {
                Pair<Integer, Integer> pair = new Pair<>(i, y);
                movimientosPosibles.add(pair);
                break;
            } else if (posiciones[i][y] == null) {
                Pair<Integer, Integer> pair = new Pair<>(i, y);
                movimientosPosibles.add(pair);
            }
            // si es el mismo color no puede avanzar mas
            else if (posiciones[i][y].color == color) {
                break;
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            // es una ficha contraria, no puede atravezarla
            if (posiciones[i][y] != null && posiciones[i][y].color != color) {
                Pair<Integer, Integer> pair = new Pair<>(i, y);
                movimientosPosibles.add(pair);
                break;
            } else if (posiciones[i][y] == null) {
                Pair<Integer, Integer> pair = new Pair<>(i, y);
                movimientosPosibles.add(pair);
            }
            // si es el mismo color no puede avanzar mas
            else if (posiciones[i][y].color == color) {
                break;
            }
        }

        //reviso por fila
        for (int j = y + 1; j < posiciones[0].length; j++) {
            // es una ficha contraria, no puede atravezarla
            if (posiciones[x][j] != null && posiciones[x][j].color != color) {
                Pair<Integer, Integer> pair = new Pair<>(x, j);
                movimientosPosibles.add(pair);
                break;
            } else if (posiciones[x][j] == null) {
                Pair<Integer, Integer> pair = new Pair<>(x, j);
                movimientosPosibles.add(pair);
            }
            // si es el mismo color no puede avanzar mas
            else if (posiciones[x][j].color == color) {
                break;
            }
        }
        for (int j = y - 1; j >= 0; j--) {
            // es una ficha contraria, no puede atravezarla
            if (posiciones[x][j] != null && posiciones[x][j].color != color) {
                Pair<Integer, Integer> pair = new Pair<>(x, j);
                movimientosPosibles.add(pair);
                break;
            } else if (posiciones[x][j] == null) {
                Pair<Integer, Integer> pair = new Pair<>(x, j);
                movimientosPosibles.add(pair);
            }
            // si es el mismo color no puede avanzar mas
            else if (posiciones[x][j].color == color) {
                break;
            }
        }

        return movimientosPosibles;
    }

    public boolean seMovio(){
        return seMovio;
    }

    @Override
    public boolean moverPieza(int movimientoX, int movimientoY) {
        boolean movioPieza = super.moverPieza(movimientoX, movimientoY);
        if(movioPieza)
            seMovio = true;
        return movioPieza;
    }
}
