/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
/**
 *
 * @author Windows
 */
public class DateLabelFormatter extends AbstractFormatter{
    
    private final String formato = "dd/MM/yyyy";
    private final SimpleDateFormat dateFomatter = new SimpleDateFormat();

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFomatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if(value == null){
            return "";
        }
        if(value instanceof Calendar calendario){
            return dateFomatter.format(calendario.getTime());
        }
        return "";
    }
    
}
