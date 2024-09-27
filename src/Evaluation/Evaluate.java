package Evaluation;

import game.Game;
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

public class Evaluate {
    protected int player = -1;
    float simplescore = -1;
    float playerscoredifferenec = 0;
    int leadplayerindex = -1;

    public float evaluate(Game game, other.context.Context context) {
        TIntArrayList player_1 = context.state().owned().sites(player);
        System.out.println(player_1);
        return simplescore;
    }

//    private float differance(Game game, Context context) {
//
//        return playerscoredifferenec;
//    }
//
//    private int leading(Game game, Context context) {
//
//        return leadplayerindex;
//    }

}
