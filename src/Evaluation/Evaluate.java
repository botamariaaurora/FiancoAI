package Evaluation;

import game.Game;

import game.equipment.other.Regions;

import other.context.Context;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

public class Evaluate {
    public float weight_h1 = 0.5f;
    public float weight_h2 = 0.25f;
    public float weight_h3 = 0.125f;

    public float evaluate(Game game, Context context) {
        int current_mover = context.state().mover();
       // if(// check terminal state mover otherwise 3-player)
        System.out.println("calculating best move for player" + current_mover);
        float[]  count_pieces = getnumberofpieces(game, context);
        float [] leading_pieces = getleadingpieces(game, context);
        float [] edge_pieces = getedgepieces(game, context);
        float[] defenders= getnumberofdefenders(game, context);
        float[] winner_loser = getwinner(game, context);
        /** Evaluation player 1*/
        float evaluation_equation_player1 =
                (count_pieces[0] * weight_h1) +
                (leading_pieces[0] * weight_h1) +
                (edge_pieces[0] * weight_h3) +
                (defenders[0] * weight_h2) +
                (winner_loser[0] * weight_h1);
        /** Evaluation player 2*/
        float evaluation_equation_player2 =
                        (count_pieces[1] * weight_h1) +
                        (leading_pieces[1] * weight_h1) +
                        (edge_pieces[1] * weight_h3) +
                        (defenders[1] * weight_h2) + (winner_loser[1] * weight_h1);

        if(current_mover == 1) {
            return evaluation_equation_player1;
        }else
        {
            return evaluation_equation_player2;
        }
    }

    public float[] getnumberofpieces(Game game, Context context) {
        float score_player1 = 0.01f;
        float score_player2 = 0.01f;
        float rational = 0.0f;
        float[] scores = null;
        try {
            int currentplayernumberofpieces_player1 = context.state().owned().sites(1).size();
            int opponentplayernumberofpieces_player2 = context.state().owned().sites(2).size();
            rational = (float) (currentplayernumberofpieces_player1 - opponentplayernumberofpieces_player2);
            score_player1 = rational * currentplayernumberofpieces_player1;

            int currentplayernumberofpieces_player2 = context.state().owned().sites(2).size();
            int opponentplayernumberofpieces_player1 = context.state().owned().sites(1).size();
            rational = (float) (currentplayernumberofpieces_player2 - opponentplayernumberofpieces_player1);
            score_player2 = rational * currentplayernumberofpieces_player2;

            System.out.println("Player 1 score: " + score_player1 + "Player 2 score : " + score_player2);

            scores = new float[] {score_player1, score_player2};

        } catch (Throwable ignored) {

        }
        return scores;
    }

    public float[] getleadingpieces(Game game, Context context) {
        float score_player1= 0.0f;
        float score_player2= 0.0f;
        float[] scores = null;
        try {
            TIntArrayList leadingcurrentplayer1 = context.state().owned().sites(1);
            TIntArrayList leadingoppnentplayer2 = context.state().owned().sites(2);
            Regions[] re = game.equipment().regions();

            // foreach site we need to calculate the lead
            while (!leadingcurrentplayer1.isEmpty()) {
                    int rowrangeneg = leadingcurrentplayer1.max() / 9;
                    score_player1 += rowrangeneg * rowrangeneg * 10.0f;
                    leadingcurrentplayer1.remove(leadingcurrentplayer1.max());
                }

            System.out.println(" Player 1 leading score: "+score_player1);
            // foreach site we need to calculate the lead
            while (!leadingoppnentplayer2.isEmpty()) {
                    int rowrangeneg = leadingoppnentplayer2.min() / 9;
                    score_player2 += (8 - rowrangeneg) * 10.0f;
                    leadingoppnentplayer2.remove(leadingoppnentplayer2.min());
                }
            System.out.println(" Player 2 leading score: "+score_player2);

            scores = new float[] {score_player1, score_player2};


        } catch (Throwable ignored) {
        }
        return scores;
    }


    public float[] getedgepieces(Game game, Context context) {
        int edgecount = 0;
        float score_player1 = 0.0f;
        float score_player2 = 0.0f;
        float[] scores = null;
        try {
            TIntArrayList leadingplayer_1 = context.state().owned().sites(1);
            for (int i = 0; i < leadingplayer_1.size(); i++) {
                if (leadingplayer_1.get(i) % 9 == 0 || leadingplayer_1.get(i) % 9 == 8) {
                    edgecount = edgecount +1;
                    if(edgecount<7){
                        score_player1 = score_player1 + 10.0f;
                    }else {
                        score_player1 = score_player1 + 5.0f;
                    }
                }
            }
            System.out.println(" Player 1 edge score: "+score_player1);
            /** Calulate for player 2 */
            TIntArrayList leadingplayer2 = context.state().owned().sites(2);
            for (int i = 0; i < leadingplayer2.size(); i++) {
                if (leadingplayer2.get(i) % 9 == 0 || leadingplayer2.get(i) % 9 == 8) {
                    edgecount = edgecount +1;
                    if(edgecount<7){
                        score_player2 = score_player2 + 10.0f;
                    }else {
                        score_player2 = score_player2 + 5.0f;
                    }
                }
            }
            System.out.println(" Player 2 edge score: "+score_player2);
            scores = new float[] {score_player1, score_player2};



        } catch (Throwable ignored) {
            score_player1 = 0.0f;
            score_player2 = 0.0f;
        }
        return scores;
    }

    public float[] getnumberofdefenders(Game game, Context context) {
        float score_player1 = 0.0f;
        float score_player2 = 0.0f;
        float[] scores = null;
        int defenders = 0;
        try {
            TIntArrayList leadingplayer_1 = context.state().owned().sites(1);
            TIntArrayList leadingplayer_2 = context.state().owned().sites(2);
            /**Current player 1 */
            while (!leadingplayer_1.isEmpty()) {
                int player1row = leadingplayer_1.max() / 9;
                if (player1row == 0) {
                    score_player1 = score_player1 + 50.0f;
                    score_player2 = score_player2 - 50.0f;
                }
                leadingplayer_1.remove(leadingplayer_1.max());
            }
            while (!leadingplayer_2.isEmpty()) {
                int player2row = leadingplayer_2.max() / 9;
                if (player2row == 8) {
                    score_player1 = score_player1 - 50.0f;
                    score_player2 = score_player2 + 50.0f;
                }
                leadingplayer_2.remove(leadingplayer_2.max());
            }
            System.out.println(" Player 1 defenders: " + score_player1);
            System.out.println(" Player 2 defenders: " + score_player2);
            scores = new float[] {score_player1, score_player2};
        } catch (Throwable ignored) {
            System.out.println("failed");
        }
        return scores;
    }

    public float[] getwinner(Game game, Context context) {
        float score_player1 = 0.0f;
        float score_player2 = 0.0f;
        float[] scores = null;
        try {
            TIntArrayList winnerplayer = context.winners();
            if (!winnerplayer.isEmpty()) {
                if(winnerplayer.get(0) == 1){
                    score_player1 = score_player1 - 10000.0f;
                    score_player2= score_player2 + 10000.0f;
                }
                else
                {
                    score_player2 = score_player2 - 10000.0f;
                    score_player1= score_player1 + 10000.0f;
                }
            }
            System.out.println(" Player 1 win: " + score_player1);
            System.out.println(" Player 2 win: " + score_player2);
        } catch (Throwable ignored) {
            System.out.println("failed");
        }
        scores = new float[] {score_player1, score_player2};
        return scores;
    }

}
