package Evaluation;

import game.Game;

import game.equipment.other.Regions;

import other.context.Context;

import gnu.trove.list.array.TIntArrayList;

public class Evaluate {
    public float weight_h1 = 0.5f;
    public float weight_h2 = 0.25f;
    public float weight_h3 = 0.125f;

    public float evaluate(Game game, Context context) {
        int current_mover = context.state().mover();
        System.out.println("calculating best move for player"+current_mover);
        float evaluation_equation =
                (getnumberofpieces(game, context, current_mover) * weight_h1) +
                (getleadingpieces(game, context, current_mover) * weight_h1) +
                (getedgepieces(game, context, current_mover) * weight_h3) +
                (getnumberofdefenders(game, context, current_mover) * weight_h2);
//        System.out.println("F1*h2: "+(getnumberofpieces(game, context, current_mover) * weight_h1));
//        System.out.println("F2*h1: "+(getleadingpieces(game, context, current_mover) * weight_h1));
//        System.out.println("F3*h3: "+(getedgepieces(game, context, current_mover) * weight_h3));
//        System.out.println("F4*h3: "+(getnumberofdefenders(game, context, current_mover) * weight_h2));
        return evaluation_equation;
    }

    public float getnumberofpieces(Game game, Context context, int playerindex) {
        float score =  0.01f;
        float rational = 0.01f;
        try{
            int currentplayernumberofpieces = context.state().owned().sites(playerindex).size();
            int opponentplayernumberofpieces = context.state().owned().sites(3-playerindex).size();
            rational = (float) (currentplayernumberofpieces - opponentplayernumberofpieces);
            score = rational * currentplayernumberofpieces;
        } catch (Throwable ignored){

        }
        return score;
    }

    public float getleadingpieces(Game game, Context context, int playerindex){
        float score = 0.0f;
        int targetrow = -1;
        try{
            TIntArrayList leadingcurrentplayer = context.state().owned().sites(playerindex);
            TIntArrayList leadingoppnentplayer = context.state().owned().sites(3 - playerindex);
            Regions[] re = game.equipment().regions();
            if (re[playerindex-1].name().endsWith("P1")) {
                targetrow = 8;
                // foreach site we need to calculate the lead
                while(!leadingcurrentplayer.isEmpty()){
                    int rowrangeneg = leadingcurrentplayer.max() / 9;
                    score += rowrangeneg;
                    leadingcurrentplayer.remove(leadingcurrentplayer.max());
                }

            }else {
                targetrow = 0;
                // foreach site we need to calculate the lead
                while(!leadingcurrentplayer.isEmpty()){
                    int rowrangeneg = leadingcurrentplayer.min() / 9;
                    score += (8-rowrangeneg);
                    leadingcurrentplayer.remove(leadingcurrentplayer.min());
                }
            }
        } catch (Throwable ignored){
            targetrow = -1;
        }
        return score;
    }

    public float getedgepieces(Game game, Context context, int playerindex){
        float score = 0.1f;
        return score;
    }

    public float getnumberofdefenders(Game game, Context context, int playerindex) {
        float score = 0.0f;
        try{
            TIntArrayList leadingcurrentplayer = context.state().owned().sites(playerindex);
            TIntArrayList leadingoppnentplayer = context.state().owned().sites(3 - playerindex);
            Regions[] re = game.equipment().regions();
            if (re[playerindex-1].name().endsWith("P1")) {
                // foreach site we need to calculate the lead
                while(!leadingcurrentplayer.isEmpty()){
                    int rowrangeneg = leadingcurrentplayer.max() / 9;
                    if(rowrangeneg == 0) {
                        score = score + leadingcurrentplayer.size();
                    }
                    leadingcurrentplayer.remove(leadingcurrentplayer.max());
                }

            }else {
                // foreach site we need to calculate the lead
                while(!leadingcurrentplayer.isEmpty()){
                    int rowrangeneg = leadingcurrentplayer.min() / 9;
                    if(rowrangeneg == 0){
                        score = score + leadingcurrentplayer.size();
                    }
                    leadingcurrentplayer.remove(leadingcurrentplayer.min());
                }
            }
        } catch (Throwable ignored){
        }
        return score;
    }

    public float getwinner(Game game, Context context, int playerindex){
        return 0.0f;
    }
    public float getloser(Game game, Context context, int playerindex){
        return 0.0f;
    }
}
