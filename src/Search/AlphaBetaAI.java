package Search;

import java.util.ArrayList;
import java.util.List;
import game.Game;
import main.collections.FastArrayList;
import other.RankUtils;
import other.context.Context;
import other.move.Move;
import other.trial.Trial;
import other.state.State;
import other.AI;
import Evaluation.Evaluate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.mail.Flags;

//import java.util.*;

import game.Game;
import game.util.graph.Face;
//import gnu.trove.list.array.TIntArrayList;
import main.collections.FastArrayList;
import other.AI;
import other.context.Context;
import other.move.Move;
import other.state.container.ContainerState;
import other.topology.Cell;
import other.topology.*;
//import other.topology.Cell;
import utils.AIUtils;
import utils.data_structures.ScoredMove;
import game.types.board.SiteType;
import game.util.graph.Radial;
import gnu.trove.list.array.TIntArrayList;
import main.collections.FastArrayList;

import game.util.directions.AbsoluteDirection;
import java.util.concurrent.ThreadLocalRandom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.lang.model.type.NullType;

public class AlphaBetaAI extends AI
{
    /** Our player index */
    protected int player = -1;
    /**Initial values of AB search**/
    public static final float ALPHA_INIT = -1000000.f;
    public static final float BETA_INIT = 1000000.f; public int[] LBest = new int[10];
    protected FastArrayList<Move> current_root_moves = null;
    protected FastArrayList<Move> root_move_sort = null;


    public AlphaBetaAI()
    {
        this.friendlyName = "Maria Alpha-Beta AI";
        System.out.println("Its Here");
    }

    @Override
    public Move selectAction
            (
                    final Game game,
                    final Context context,
                    final double maxSeconds,
                    final int maxIterations,
                    final int maxDepth
            ) {
        // Creating my Tree starting with Root Node
//        final Node root = new Node(null, null, context);
        System.out.println("Planting tree");
        double bestScore = -1000;
        Move bestMove = null;
        long start = System.currentTimeMillis();
        long total_time=0;
        final int maxsec = 16;
        final int maxdepth = 1;


        Move iterativeDeep_move = iterativeDeepening(game,context,maxsec,maxdepth, 1 ,true);
        bestMove=iterativeDeep_move;
//        float alphabeta_score = alphaBeta(game,context,maxdepth, ALPHA_INIT ,BETA_INIT,true,maxsec);

        System.out.println("time:"+((System.currentTimeMillis()-start)/1000));
        total_time= total_time +((System.currentTimeMillis()-start)/1000) ;
        System.out.println(bestMove);
        return bestMove;
    }

    public float alphaBeta(Game game,final Context context,final int depth, final float inAlpha, final float inBeta, boolean isMaximisingPlayer, long stopTime)
    {
        float alpha = inAlpha;
        float beta = inBeta;
        Trial trial = context.trial(); /** store history game as it progress*/
        State state = context.state(); /** retrive current trial form game context*/
        Evaluate evaluator = new Evaluate();
        final int mover = state.playerToAgent(state.mover());/** determine which player turn is*/
        FastArrayList<Move> legalMoves = game.moves(context).moves(); /** getting the legal moves*/
//        final int numLegalMoves = legalMoves.size();


        /** Check for terminal state or depth limit*/
        if (context.trial().over() || depth == 0) {
            System.out.println("Reached!The evaluation took place.");
//            return 1;
            return evaluator.evaluate(game, context);// Return heuristic or game outcome score
        }
        Move bestMove = legalMoves.get(0);
        if(isMaximisingPlayer)
        {   float bestScore = ALPHA_INIT;
            for(Move move : legalMoves)
            {
                final Context copyContext = copyContext(context);
                game.apply(copyContext,move);
                final float score = alphaBeta(game,copyContext, depth - 1, alpha, beta, true, stopTime);
                if(bestScore > score)
                {
                    bestMove = move;
                    bestScore = score;
                }
                alpha = Math.max(alpha, score);
                if (alpha >= beta)	// beta cut-off
                    break;
            }
            return bestScore;
        }
        else
        {
            float bestScore = BETA_INIT;
            for(Move move : legalMoves)
            {
                final Context copyContext = copyContext(context);
                game.apply(copyContext,move);
                final float score = alphaBeta(game,copyContext, depth - 1, alpha, beta, false, stopTime);
                if(bestScore < score)
                {
                    bestMove = move;            /**storing the besat move for TT table*/
                    bestScore = score;          /**storing the best score*/
                }
                alpha = Math.min(beta, score);
                if (alpha >= beta)	// alpha cut-off
                    break;
            }
            return bestScore;
        }

    }

