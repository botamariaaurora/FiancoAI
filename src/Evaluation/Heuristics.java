package Evaluation;

import java.util.HashMap;
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

public class Heuristics {
    protected int player = -1;
    private int turn;
    public int CountPieces(Game game, Context context)
    {
//        TIntArrayList player_x = context.state().owned().sites(player);
//        TIntArrayList player_x = context.state().owned().sites(player);
//        if(turn == 1)
//            for(int i = 0 ; i <= 1)

        return 1;

    }

}
