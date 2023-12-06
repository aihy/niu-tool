package com.hhwyz.niu;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import sun.swing.DefaultLookup;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 二牛
 * @date 2023-12-05 13:31
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    static AtomicInteger atomicInteger = new AtomicInteger(0);
    static Set<CellRendererRequest> set = new HashSet<>();
    private final Cache<Object, Component> cache;

    public MyTreeCellRenderer() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(10000)
                .build();
    }


    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
//        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        CellRendererRequest cellRendererRequest = new CellRendererRequest(tree, value, selected, expanded, leaf, row, hasFocus);
        try {
            return cache.get(cellRendererRequest, () -> getTreeCellRendererComponentSuper(tree, value, selected, expanded, leaf, row, hasFocus));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    public Component getTreeCellRendererComponentSuper(JTree tree, Object value,
                                                       boolean sel,
                                                       boolean expanded,
                                                       boolean leaf, int row,
                                                       boolean hasFocus) {

        JLabel jLabel = new JLabel();
        String stringValue = tree.convertValueToText(value, sel,
                expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        jLabel.setText(stringValue);

        Color fg = null;
//        isDropCell = false;

        JTree.DropLocation dropLocation = tree.getDropLocation();
        if (dropLocation != null
                && dropLocation.getChildIndex() == -1
                && tree.getRowForPath(dropLocation.getPath()) == row) {

            Color col = DefaultLookup.getColor(this, ui, "Tree.dropCellForeground");
            if (col != null) {
                fg = col;
            } else {
                fg = getTextSelectionColor();
            }

//            isDropCell = true;
        } else if (sel) {
            fg = getTextSelectionColor();
        } else {
            fg = getTextNonSelectionColor();
        }

        jLabel.setForeground(fg);

        Icon icon = null;
        if (leaf) {
            icon = getLeafIcon();
        } else if (expanded) {
            icon = getOpenIcon();
        } else {
            icon = getClosedIcon();
        }

        if (!tree.isEnabled()) {
            jLabel.setEnabled(false);
            LookAndFeel laf = UIManager.getLookAndFeel();
            Icon disabledIcon = laf.getDisabledIcon(tree, icon);
            if (disabledIcon != null) icon = disabledIcon;
            jLabel.setDisabledIcon(icon);
        } else {
            jLabel.setEnabled(true);
            jLabel.setIcon(icon);
        }
        jLabel.setComponentOrientation(tree.getComponentOrientation());

//        super.selected = sel;
        if (sel) {
            jLabel.setBackground(getBackgroundSelectionColor());
            jLabel.setOpaque(true); // 确保背景颜色被绘制
        } else {
            jLabel.setBackground(getBackgroundNonSelectionColor());
            jLabel.setOpaque(false); // 在非选中状态下不需要绘制背景
        }


        return jLabel;
    }

    public Component load(CellRendererRequest cellRendererRequest) {
        JTree tree = cellRendererRequest.getTree();
        Object value = cellRendererRequest.getValue();
        boolean selected = cellRendererRequest.isSel();
        boolean expanded = cellRendererRequest.isExpanded();
        boolean leaf = cellRendererRequest.isLeaf();
        int row = cellRendererRequest.getRow();
        boolean hasFocus = cellRendererRequest.isHasFocus();

        JLabel jLabel = new JLabel();
        String stringValue = tree.convertValueToText(value, selected,
                expanded, leaf, row, hasFocus);

        this.hasFocus = hasFocus;
        jLabel.setText(stringValue);

        Color fg = null;
        boolean isDropCell;
        isDropCell = false;

        JTree.DropLocation dropLocation = tree.getDropLocation();
        if (dropLocation != null
                && dropLocation.getChildIndex() == -1
                && tree.getRowForPath(dropLocation.getPath()) == row) {

            Color col = DefaultLookup.getColor(this, ui, "Tree.dropCellForeground");
            if (col != null) {
                fg = col;
            } else {
                fg = getTextSelectionColor();
            }

            isDropCell = true;
        } else if (selected) {
            fg = getTextSelectionColor();
        } else {
            fg = getTextNonSelectionColor();
        }

        jLabel.setForeground(fg);

        Icon icon = null;
        if (leaf) {
            icon = getLeafIcon();
        } else if (expanded) {
            icon = getOpenIcon();
        } else {
            icon = getClosedIcon();
        }

        if (!tree.isEnabled()) {
            setEnabled(false);
            LookAndFeel laf = UIManager.getLookAndFeel();
            Icon disabledIcon = laf.getDisabledIcon(tree, icon);
            if (disabledIcon != null) icon = disabledIcon;
            jLabel.setDisabledIcon(icon);
        } else {
            jLabel.setEnabled(true);
            jLabel.setIcon(icon);
        }
        setComponentOrientation(tree.getComponentOrientation());


        return jLabel;
    }
}
