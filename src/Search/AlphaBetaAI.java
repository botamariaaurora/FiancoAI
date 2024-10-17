package Search;

import java.util.*;

import game.Game;
import main.collections.FastArrayList;
import other.context.Context;
import other.move.Move;
import other.trial.Trial;
import other.state.State;
import other.AI;
import Evaluation.Evaluate;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import TranspositionTable.*;

public class AlphaBetaAI extends AI
{
    /** Our player index */
    /**Initial values of AB search**/
    public static final float ALPHA_INIT = -1000000.f;
    public static final float BETA_INIT = 1000000.f;
    public static final float inAlpha = ALPHA_INIT;
    public static final float inBeta= BETA_INIT;
    protected FastArrayList<Move> current_root_moves = null;
    protected FastArrayList<Move> root_move_sort = null;
    private TranspositionTable tt;
//    private HashMap<Long, Move[]> killerMoveMap;
    public int C = 3;  // Number of cutoffs needed to prune
    public int M = 10; // Maximum number of shallow searches
    public int R = 2;






    public AlphaBetaAI()
    {
        this.friendlyName = "Maria Alpha-Beta AI";
        tt = new TranspositionTable(16);  // Initialize TT with 2^16 entries
        tt.allocate();


    }

    @Override
    public Move selectAction
            (
                    final Game game,
                    final Context context,
                    final double maxSeconds,
                    final int maxIterations,
                    int maxDepth
            ) {
        double bestScore = -1000;
        Move bestMove = null;
        Move worseMove = null;
        long start = System.currentTimeMillis();
        long total_time=0;
        final int maxsec = 16;
        final int maxdepth = 4;
        System.out.flush();
        System.out.println("Starting Iterative Deep Max Depth: "+maxdepth+" Max Time: "+maxsec+"s ");

        Move iterativeDeep_move = iterativeDeepening(game,context,maxsec,maxdepth, 1 ,true);
        bestMove=iterativeDeep_move;


        return bestMove;
    }

