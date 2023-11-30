package com.hhwyz.niu;

import javax.swing.*;
import java.awt.*;

/**
 * @author 二牛
 * @date 2023-11-30 17:13
 */
public class DialogUtil {
    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