    public Move iterativeDeepening
            (
                    final Game game,
                    final Context context,
                    final double maxSeconds,
                    final int maxDepth,
                    final int startDepth,
                    boolean isMaximizingPlayer
            )
    {
        final long startTime = System.currentTimeMillis();
        long stopTime = (maxSeconds > 0.0) ? startTime + (long) (maxSeconds * 1000) : Long.MAX_VALUE;

        current_root_moves = new FastArrayList<Move>(game.moves(context).moves());

        final FastArrayList<Move> tempMovesList = new FastArrayList<Move>(current_root_moves);
        root_move_sort = new FastArrayList<Move>(current_root_moves.size());
        while (!tempMovesList.isEmpty())
        {
            root_move_sort.add(tempMovesList.removeSwap(ThreadLocalRandom.current().nextInt(tempMovesList.size())));
        }

        final int numRootMoves = root_move_sort.size();
        ArrayList<Float> moveScores = new ArrayList<>();
        ArrayList<ScoredMove> scored_moves = new ArrayList<>();

        int searchDepth = 0;
        boolean fullTreeSearch ;
        Move best_move_full_search = root_move_sort.get(0);
        double score_full_search = 0;

        while(searchDepth<maxDepth)
        {
            searchDepth+=1;

            System.out.println("Searching at depth : " + searchDepth+ "................................");
//				fullTreeSearch =true;
            float score = ALPHA_INIT;
            float alpha= ALPHA_INIT;
            final float beta =BETA_INIT;


            Move best_move = root_move_sort.get(0);
            Move move;
            for(int i = 0; i < numRootMoves;i++)
            {
                final Context copyContext = copyContext(context);
                move = root_move_sort.get(i);
                game.apply(copyContext, move);
                float value = alphaBeta(game, copyContext, searchDepth - 1, alpha, beta, isMaximizingPlayer, stopTime);
                moveScores.add(value);
                if (System.currentTimeMillis() >= stopTime) {
                    best_move = null;
                    break;
                }
                if(value>score)
                {
                    score = value;
                    best_move = move;

                }
                alpha = Math.max(alpha, score);

                if (alpha >= beta) {
                    break;
                }


            }
            if(best_move !=  null)
            {
                System.out.println("Finished Searched at Depth : " + searchDepth);
                System.out.println("Score : " + score);
                System.out.println("Best move: " + best_move);
                if(score == this.BETA_INIT)
                {
                    System.out.println("It exited at score == beta");
                    return best_move;
                }
                else if (score == this.ALPHA_INIT)
                {
                    System.out.println("It exited at score == alpha" +score + "this is alpha: " + alpha);
                    return best_move_full_search;
                }
                else
                {
                    System.out.println("Best move reached:" + best_move);
                    best_move_full_search = best_move;
                    score_full_search = score;
                }

            }

            if (System.currentTimeMillis() >= stopTime)
            {
                System.out.println("best_move_full_search");
                return best_move_full_search;
            }

            scored_moves.clear();
            for(int i = 0; i < numRootMoves;i++)
            {
                scored_moves.add(new ScoredMove(root_move_sort.get(i), moveScores.get(i)));
            }
            Collections.sort(scored_moves);

            root_move_sort.clear();
            for (int i = 0; i < numRootMoves; ++i)
            {
                root_move_sort.add(scored_moves.get(i).move);
            }
            scored_moves.clear();
        }
        System.out.println("It extited the while"+best_move_full_search);
        return best_move_full_search;

    }

    private class ScoredMove implements Comparable<ScoredMove> {
        public final Move move;
        public final double score;

        public ScoredMove(Move move, double score) {
            this.move = move;
            this.score = score;
        }

        public int compareTo(ScoredMove move_2) {
            double differention = move_2.score - this.score;
            if (differention < 0.0F) {
                return -1;
            } else {
                return differention > 0.0F ? 1 : 0;
            }
        }
    }

}

