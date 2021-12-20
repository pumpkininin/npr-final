package client;

import javax.swing.*;
import java.awt.*;

public class ListRender extends JLabel implements ListCellRenderer<ChatGUI.User> {
    public ListRender(){
        setOpaque(true);
    }
    @Override
    public Component getListCellRendererComponent(JList<? extends ChatGUI.User> list, ChatGUI.User value, int index, boolean isSelected, boolean cellHasFocus) {
        String name =value.getName();
        ImageIcon ic = new ImageIcon(getClass().getResource("/images/hanu-1.png"));
        setText(name);
        setIcon(ic);
        setFont(new Font("Verdana", Font.PLAIN, 12));
        if(isSelected){
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }else{
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}
