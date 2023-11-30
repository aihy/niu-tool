package com.hhwyz.niu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author 二牛
 * @date 2023-11-30 16:31
 */
public class NiuTool {
    private JTextArea jTextArea1;
    private JButton trimButton;
    private JButton antiEscapeButton;
    private JButton formatButton;
    private JButton clearButton;
    private JPanel root;
    private JButton compressButton;
    private JButton copyButton;

    public NiuTool() {
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
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear(e);
            }
        });


        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.selectAll();
                jTextArea1.copy();
            }
        });
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compress(e);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("NiuTool");
        frame.setContentPane(new NiuTool().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void compress(ActionEvent evt) {
        String text = jTextArea1.getText();
        String s = text.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
        jTextArea1.setText(s);
        jTextArea1.setCaretPosition(0);
    }

    private void trim(ActionEvent evt) {
        String text = jTextArea1.getText();
        int left = jTextArea1.getCaretPosition();
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
                jTextArea1.setText(contentBetweenBraces);
                jTextArea1.setCaretPosition(0);
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
        jTextArea1.setText("");
    }

    private void antiEscape(ActionEvent evt) {
        String text = jTextArea1.getText();
        text = text.replace("\\\\", "\\");
        text = text.replace("\\\"", "\"");
        jTextArea1.setText(text);
        jTextArea1.setCaretPosition(0);
    }

    private void format(ActionEvent evt) {
        String text = jTextArea1.getText();
        text = text.replaceAll("\n", "");
        text = text.replaceAll(" ", "");
        Object object = JSON.parse(text);
        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        pretty = pretty.replaceAll("\t", "    ");
        jTextArea1.setText(pretty);
        jTextArea1.setCaretPosition(0);
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
        root = new JPanel();
        root.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        root.add(scrollPane1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        jTextArea1 = new JTextArea();
        scrollPane1.setViewportView(jTextArea1);
        trimButton = new JButton();
        trimButton.setText("括号修剪");
        root.add(trimButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        formatButton = new JButton();
        formatButton.setText("格式化");
        root.add(formatButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        antiEscapeButton = new JButton();
        antiEscapeButton.setText("反转义");
        root.add(antiEscapeButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clearButton = new JButton();
        clearButton.setText("清空");
        root.add(clearButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        compressButton = new JButton();
        compressButton.setText("压缩");
        root.add(compressButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        copyButton = new JButton();
        copyButton.setText("全选并拷贝");
        root.add(copyButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return root;
    }

}
