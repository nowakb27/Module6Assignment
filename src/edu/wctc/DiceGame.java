package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;

public class DiceGame {
    private List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private int maxRolls = 0;
    private Player currentPlayer = new Player();


    public DiceGame(int countPlayers, int countDice, int maxRoll) throws IllegalArgumentException{
        maxRolls = maxRoll;
        players.add(currentPlayer);
        if (countPlayers < 2)
            throw new IllegalArgumentException();
        else {
            for (int i = 1; i < countPlayers; i++) {
                Player player = new Player();
                players.add(player);
            }
        }

        for (int i = 1; i <= countDice; i++) {
            Die die = new Die(6);
            dice.add(die);
        }
    }

    private boolean allDiceHeld() {
        return dice.stream().allMatch(Die::isBeingHeld);
    }

    public boolean autoHold(int faceValue) {
        boolean held = false;
        List<Die> matches = dice.stream().filter(d -> d.getFaceValue() == faceValue)
                .collect(Collectors.toList());
        for (Die die : matches) {
            if (die.isBeingHeld()) {
                held = true;
                break;
            }
            else {
                if (!isHoldingDie(faceValue)) {
                    die.holdDie();
                    held = true;
                    break;
                }
            }
        }
        return held;
    }

    public boolean currentPlayerCanRoll() {
        return (currentPlayer.getRollsUsed() < maxRolls && !allDiceHeld());
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
        return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return dice.stream().map(Die::toString).collect(Collectors.joining(","));
    }

    public String getFinalWinner() {
        List<Player> winRanking = players.stream().sorted(Comparator.comparing(Player::getWins).reversed())
                .collect(Collectors.toList());
        return winRanking.get(0).toString();
    }

    public String getGameResults() {
        players = players.stream().sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
        players.forEach(p -> {
            if (p == players.get(0))
                p.addWin();
            else
                p.addLoss();
        });

        return players.stream().map(Player::toString).collect(Collectors.joining(","));

    }

    private boolean isHoldingDie(int faceValue) {
        return dice.stream().anyMatch(d -> d.isBeingHeld() && d.getFaceValue() == faceValue);
    }

    public boolean nextPlayer() {
        boolean more = true;
        int index = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) == currentPlayer)
                index = i+1;
        }
        if (index < players.size())
            currentPlayer = players.get(index);
        else
            more = false;

        return more;
    }

    public void playerHold(char dieNum) {
        Optional<Die> hold = dice.stream().filter(d -> d.getDieNum() == dieNum).findFirst();
        hold.ifPresent(Die::holdDie);
    }

    public void resetDice() {
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers() {
        players.forEach(Player::resetPlayer);
    }

    public void rollDice() {
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer() {
        int ship = 0, captain = 0, crew = 0, score = 0;
        List<Integer> values = dice.stream().map(Die::getFaceValue).collect(Collectors.toList());
        if (isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4)) {
            for (int i = 0; i < 5; i++) {
                if (values.get(i) == 6)
                    ship = i;
                else if (values.get(i) == 5)
                    captain = i;
                else if (values.get(i) == 4)
                    crew = i;
            }
            for (int i = 0; i < 5; i++) {
                if (i == ship | i == captain | i == crew);
                else
                    score += values.get(i);
            }
        }

        currentPlayer.setScore(score);
    }

    public void startNewGame() {
        currentPlayer = players.get(0);
        resetPlayers();
    }
}