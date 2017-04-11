package page;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainApp extends JFrame implements ActionListener{
	JButton[] menu=new JButton[3];
	JPanel p_north;
	URL[] url=new URL[3];
	String[] path={"/login.png","/content.png","/etc.png"};
	ImageIcon[] icon=new ImageIcon[3];
	
	//페이지들을 보유한다!!
	JPanel p_center; //페이지들이 붙을 곳!!
	LoginForm loginForm;
	Content content;
	Etc etc;
	JPanel[] page=new JPanel[3]; //생성한 패널들을 넣을 배열!
	
	public MainApp() {
		p_north=new JPanel();
		
		for(int i=0; i<path.length; i++){
			url[i]=this.getClass().getResource(path[i]);
			menu[i]=new JButton(new ImageIcon(url[i]));
			p_north.add(menu[i]);
			menu[i].addActionListener(this); //버튼과 리스너 연결
		}
		
		add(p_north,BorderLayout.NORTH);
		
		p_center=new JPanel();
		page[0]=new LoginForm();  //로그인 폼 생성!
		page[1]=new Content(); //콘텐츠 생성
		page[2]=new Etc(); //기타 페이지 
		
		p_center.add(page[0]);//메인 프레임에 붙임
		p_center.add(page[1]);
		p_center.add(page[2]); 
		
		add(p_center);
		
		
		setSize(700,600);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
	
		for(int i=0; i<page.length; i++)
			if(obj==menu[i]){
				page[i].setVisible(true);
			}else{
				page[i].setVisible(false);
				
			}
			
		}
	

	public static void main(String[] args) {
		new MainApp();

	}


}
