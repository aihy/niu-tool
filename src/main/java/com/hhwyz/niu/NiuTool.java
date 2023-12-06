package com.hhwyz.niu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Stopwatch;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

/**
 * @author 二牛
 * @date 2023-11-30 16:31
 */
public class NiuTool {
    private JTextArea jsonEdit;
    private JButton trimButton;
    private JButton antiEscapeButton;
    private JButton formatButton;
    private JPanel root;
    private JButton compressButton;
    private JTree jsonTree;
    private JTextField level;
    private JLabel levelLabel;
    private JButton collpseAll;
    private JButton exbandAll;
    private JScrollPane jsonTreeScrollPane;

    public NiuTool() {
        $$$setupUI$$$();
        trimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                trim(e);
            }
        });
        antiEscapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                antiEscape(e);
            }
        });
        formatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                format(e);
            }
        });


        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compress(e);
            }
        });
        jsonEdit.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }


            @Override
            public void removeUpdate(DocumentEvent e) {

                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

                update();
            }

        });
        level.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                level.selectAll();
            }
        });
        level.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String trim = level.getText().trim();
                expandTreeToLevel(trim);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String trim = level.getText().trim();
                expandTreeToLevel(trim);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                String trim = level.getText().trim();
                expandTreeToLevel(trim);
            }

            private void expandTreeToLevel(String levelString) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    reInit();
//                    collapseAll();
//                    expandAll();
                    int level = Integer.parseInt(levelString) - 1;
//                    // 确保level不小于0
                    level = Math.max(0, level);
//                    // 调用内部递归方法，当前节点是根节点，当前层级为0
                    expandNodeToLevel(level);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stopwatch.stop();
                    System.out.println("耗时：" + stopwatch.elapsed());
                }
            }
        });

        exbandAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    expandAllFaster(jsonTree);
                } finally {
                    stopwatch.stop();
                    System.out.println("耗时：" + stopwatch.elapsed());
                }
            }
        });
        collpseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    collapseAll();
                } finally {
                    stopwatch.stop();
                    System.out.println("耗时：" + stopwatch.elapsed());
                }
            }
        });
    }

    private static void expandAllFaster(JTree tree) {
        // Determine a suitable row height for the tree, based on the
        // size of the component that is used for rendering the root
        TreeCellRenderer cellRenderer = tree.getCellRenderer();
        Component treeCellRendererComponent =
                cellRenderer.getTreeCellRendererComponent(
                        tree, tree.getModel().getRoot(), false, false, false, 1, false);
        int rowHeight = treeCellRendererComponent.getPreferredSize().height + 2;
        tree.setRowHeight(rowHeight);

        // Temporarily remove all listeners that would otherwise
        // be flooded with TreeExpansionEvents
        TreeExpansionListener[] expansionListeners =
                tree.getTreeExpansionListeners();
        for (TreeExpansionListener expansionListener : expansionListeners) {
            tree.removeTreeExpansionListener(expansionListener);
        }

        // Recursively expand all nodes of the tree
        TreePath rootPath = new TreePath(tree.getModel().getRoot());
        expandAllRecursively(tree, rootPath);

        // Restore the listeners that the tree originally had
        for (TreeExpansionListener expansionListener : expansionListeners) {
            tree.addTreeExpansionListener(expansionListener);
        }

        // Trigger an update for the TreeExpansionListeners
        tree.collapsePath(rootPath);
        tree.expandPath(rootPath);
    }

    // Recursively expand the given path and its child paths in the given tree
    private static void expandAllRecursively(JTree tree, TreePath treePath) {
        TreeModel model = tree.getModel();
        Object lastPathComponent = treePath.getLastPathComponent();
        int childCount = model.getChildCount(lastPathComponent);
        if (childCount == 0) {
            return;
        }
        tree.expandPath(treePath);
        for (int i = 0; i < childCount; i++) {
            Object child = model.getChild(lastPathComponent, i);
            int grandChildCount = model.getChildCount(child);
            if (grandChildCount > 0) {
                class LocalTreePath extends TreePath {
                    private static final long serialVersionUID = 0;

                    public LocalTreePath(
                            TreePath parent, Object lastPathComponent) {
                        super(parent, lastPathComponent);
                    }
                }
                TreePath nextTreePath = new LocalTreePath(treePath, child);
                expandAllRecursively(tree, nextTreePath);
            }
        }
    }

    public static int getTreeDepth(JTree tree) {
        TreeModel model = tree.getModel();
        if (model != null) {
            TreeNode root = (TreeNode) model.getRoot();
            // 开始递归查询树的深度
            return getTreeNodeDepth(root);
        }
        // 如果树为空，则返回0
        return 0;
    }

    // 递归函数
    private static int getTreeNodeDepth(TreeNode node) {
        if (node.isLeaf()) {
            // 如果是叶子节点，则没有子节点，深度为1
            return 1;
        } else {
            int depth = 0;
            // 遍历所有子节点，找到最大深度
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode child = node.getChildAt(i);
                depth = Math.max(depth, getTreeNodeDepth(child));
            }
            // 返回当前节点的深度，即子节点最大深度+1
            return depth + 1;
        }
    }

    private void update() {
        String jsonString = jsonEdit.getText().trim();
        try {
            Object obj = JSON.parse(jsonString);
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
            buildTree(rootNode, null, obj);
            jsonTree.setModel(new DefaultTreeModel(rootNode));
        } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(this, "Invalid JSON format", "Error", JOptionPane.ERROR_MESSAGE);
//                    ex.printStackTrace();
        }
    }

    private void reInit() {
        String jsonString = jsonEdit.getText().trim();
        Object obj = JSON.parse(jsonString);
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        buildTree(rootNode, null, obj);
        jsonTree = new JTree(new DefaultTreeModel(rootNode));
        jsonTree.setLargeModel(true);
        MyTreeCellRenderer myTreeCellRenderer = new MyTreeCellRenderer();
        jsonTree.setCellRenderer(myTreeCellRenderer);
        jsonTreeScrollPane.setViewportView(jsonTree);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("NiuTool");
        frame.setContentPane(new NiuTool().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1366, 768));
        frame.pack();
