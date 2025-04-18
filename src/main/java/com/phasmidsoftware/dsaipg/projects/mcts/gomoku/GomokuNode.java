/*package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

/**
 * GomokuNode 实现 Node<Gomoku>，存储 MCTS 统计信息并支持节点扩展和回传。
 *//* 
public class GomokuNode implements Node<Gomoku> {

    private final GomokuState         state;
    private final GomokuMove          move;      // 导致生成本节点的那一步
    //private final GomokuNode          parent;
    private final List<GomokuNode>    children  = new ArrayList<>();
    private int                       wins      = 0;
    private int                       playouts  = 0;
    private GomokuNode parent;  

    /** 根节点构造器，move 和 parent 都是 null *//* 
    public GomokuNode(GomokuState state) {
        this(state, null, null);
    }

    /** 子节点构造器，必须传入本节点对应的 move 和父节点 *//* 
    public GomokuNode(GomokuState state, GomokuMove move, GomokuNode parent) {
        this.state  = state;
        this.move   = move;
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public State<Gomoku> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == 0;
    }

    /**
     * 延迟扩展：首次调用且非终局时，把所有合法落子生成子节点
     *//* 
    @Override
    public Collection<Node<Gomoku>> children() {
        if (children.isEmpty() && !state.isTerminal()) {
            for (Move<Gomoku> m : state.moves(state.player())) {
                GomokuState nextState = (GomokuState) state.next(m);
                // 将具体的 GomokuMove 和当前节点 as parent 传给新节点
                children.add(new GomokuNode(nextState, (GomokuMove) m, this));
            }
        }
        return Collections.unmodifiableList(children);
    }

    /**
     * 不再使用 Node.explore 中的 addChild/backPropagate 机制，
     * 如果误用这里会抛异常提醒。
     *//*/
    @Override
    public void addChild(State<Gomoku> s) {
        throw new UnsupportedOperationException("Use children() to expand");
    }

    @Override
    public void backPropagate() {
        throw new UnsupportedOperationException("Use record(int) for backpropagation");
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    /**
     * MCTS 回传用：每次模拟结束后，从叶节点开始沿父链更新 visits 和 wins。
     * @param result 模拟结果：0=白方胜,1=黑方胜,-1=平局
     *//* 
    public void record(int result) {
        playouts++;
        // 如果 result 是本节点“落子方”的胜利，就 wins++
        if (move != null && result == move.player()) {
            wins++;
        }
        if (parent != null) {
            parent.record(result);
        }
    }

    /** 外部获取本节点对应的落子 *//* 
    public GomokuMove getMove() {
        return move;
    }
}
*//* 
package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;      // ← 加上这一行
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import java.util.*;


/**
 * GomokuNode 实现 Node<Gomoku>，存储统计信息并支持跨步复用。
 *//* 
public class GomokuNode implements Node<Gomoku> {

    private final GomokuState       state;
    private final GomokuMove        move;      // 导致本节点的那一步
    private GomokuNode              parent;    // 不再是 final，方便 setParent()
    private final List<GomokuNode>  children = new ArrayList<>();
    private int                     wins      = 0;
    private int                     playouts  = 0;

    /** 根节点构造器：move 和 parent 都是 null *//* 
    public GomokuNode(GomokuState state) {
        this(state, null, null);
    }

    /** 子节点构造器：需传本步 move 与父节点 *//* 
    public GomokuNode(GomokuState state, GomokuMove move, GomokuNode parent) {
        this.state  = state;
        this.move   = move;
        this.parent = parent;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public State<Gomoku> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == 0;
    }

    /** 延迟扩展：首次调用时，如果非终局就根据 state.moves 创建子节点 *//*
    @Override
    public Collection<Node<Gomoku>> children() {
        if (children.isEmpty() && !state.isTerminal()) {
            for (Move<Gomoku> m : state.moves(state.player())) {
                GomokuState ns = (GomokuState) state.next(m);
                children.add(new GomokuNode(ns, (GomokuMove) m, this));
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public void addChild(State<Gomoku> s) {
        throw new UnsupportedOperationException("Use children() to expand");
    }

    @Override
    public void backPropagate() {
        throw new UnsupportedOperationException("Use record(int) instead");
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    /**
     * MCTS 回传：从叶子节点开始沿父链更新 playouts 和 wins。
     * @param result 0=白胜,1=黑胜,-1=平局
     *//* 
    public void record(int result) {
        playouts++;
        if (move != null && result == move.player()) {
            wins++;
        }
        if (parent != null) {
            parent.record(result);
        }
    }

    /** 获取本节点对应的落子 *//* 
    public GomokuMove getMove() {
        return move;
    }

    /** 
     * 跨步复用用：把这个节点当作新根后，需要断开旧的 parent 引用 
     *//* 
    public void setParent(GomokuNode parent) {
        this.parent = parent;
    }
}*//* 
package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;   // ← 一定要有


public class GomokuNode implements Node<Gomoku> {

    private final GomokuState         state;
    private final GomokuMove          move;      // 本节点对应的落子
    private GomokuNode                parent;    // 可变，用于跨步保留
    private final List<GomokuNode>    children = new ArrayList<>();
    private int                       wins = 0, visits = 0;

    public GomokuNode(GomokuState state) {
        this(state, null, null);
    }
    public GomokuNode(GomokuState state, GomokuMove move, GomokuNode parent) {
        this.state = state;
        this.move  = move;
        this.parent= parent;
    }

    @Override public boolean isLeaf() { return children.isEmpty(); }
    @Override public State<Gomoku> state() { return state; }
    @Override public boolean white() { return state.player()==0; }

    /** 延迟扩展：调用 children() 实现 *//* 
    @Override
    public Collection<Node<Gomoku>> children() {
        if (children.isEmpty() && !state.isTerminal()) {
            for (Move<Gomoku> m : state.moves(state.player())) {
                GomokuState ns = (GomokuState) state.next(m);
                children.add(new GomokuNode(ns, (GomokuMove)m, this));
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override public void addChild(State<Gomoku> s) { throw new UnsupportedOperationException(); }
    @Override public void backPropagate() { throw new UnsupportedOperationException(); }
    @Override public int wins() { return wins; }
    @Override public int playouts() { return visits; }

    /**
     * 回传结果：从叶子到根更新 visits 和 wins
     * @param result 0=白胜,1=黑胜,-1=平局
     *//* 
    public void record(int result) {
        visits++;
        if (move!=null && result==move.player()) wins++;
        if (parent!=null) parent.record(result);
    }

    public GomokuMove getMove() { return move; }

    /** 用于跨步保留子树 *//* 
    public void setParent(GomokuNode parent) { this.parent = parent; }
}*/
package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.*;