    public float alphaBeta(Game game,final Context context,final int depth, final float inAlpha, final float inBeta)
    {
        if(depth<0){
            System.out.flush();
            System.out.println("Error: Invalid Depth Number assigned");
            System.out.println("Please check the configured max depth");
        }
        float alpha = inAlpha;
        float beta = inBeta;
        Trial trial = context.trial(); /** store history game as it progress*/
        State state = context.state(); /** retrive current trial form game context*/
        Evaluate evaluator = new Evaluate();
        final int mover = state.playerToAgent(state.mover());/** determine which player turn is*/
        FastArrayList<Move> legalMoves = game.moves(context).moves(); /** getting the legal moves*/
        final int numLegalMoves = legalMoves.size();

        /** Check for terminal state or depth limit*/
        if (context.trial().over() || depth == 0) {
            System.out.println("Game has ended while player: "+mover+" Had: "+numLegalMoves+" Moves");
            return evaluator.evaluate(game,context);
        }
        if (depth-1-R >=0) {
            Float beta_multi = multiCut(game, context, depth, alpha, beta, C, M, R);
            if(beta_multi != null){
                return beta; // If multi-cut succeeds, we return beta for pruning
            }
        }

        float oldAlpha = alpha;
        long fullHash = context.state().fullHash();

        // Transposition Table check
        TableData ttEntry = tt.retrieve(fullHash);
        if (ttEntry != null && ttEntry.depth >= depth) {
            // Use the TT entry if the stored depth is sufficient
            if (ttEntry.boundType == TTBounds.EXACT_VALUE) {
                return ttEntry.value;  // Exact value found, return it
            } else if (ttEntry.boundType == TTBounds.LOWER_BOUND) {
                alpha = Math.max(alpha, ttEntry.value);  // Update alpha
            } else if (ttEntry.boundType == TTBounds.UPPER_BOUND) {
                beta = Math.min(beta, ttEntry.value);  // Update beta
            }
            if (alpha >= beta) {
                return ttEntry.value;  // Cut-off if alpha >= beta
            }
        }

        Move bestMove = legalMoves.get(0);
        Move worseMove = legalMoves.get(0);
//        final int numLegalMoves = legalMoves.size();



        float bestScore = ALPHA_INIT;
//        float score = ALPHA_INIT;
            for(Move move : legalMoves)
            {
                final Context copyContext = copyContext(context);
                game.apply(copyContext,move);
                final float score = -alphaBeta(game,copyContext,depth - 1,-beta,-alpha);
                if(bestScore < score)
                {
                    bestMove = move;
                    bestScore = score;
                }
                if(score>alpha){alpha=score;}

                if (score >= beta) {
                    System.out.println("alpha:"+alpha+" beta:"+ beta);
                    System.out.println("cut-off");
                    break;
                }
            }

            byte boundType;
            if (bestScore <= oldAlpha) {
                boundType = TTBounds.UPPER_BOUND;  // Fail-low result (alpha cut-off)
            } else if (bestScore >= beta) {
                boundType = TTBounds.LOWER_BOUND;  // Fail-high result (beta cut-off)
            } else {
                boundType = TTBounds.EXACT_VALUE;  // Exact result
            }

            // Store the result in the TT
            tt.store(bestMove, fullHash, bestScore, depth, boundType);
            System.out.println("Best Move! "+ bestMove + " score of move " + bestScore);
            return bestScore;

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
        int searchDepth = 0;
        double score_full_search = 0;
        boolean fullTreeSearch ;
        ArrayList<Float> moveScores = new ArrayList<>();
        ArrayList<ScoredMove> scored_moves = new ArrayList<>();

        current_root_moves = new FastArrayList<Move>(game.moves(context).moves());
        final FastArrayList<Move> tempMovesList = new FastArrayList<Move>(current_root_moves);
        root_move_sort = new FastArrayList<Move>(current_root_moves.size());
        while (!tempMovesList.isEmpty())
        {
            root_move_sort.add(tempMovesList.removeSwap(ThreadLocalRandom.current().nextInt(tempMovesList.size())));
        }
        final int numRootMoves = root_move_sort.size();
        // So we need to get the real best move ?
        Move best_move_full_search = root_move_sort.get(0);

        while(searchDepth<maxDepth)
        {
            System.out.println("Searching ... D"+searchDepth);
            searchDepth+=1;
            float score = ALPHA_INIT;
            float alpha= ALPHA_INIT;
            final float beta = BETA_INIT;
            Move best_move = root_move_sort.get(0);
            Move move;
            for(int i = 0; i < numRootMoves;i++)
            {
                int realDepth = searchDepth - 1;
                final Context copyContext = copyContext(context);
                move = root_move_sort.get(i);
                game.apply(copyContext, move);
                
                float value = -alphaBeta(game,copyContext, realDepth, -beta,-alpha);
                moveScores.add(value);
                System.out.println(value);
                if (System.currentTimeMillis() >= stopTime) {
                    best_move = null;
                    break;
                }
                if(value>score)
                {
                    score = value;
                    best_move = move;

                }
                if(score>alpha){alpha=score;}
                System.out.println("Valdiation please");
                System.out.println(score);
                System.out.println("LL"+value);
                if (score >= beta) {
                    break;
                }


            }
            if(best_move !=  null)
            {
                if(score == this.BETA_INIT)
                {
                    return best_move;
                }
                else if (score == this.ALPHA_INIT)
                {
                    return best_move_full_search;
                }
                else
                {
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
        System.out.println("It extited the while"+best_move_full_search+" score "+ score_full_search);
        return best_move_full_search;

    }
    public Float multiCut(Game game, final Context context, final int depth, float alpha, float beta, int C, int M, int R)
    {
        int c = 0;  // Count of beta cutoffs
        int m = 0;  // Count of moves examined

        FastArrayList<Move> legalMoves = game.moves(context).moves();
        Move next = legalMoves.get(0);

        while (next != null && m < M) {
            final Context shallowContext = copyContext(context);
            game.apply(shallowContext, next);

            // Perform a shallow search with reduced depth
            float value = -alphaBeta(game, shallowContext, depth - 1 - R, -beta, -alpha);
            if (value >= beta)
            {
                c++;
                if (c >= C)
                {
                    // If we find at least C cutoffs, we prune and return beta
                    return beta;
                }
            }
            m++;
            if (m < legalMoves.size()) {
                next = legalMoves.get(m);  // Get the next move
            } else {
                next = null;  // No more moves to examine
            }

        }
        return null;
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