//        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void expandAll() {
        int r = 0;
        while (r < jsonTree.getRowCount()) {
            jsonTree.expandRow(r);
            r++;
        }
    }

    private void collapseAll() {
        int row = jsonTree.getRowCount() - 1;
        while (row >= 0) {
            jsonTree.collapseRow(row);
            row--;
        }
        // 若需要将根节点展开，取消下面这行的注释
        // tree.expandRow(0);
    }

    private void expandNodeToLevel(int targetLevel) {
        Set<TreePath> pathList = new HashSet<>();
        findLevelPath((DefaultMutableTreeNode) jsonTree.getModel().getRoot(), 0, targetLevel, pathList);
        System.out.println("pathList.old.size=" + pathList.size());
        prunePathList(pathList);
        System.out.println("pathList.size=" + pathList.size());
        for (TreePath path : pathList) {
            jsonTree.expandPath(path);
        }
    }

    private void findLevelPath(DefaultMutableTreeNode node, int level, int targetLevel, Set<TreePath> pathList) {
        if (!node.isLeaf()) {
            pathList.add(new TreePath(node.getPath()));
        }
        if (level == targetLevel) {
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            findLevelPath(childNode, level + 1, targetLevel, pathList);
        }
    }

    private void prunePathList(Set<TreePath> pathList) {
        Iterator<TreePath> iterator = pathList.iterator();
        while (iterator.hasNext()) {
            TreePath shorterPath = iterator.next();
            for (TreePath longerPath : pathList) {
                if (shorterPath != longerPath && isSubPath(shorterPath, longerPath)) {
                    // 如果shorterPath是longerPath的子路径，则移除shorterPath
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private boolean isSubPath(TreePath shorterPath, TreePath longerPath) {
        Object[] shorterPathArray = shorterPath.getPath();
        Object[] longerPathArray = longerPath.getPath();
        if (shorterPathArray.length >= longerPathArray.length) {
            return false;
        }
        for (int i = 0; i < shorterPathArray.length; i++) {
            if (!shorterPathArray[i].equals(longerPathArray[i])) {
                return false;
            }
        }
        return true;
    }

    private void buildTree(DefaultMutableTreeNode parent, Object jsonKey, Object jsonValue) {
        class Node {
            final DefaultMutableTreeNode parent;
            final Object key;
            final Object value;

            Node(DefaultMutableTreeNode parent, Object key, Object value) {
                this.parent = parent;
                this.key = key;
                this.value = value;
            }
        }

        Stack<Node> stack = new Stack<>();
        stack.push(new Node(parent, jsonKey, jsonValue));

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            DefaultMutableTreeNode parentNode = node.parent;
            Object currentKey = node.key;
            Object currentValue = node.value;

            if (currentValue instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) currentValue;
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    String ending = "";
                    if (value instanceof JSONObject && ((JSONObject) value).isEmpty()) {
                        ending = " : {}";
                    }
                    if (value instanceof JSONArray && ((JSONArray) value).isEmpty()) {
                        ending = " : []";
                    }
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key + ending);
                    parentNode.add(childNode);
                    stack.push(new Node(childNode, key, value)); // Push child to stack instead of recursive call
                }
            } else if (currentValue instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) currentValue;
                for (int i = 0; i < jsonArray.size(); i++) {
                    Object value = jsonArray.get(i);
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("[" + i + "]");
                    parentNode.add(childNode);
                    stack.push(new Node(childNode, null, value)); // Push child to stack instead of recursive call
                }
            } else {
                if (Objects.isNull(currentKey)) {
                    parentNode.setUserObject(currentValue.toString());
                } else {
                    String left = currentKey.toString();
                    String right = currentValue == null ? "" : currentValue.toString();
                    String sb = "<html><font color=\"#dd4a68\">" +
                            left +
                            "</font> : <font color=\"#669900\">" +
                            right +
                            "</font></html>";
//                    String sb = left + " : " + right;
                    parentNode.setUserObject(sb); // Handle primitive types
                }
            }
        }
    }

    private void compress(ActionEvent evt) {
        String text = jsonEdit.getText();
        String s = text.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
        jsonEdit.setText(s);
        jsonEdit.setCaretPosition(0);
    }

    private void trim(ActionEvent evt) {
        String text = jsonEdit.getText();
        int left = jsonEdit.getCaretPosition();
        int right = -1; // 初始化right为-1，表示未找到
        if (left >= 0 && left < text.length()) {
            char leftChar = text.charAt(left); // 获取光标左侧的字符
            switch (leftChar) {
                case '{':
                case '[':
                case '(':
                    right = findMatchingBraceForwards(text, left, leftChar);
                    break;
                case '}':
                case ']':
                case ')':
                    right = findMatchingBraceBackwards(text, left, leftChar);
                    break;
                default:
                    DialogUtil.showErrorDialog(root, "ERROR", "光标必须放在括号左边");
                    return;
            }

            if (right != -1) {
                if (left > right) {
                    int temp = left;
                    left = right;
                    right = temp;
                }
                String contentBetweenBraces = text.substring(left, right + 1);
                jsonEdit.setText(contentBetweenBraces);
                jsonEdit.setCaretPosition(0);
                format(evt);
            } else {
                DialogUtil.showErrorDialog(root, "ERROR", "没有找到匹配的括号");
            }
        } else {
            DialogUtil.showErrorDialog(root, "ERROR", "光标必须放在括号左边");
        }
    }

    private int findMatchingBraceForwards(String text, int startPos, char brace) {
        Deque<Integer> stack = new ArrayDeque<>();
        char match;
        switch (brace) {
            case '{':
                match = '}';
                break;
            case '[':
                match = ']';
                break;
            case '(':
                match = ')';
                break;
            default:
                return -1; // Invalid brace
        }
        stack.push(startPos);
        for (int i = startPos + 1; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == brace) {
                stack.push(i);
            } else if (c == match) {
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            }
        }
        return -1; // No matching brace found
    }

    private int findMatchingBraceBackwards(String text, int startPos, char brace) {
        Deque<Integer> stack = new ArrayDeque<>();
        char match;
        switch (brace) {
            case '}':
                match = '{';
                break;
            case ']':
                match = '[';
                break;
            case ')':
                match = '(';
                break;
            default:
                return -1; // Invalid brace
        }
        stack.push(startPos);
        for (int i = startPos - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == brace) {
                stack.push(i);
            } else if (c == match) {
                stack.pop();
                if (stack.isEmpty()) {
                    return i;
                }
            }
        }
        return -1; // No matching brace found
    }

    private void clear(ActionEvent evt) {
        jsonEdit.setText("");
    }

    private void antiEscape(ActionEvent evt) {
        String text = jsonEdit.getText();
        text = text.replace("\\\\", "\\");
        text = text.replace("\\\"", "\"");
        jsonEdit.setText(text);
        jsonEdit.setCaretPosition(0);
    }

    private void format(ActionEvent evt) {
        String text = jsonEdit.getText();
        text = text.replaceAll("\n", "");
        text = text.replaceAll(" ", "");
        Object object = JSON.parse(text);
        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        pretty = pretty.replaceAll("\t", "    ");
        jsonEdit.setText(pretty);
        jsonEdit.setCaretPosition(0);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        root = new JPanel();
        root.setLayout(new BorderLayout(0, 0));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation(400);
        root.add(splitPane1, BorderLayout.CENTER);
        final JScrollPane scrollPane1 = new JScrollPane();
        splitPane1.setLeftComponent(scrollPane1);
        jsonEdit = new JTextArea();
        scrollPane1.setViewportView(jsonEdit);
        jsonTreeScrollPane = new JScrollPane();
        splitPane1.setRightComponent(jsonTreeScrollPane);
        jsonTree.setEditable(false);
        jsonTreeScrollPane.setViewportView(jsonTree);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        root.add(panel1, BorderLayout.SOUTH);
        exbandAll = new JButton();
        exbandAll.setText("全展开");
        panel1.add(exbandAll);
        collpseAll = new JButton();
        collpseAll.setText("全收缩");
        panel1.add(collpseAll);
        levelLabel = new JLabel();
        levelLabel.setText("展开层级：");
        panel1.add(levelLabel);
        level = new JTextField();
        level.setColumns(2);
        panel1.add(level);
        trimButton = new JButton();
        trimButton.setText("括号修剪");
        panel1.add(trimButton);
        formatButton = new JButton();
        formatButton.setText("格式化");
        panel1.add(formatButton);
        antiEscapeButton = new JButton();
        antiEscapeButton.setText("反转义");
        panel1.add(antiEscapeButton);
        compressButton = new JButton();
        compressButton.setText("压缩");
        panel1.add(compressButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

    private void createUIComponents() {
        jsonTree = new JTree(new Object[]{});
        jsonTree.setLargeModel(true);
        MyTreeCellRenderer myTreeCellRenderer = new MyTreeCellRenderer();
        jsonTree.setCellRenderer(myTreeCellRenderer);
    }
}
