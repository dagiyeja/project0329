package page;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Etc extends JPanel{
	JButton bt;
	
	public Etc() {
		bt=new JButton("기타 페이지");
		add(bt);
		
		setPreferredSize(new Dimension(700, 500));
		setBackground(Color.ORANGE);
	}
}
