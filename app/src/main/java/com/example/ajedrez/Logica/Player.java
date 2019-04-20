package com.example.ajedrez.Logica;

import android.util.Pair;

import java.util.ArrayList;

public class Player {
    private ArrayList<Piece> pieces;
    private int color;
    private King king;
    private Tablero tablero;
    public Player(ArrayList<Piece> pieces, int color, King king, Tablero tablero) {
        this.pieces = pieces;
        for(Piece piece : pieces)
            piece.setPlayer(this);
        this.color = color;
        this.king = king;
        this.tablero = tablero;
    }

    public boolean movimientoEsJaque(Piece piece, int movX, int movY) {
        return piece.movimientoEsJaque(movX, movY);
        /*Piece[][] posiciones = tablero.getPosiciones();
        Piece fichaAux = posiciones[movX][movY];
        int posXInicial = piece.getX();
        int posYInicial = piece.getY();
        piece.moverPieza(movX, movY);
        boolean result;
        if(jaquePropio())
            result = true;
        else
            result = false;
        piece.moverPieza(posXInicial, posYInicial);
        posiciones[movX][movY] = fichaAux;
        return result;*/
    }

    public boolean checkMate() {
        ArrayList<Pair<Piece, ArrayList<Pair<Integer, Integer>>>> movPosibles = movimientosPosibles();
        for(Pair<Piece, ArrayList<Pair<Integer, Integer>>> movPosiblesFicha : movPosibles){
            for(Pair<Integer, Integer> mov : movPosiblesFicha.second) {
                if(!movimientoEsJaque(movPosiblesFicha.first, mov.first, mov.second))
                    return false;
            }
        }
        return true;
    }

    public boolean jaquePropio() {
        Player oponente;
        if(this == tablero.getPlayer1())
            oponente = tablero.getPlayer2();
        else
            oponente = tablero.getPlayer1();
        ArrayList<Pair<Piece, ArrayList<Pair<Integer, Integer>>>> movimientosPosibles = oponente.movimientosPosibles();
        for(Pair<Piece, ArrayList<Pair<Integer, Integer>>> movPosiblesFicha : movimientosPosibles){
            for(Pair<Integer, Integer> movimiento : movPosiblesFicha.second)
                if(movimiento.first == king.getX() && movimiento.second == king.getY())
                    return true;
        }
        return false;
    }

    public King getKing() {
        return king;
    }

    public boolean moverPieza(Piece piece, int movimientoX, int movimientoY) {
        if(!pieces.contains(piece))
            return false;
        return piece.moverPieza(movimientoX, movimientoY);
    }

    public void piezaComida(Piece piece) {
        pieces.remove(piece);
    }

    public void addPieza(Piece piece) {pieces.add(piece);}

    public int getColor() {
        return color;
    }

    public ArrayList<Pair<Piece, ArrayList<Pair<Integer, Integer>>>> movimientosPosibles() {
        ArrayList<Pair<Piece, ArrayList<Pair<Integer, Integer>>>> movimientos = new ArrayList<>();
        for(Piece piece : pieces) {
            movimientos.add(new Pair<>(piece, piece.movimientosPosibles()));
        }
        return movimientos;
    }
}
