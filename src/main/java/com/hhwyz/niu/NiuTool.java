package com.hhwyz.niu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

            private void update() {
                String jsonString = jsonEdit.getText().trim();
                try {
                    Object obj = JSON.parse(jsonString);
                    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("JSON");
                    buildTree(rootNode, null, obj);
                    jsonTree.setModel(new DefaultTreeModel(rootNode));
                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(this, "Invalid JSON format", "Error", JOptionPane.ERROR_MESSAGE);
//                    ex.printStackTrace();
                }
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
                try {
                    collapseAll();
                    int level = Integer.parseInt(levelString);
                    // 确保level不小于0
                    level = Math.max(0, level);
                    // 调用内部递归方法，当前节点是根节点，当前层级为0
                    expandNodeToLevel((DefaultMutableTreeNode) jsonTree.getModel().getRoot(), level);
                } catch (Exception ignore) {
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

            //            private void expandNodeToLevel(DefaultMutableTreeNode node, int targetLevel, int currentLevel) {
//                // 检查当前层级是否小于目标层级
//                if (currentLevel < targetLevel) {
//                    // 如果是，则遍历所有子节点
//                    for (int i = 0; i < node.getChildCount(); i++) {
//                        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
//                        // 递归调用此方法，层级增加
//                        expandNodeToLevel(childNode, targetLevel, currentLevel + 1);
//                    }
//                }
//                if (currentLevel <= targetLevel) {
//                    // 展开当前节点的路径
//                    jsonTree.expandPath(new TreePath(node.getPath()));
//                }
//            }
            private void expandNodeToLevel(DefaultMutableTreeNode node, int targetLevel) {
                // 创建栈保存节点及其层级
                Stack<DefaultMutableTreeNode> stack = new Stack<>();
                Stack<Integer> levels = new Stack<>();

                // 初始化栈
                stack.push(node);
                levels.push(0);

                // 循环直到栈为空
                while (!stack.isEmpty()) {
                    // 从栈中取出当前节点及其层级
                    DefaultMutableTreeNode currentNode = stack.pop();
                    int currentLevel = levels.pop();

                    // 如果当前层级小于目标层级，则将子节点压入栈中
                    if (currentLevel < targetLevel) {
                        for (int i = 0; i < currentNode.getChildCount(); i++) {
                            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) currentNode.getChildAt(i);
                            stack.push(childNode);
                            levels.push(currentLevel + 1); // 子节点的层级是当前层级 + 1
                        }
                    }

                    // 展开当前节点的路径（如果当前层级不超过目标层级）
                    if (currentLevel <= targetLevel) {
                        jsonTree.expandPath(new TreePath(currentNode.getPath()));
                    }
                }
            }
        });
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
                    parentNode.setUserObject("<html><font color=\"#dd4a68\">\"" + left + "\"</font> : <font color=\"#669900\">\"" + right + "\"</font></html>"); // Handle primitive types
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
        final JScrollPane scrollPane2 = new JScrollPane();
        splitPane1.setRightComponent(scrollPane2);
        jsonTree.setEditable(true);
        scrollPane2.setViewportView(jsonTree);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        root.add(panel1, BorderLayout.SOUTH);
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
    }
}
