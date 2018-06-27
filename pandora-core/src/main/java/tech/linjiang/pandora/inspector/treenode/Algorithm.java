package tech.linjiang.pandora.inspector.treenode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linjiang on 13/06/2018.
 * <p>
 * <pre>
 * Improving Walker's Algorithm to Run in Linear Time (2002)
 * by Christoph Buchheim , Michael Jünger , Sebastian Leipert
 * </pre>
 * https://llimllib.github.io/pymag-trees/
 */
class Algorithm {

    private int MARGIN_SIBLING = 100;
    private int MARGIN_SUBTREE = 100;

    public Algorithm() {
    }

    public Algorithm(int siblingMargin, int subtreeMargin) {
        MARGIN_SIBLING = siblingMargin;
        MARGIN_SUBTREE = subtreeMargin;
    }

    private Map<INode, NodeData> mNodeData = new HashMap<>();


    private NodeData createNodeData(INode node) {
        NodeData nodeData = new NodeData();
        nodeData.mAncestor = node;
        mNodeData.put(node, nodeData);
        return nodeData;
    }

    private NodeData getNodeData(INode node) {
        return mNodeData.get(node);
    }

    private void firstWalk(INode node, int depth, int number) {
        NodeData nodeData = createNodeData(node);
        nodeData.mDepth = depth;
        nodeData.mNumber = number;

        if (isLeaf(node)) {
            if (hasLeftSibling(node)) {
                INode leftSibling = getLeftSibling(node);
                nodeData.mPrelim = getPrelim(leftSibling) + getSpacing(leftSibling, node);
            }
        } else {
            INode leftMost = getLeftMostChild(node);
            INode rightMost = getRightMostChild(node);
            INode defaultAncestor = leftMost;

            INode next = leftMost;
            int i = 1;
            while (next != null) {
                firstWalk(next, depth + 1, i++);
                defaultAncestor = apportion(next, defaultAncestor);

                next = getRightSibling(next);
            }

            executeShifts(node);

            double midPoint = 0.5 * ((getPrelim(leftMost)
                    + getPrelim(rightMost) + rightMost.getWidth()) - node.getWidth());

            if (hasLeftSibling(node)) {
                INode leftSibling = getLeftSibling(node);
                nodeData.mPrelim = getPrelim(leftSibling) + getSpacing(leftSibling, node);
                nodeData.mModifier = nodeData.mPrelim - midPoint;
            } else {
                nodeData.mPrelim = midPoint;
            }
        }
    }

    private void secondWalk(INode node, double modifier) {
        NodeData nodeData = getNodeData(node);
        node.setX((int) (nodeData.mPrelim + modifier));
        node.setY(nodeData.mDepth * (MARGIN_SUBTREE + node.getHeight()));
        node.setLevel(nodeData.mDepth);

        for (INode w : node.getChildren()) {
            secondWalk(w, modifier + nodeData.mModifier);
        }
    }

    private void executeShifts(INode node) {
        double shift = 0, change = 0;
        INode w = getRightMostChild(node);
        while (w != null) {
            NodeData nodeData = getNodeData(w);

            nodeData.mPrelim = nodeData.mPrelim + shift;
            nodeData.mModifier = nodeData.mModifier + shift;
            change += nodeData.mChange;
            shift += nodeData.mShift + change;

            w = getLeftSibling(w);
        }
    }

    private INode apportion(INode node, INode defaultAncestor) {
        if (hasLeftSibling(node)) {
            INode leftSibling = getLeftSibling(node);

            INode vip = node;
            INode vop = node;
            INode vim = leftSibling;
            INode vom = getLeftMostChild(vip.getParent());

            double sip = getModifier(vip);
            double sop = getModifier(vop);
            double sim = getModifier(vim);
            double som = getModifier(vom);

            INode nextRight = nextRight(vim);
            INode nextLeft = nextLeft(vip);

            while (nextRight != null && nextLeft != null) {
                vim = nextRight;
                vip = nextLeft;
                vom = nextLeft(vom);
                vop = nextRight(vop);

                setAncestor(vop, node);

                double shift = (getPrelim(vim) + sim) - (getPrelim(vip) + sip) + getSpacing(vim, vip);
                if (shift > 0) {
                    moveSubtree(ancestor(vim, node, defaultAncestor), node, shift);
                    sip += shift;
                    sop += shift;
                }

                sim += getModifier(vim);
                sip += getModifier(vip);
                som += getModifier(vom);
                sop += getModifier(vop);

                nextRight = nextRight(vim);
                nextLeft = nextLeft(vip);
            }

            if (nextRight != null && nextRight(vop) == null) {
                setThread(vop, nextRight);
                setModifier(vop, getModifier(vop) + sim - sop);
            }

            if (nextLeft != null && nextLeft(vom) == null) {
                setThread(vom, nextLeft);
                setModifier(vom, getModifier(vom) + sip - som);
                defaultAncestor = node;
            }
        }

        return defaultAncestor;
    }

