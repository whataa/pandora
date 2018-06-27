package tech.linjiang.pandora.inspector.treenode;

import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.util.ViewKnife;


public class ViewNode implements INode {
    private int mWidth = 60;
    private int mHeight = 20;
    private int mLevel;
    private int mNodeCount = 1;
    private ViewNode mParent;
    private List<ViewNode> mChildren = new ArrayList<>();

    private Rect rect = new Rect();
    private final View view;
    private final StaticLayout layout;
    private static TextPaint TEXT_PAINT = new TextPaint() {
        {
            setAntiAlias(true);
            setTextSize(ViewKnife.dip2px(9));
            setColor(Color.BLACK);
            setStyle(Style.FILL);
            setStrokeWidth(ViewKnife.dip2px(1));
            setFlags(FAKE_BOLD_TEXT_FLAG);
        }
    };

    private ViewNode(View view, int width, int height) {
        this.view = view;
        this.mWidth = width;
        this.mHeight = height;

        layout = new StaticLayout(view.getClass().getSimpleName() + "\n("
                + ViewKnife.getIdString(view) + ")", TEXT_PAINT,
                mWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0.0f, false);
    }

    public static ViewNode create(View view, int width, int height) {
        ViewNode rootNode = new ViewNode(view, width, height);
        buildNode(rootNode,width,height);
        return rootNode;
    }

    private static void buildNode(ViewNode node, int width, int height) {
        View view = node.getView();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;
            if (group.getChildCount() > 0) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    ViewNode treeNode = new ViewNode(group.getChildAt(i), width, height);
                    node.addChild(treeNode);
                    buildNode(treeNode, width, height);
                }
            }
        }
    }

    public int getLevel() {
        return mLevel;
    }

    public Rect getRect() {
        return rect;
    }

    @Override
    public void setLevel(int level) {
        mLevel = level;
    }

    @Override
    public void setX(int x) {
        rect.set(x, rect.top, x + mWidth, rect.bottom);
    }

    @Override
    public void setY(int y) {
        rect.set(rect.left, y, rect.right, y + mHeight);
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    public View getView() {
        return view;
    }

    public StaticLayout getLayout() {
        return layout;
    }

    private void notifyParentNodeCountChanged() {
        if (mParent != null) {
            mParent.notifyParentNodeCountChanged();
        } else {
            calculateNodeCount();
        }
    }

    private int calculateNodeCount() {
        int size = 1;

        for (ViewNode child : mChildren) {
            size += child.calculateNodeCount();
        }

        return mNodeCount = size;
    }

    public void addChild(ViewNode child) {
        mChildren.add(child);
        child.mParent = this;

        notifyParentNodeCountChanged();

    }

    public int getNodeCount() {
        return mNodeCount;
    }

    public ViewNode getParent() {
        return mParent;
    }

    @Override
    public List<ViewNode> getChildren() {
        return mChildren;
    }

    public boolean hasChildren() {
        return !mChildren.isEmpty();
    }

    public boolean hasParent() {
        return mParent != null;
    }


    public boolean isFirstChild(ViewNode node) {
        return mChildren.indexOf(node) == 0;
    }
}