public class GomokuNode implements Node<Gomoku> {

    private final GomokuState      state;
    private final GomokuMove       move;    // 从父节点落到本节点的那步
    private GomokuNode             parent;  // 可变，用于 advanceRoot
    private final List<GomokuNode> children = new ArrayList<>();
    private int                    wins=0, visits=0;

    public GomokuNode(GomokuState state) {
        this(state,null,null);
    }
    public GomokuNode(GomokuState state, GomokuMove move, GomokuNode parent) {
        this.state  = state;
        this.move   = move;
        this.parent = parent;
    }

    @Override public boolean isLeaf()           { return children.isEmpty(); }
    @Override public State<Gomoku> state()      { return state; }
    @Override public boolean white()            { return state.player()==0; }

    @Override
    public Collection<Node<Gomoku>> children() {
        if (children.isEmpty() && !state.isTerminal()) {
            for (Move<Gomoku> m: state.moves(state.player())) {
                GomokuState ns = (GomokuState)state.next(m);
                children.add(new GomokuNode(ns,(GomokuMove)m,this));
            }
        }
        return Collections.unmodifiableList(children);
    }

    @Override public void addChild(State<Gomoku> s)     { throw new UnsupportedOperationException(); }
    @Override public void backPropagate()               { throw new UnsupportedOperationException(); }
    @Override public int wins()                         { return wins; }
    @Override public int playouts()                     { return visits; }

    public void record(int result) {
        visits++;
        if (move!=null && result==move.player()) wins++;
        if (parent!=null) parent.record(result);
    }

    public GomokuMove getMove() { return move; }
    public void setParent(GomokuNode p) { this.parent=p; }
}