package com.example.ajedrez.Logica;

import android.util.Pair;

import java.util.ArrayList;

public class Controller {
    private static final Controller ourInstance = new Controller();
    private Tablero tablero;
    private Player turnPlayer;
    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {
    }

    public Tablero nuevoTablero() {
        tablero = new Tablero();
        turnPlayer = tablero.getPlayer1();
        return tablero;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public boolean moverFicha(int posX, int posY, int movX, int movY) {
        Piece[][] posiciones = tablero.getPosiciones();
        if(posiciones[posX][posY] == null || posiciones[posX][posY].color != turnPlayer.getColor() || posiciones[movX][movY] == null || posiciones[movX][movY].color == turnPlayer.getColor()) {
            return turnPlayer.moverPieza(posiciones[posX][posY], movX, movY);
        }else {
            Piece piezaMov = posiciones[movX][movY];
            boolean moverPieza = turnPlayer.moverPieza(posiciones[posX][posY], movX, movY);
            if(moverPieza && piezaMov != null && piezaMov.player != turnPlayer)
                oponente(turnPlayer).piezaComida(piezaMov);
            return moverPieza;
        }

    }



    public boolean checkMate() {
        return turnPlayer.checkMate();
    }


    public boolean jugadorEnJaque() {
        return turnPlayer.jaquePropio();
    }

    public boolean oponenteEnJaque() {
        return oponente(turnPlayer).jaquePropio();
    }

    public boolean terminoJuego() {
        return tablero.terminoJuego();
    }

    public ArrayList<Pair<Integer, Integer>> movimientosPosiblesFicha(Piece piece) {
        ArrayList<Pair<Integer, Integer>> movPosibles = piece.movimientosPosibles();
        ArrayList<Pair<Integer, Integer>> movSinJaque = new ArrayList<>();
        for(Pair<Integer, Integer> mov : movPosibles) {
            if(!piece.movimientoEsJaque(mov.first, mov.second))
                movSinJaque.add(mov);
        }
        return movSinJaque;
    }


    public void cambiarTurno() {
        if(turnPlayer == tablero.getPlayer1())
            turnPlayer = tablero.getPlayer2();
        else
            turnPlayer = tablero.getPlayer1();
    }

    public Player oponente(Player player) {
        if(player == tablero.getPlayer1())
            return tablero.getPlayer2();
        else
            return tablero.getPlayer1();
    }

    public Player getTurnPlayer() {
        return turnPlayer;
    }
}
