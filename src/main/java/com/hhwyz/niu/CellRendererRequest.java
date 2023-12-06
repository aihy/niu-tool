package com.hhwyz.niu;

import com.google.common.base.Objects;
import lombok.Data;

import javax.swing.*;

/**
 * @author 二牛
 * @date 2023-12-05 14:18
 */
@Data
public class CellRendererRequest {
    private JTree tree;
    private Object value;
    private boolean sel;
    private boolean expanded;
    private boolean leaf;
    private int row;
    private boolean hasFocus;
    public CellRendererRequest(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.tree = tree;
        this.value = value;
        this.sel = sel;
        this.expanded = expanded;
        this.leaf = leaf;
        this.row = row;
        this.hasFocus = hasFocus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellRendererRequest request = (CellRendererRequest) o;
        return sel == request.sel && expanded == request.expanded && leaf == request.leaf && row == request.row && hasFocus == request.hasFocus && Objects.equal(value.toString(), request.value.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value.toString(), sel, expanded, leaf, row, hasFocus);
    }
}
