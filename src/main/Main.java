package main;
import javax.swing.JFrame;

import ventana.DocenteParalelosFrame;
import ventana.LoginFrame;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(LoginFrame::new);
    }
}