    private void setAncestor(INode v, INode ancestor) {
        getNodeData(v).mAncestor = ancestor;
    }

    private void setModifier(INode v, double modifier) {
        getNodeData(v).mModifier = modifier;
    }

    private void setThread(INode v, INode thread) {
        getNodeData(v).mThread = thread;
    }

    private double getPrelim(INode v) {
        return getNodeData(v).mPrelim;
    }

    private double getModifier(INode vip) {
        return getNodeData(vip).mModifier;
    }

    private void moveSubtree(INode wm, INode wp, double shift) {
        NodeData wpNodeData = getNodeData(wp);
        NodeData wmNodeData = getNodeData(wm);

        int subtrees = wpNodeData.mNumber - wmNodeData.mNumber;
        wpNodeData.mChange = wpNodeData.mChange - shift / subtrees;
        wpNodeData.mShift = wpNodeData.mShift + shift;
        wmNodeData.mChange = wmNodeData.mChange + shift / subtrees;
        wpNodeData.mPrelim = wpNodeData.mPrelim + shift;
        wpNodeData.mModifier = wpNodeData.mModifier + shift;
    }

    private INode ancestor(INode vim, INode node, INode defaultAncestor) {
        NodeData vipNodeData = getNodeData(vim);

        if (vipNodeData.mAncestor.getParent() == node.getParent()) {
            return vipNodeData.mAncestor;
        }

        return defaultAncestor;
    }

    private INode nextRight(INode node) {
        if (node.getChildren() != null && node.getChildren().size() > 0) {
            return getRightMostChild(node);
        }

        return getNodeData(node).mThread;
    }

    private INode nextLeft(INode node) {
        if (node.getChildren() != null && node.getChildren().size() > 0) {
            return getLeftMostChild(node);
        }

        return getNodeData(node).mThread;
    }

    private int getSpacing(INode leftNode, INode rightNode) {
        return MARGIN_SIBLING + leftNode.getWidth();
    }

    private boolean isLeaf(INode node) {
        return node.getChildren().isEmpty();
    }

    private INode getLeftSibling(INode node) {
        if (!hasLeftSibling(node)) {
            return null;
        }

        INode parent = node.getParent();
        List<? extends INode> children = parent.getChildren();
        int nodeIndex = children.indexOf(node);
        return children.get(nodeIndex - 1);
    }

    private boolean hasLeftSibling(INode node) {
        INode parent = node.getParent();
        if (parent == null) {
            return false;
        }

        int nodeIndex = parent.getChildren().indexOf(node);
        return nodeIndex > 0;
    }

    private INode getRightSibling(INode node) {
        if (!hasRightSibling(node)) {
            return null;
        }

        INode parent = node.getParent();
        List<? extends INode> children = parent.getChildren();
        int nodeIndex = children.indexOf(node);
        return children.get(nodeIndex + 1);
    }

    private boolean hasRightSibling(INode node) {
        INode parent = node.getParent();
        if (parent == null) {
            return false;
        }

        List<? extends INode> children = parent.getChildren();
        int nodeIndex = children.indexOf(node);
        return nodeIndex < children.size() - 1;
    }

    private INode getLeftMostChild(INode node) {
        return node.getChildren().get(0);
    }

    private INode getRightMostChild(INode node) {
        List<? extends INode> children = node.getChildren();
        if (children.isEmpty()) {
            return null;
        }

        return children.get(children.size() - 1);
    }

    public void calc(INode root) {
        mNodeData.clear();

        firstWalk(root, 0, 0);
        secondWalk(root, -getPrelim(root));
    }


    private static class NodeData {
        private INode mAncestor;
        private INode mThread;
        private int mNumber;
        private int mDepth;
        private double mPrelim;
        private double mModifier;
        private double mShift;
        private double mChange;
    }
}