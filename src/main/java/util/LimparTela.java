/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Windows
 */
public class LimparTela {
    
    public void LimparCampos(JPanel container){
        Component components[] = container.getComponents();
        for(Component component : container.getComponents()){
            if(component instanceof JTextField){
               ((JTextField) component).setText("");
            }
            if(component instanceof JComboBox){
                ((JComboBox<?>) component).setSelectedIndex(-1);
            }
        }
    }
    
}
