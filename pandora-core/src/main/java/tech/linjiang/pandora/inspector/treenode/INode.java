package tech.linjiang.pandora.inspector.treenode;

import java.util.List;

/**
 * Created by linjiang on 13/06/2018.
 * <p>
 * provide width, height, parent, children
 * <br>
 * get level, x, y
 */

public interface INode {
    /**
     * the level in tree Hierarchy
     *
     * @param level
     */
    void setLevel(int level);

    /**
     * The x coordinate of the relative root node
     *
     * @param x
     */
    void setX(int x);

    /**
     * The y coordinate of the relative root node
     *
     * @param y
     */
    void setY(int y);

    /**
     * @return width of node
     */
    int getWidth();

    /**
     * @return height of node
     */
    int getHeight();

    /**
     * @return parent of node
     */
    INode getParent();

    /**
     * @return children of node
     */
    List<? extends INode> getChildren();
}
