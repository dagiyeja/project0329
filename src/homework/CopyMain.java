/*31일차 숙제 풀이*/
package homework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class CopyMain extends JFrame implements ActionListener, Runnable{
	JProgressBar bar;
	JButton bt_open, bt_save, bt_copy;
	JTextField t_open, t_save;
	JFileChooser chooser; //미리 올려놈
	File file; //멤버변수로 선언-> 다른 메서드에서 이용할 수 있게. 읽어들일 파일, 복사원본
	Thread thread; //복사를 실행할 전용 쓰레드! ,Runnable과 상관은 없다 따로 줘야함
	//메인 메서드는 우리가 알고있는 그 실행부라 불리는 어플리케이션의 운영을 담당하는 역할을 수행한다..
	//따라서 절대 무한루프나 대기상태에 빠지게 해서는 안된다!!
	long total; //원본파일의 전체 용량
	
	public CopyMain() {
		bar=new JProgressBar();
		bar.setStringPainted(true);
	
		bt_open=new JButton("열기");
		bt_save=new JButton("저장");
		bt_copy=new JButton("복사실행");
	
		t_open=new JTextField(30);
		t_save=new JTextField(30);
		chooser=new JFileChooser("C:/html_workspace/images");
		
		bar.setPreferredSize(new Dimension(400, 50));
		bar.setBackground(Color.YELLOW);
		bar.setString("0%");
	
		setLayout(new FlowLayout());
		
		add(bar);
		add(bt_open);
		add(t_open);
		add(bt_save);
		add(t_save);
		add(bt_copy);
		
		bt_open.addActionListener(this);
		bt_save.addActionListener(this);
		bt_copy.addActionListener(this);
		
		setSize(450,200);
		setVisible(true);
		setLocationRelativeTo(null); //컴포넌트에 의존하지 않고 화면 가운데 출력
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	

	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource(); //이벤트를 일으킨 이벤트 소스 (이벤트 리스너에 연결된 주체)
		if(obj==bt_open){
			open();
		}
		else if(obj==bt_save){
			save();
		}
		else if(obj==bt_copy){
			//메인이 직접 복사를 수행하지 말고 
			//쓰레드에게 시키자!!
			//쓰레드 생성자에 Runnable 구현객체를 인수로 넣으면,
			//Runnable 객체에서 재정의한 run()메서드를 수행한다
			thread=new Thread(this); //내가 구현한 자료를 실행하겠다
			thread.start(); //우리꺼 run 수행!!
		}
		
	}
	
	public void open() {
		
		int result=chooser.showOpenDialog(this); //parent 여기서 container
	
		if(result==JFileChooser.APPROVE_OPTION){
			file=chooser.getSelectedFile(); //멤버변수로 뺌. 밑에서 빼려고
			t_open.setText(file.getAbsolutePath());
			total=file.length();
		}
	}
	


	public void save() {
		int result=chooser.showSaveDialog(this);
		
		if(result==JFileChooser.APPROVE_OPTION){
			File file=chooser.getSelectedFile(); //원본파일이 save된 파일로 바뀌어 버리는 에러발생! 여기는 지역변수로
			t_save.setText(file.getAbsolutePath());
		}
		
	}
	
	//버튼을 누르고 나서 계속 눌려있음-> 메인쓰레드에서 실행을 하기 때문에, 메인쓰레드로 gui제어하지 말자 
	//메인쓰레드는 무한루프에 빠뜨리지 말것! ->메인쓰레드는 프로그램 운영만, 쓰레드에서 이 작업을 한다
	//즉 copy를 쓰레드가 하면 된다
	public void copy() {
		FileInputStream fis=null;
		FileOutputStream fos=null;
		
		try {
			fis=new FileInputStream(file); //빨대를 꽃았다!
			fos=new FileOutputStream(t_save.getText()); //선택한 파일 가져옴
			
			//생성된 스트림을 통해 데이터 읽기!!
			try {
				int data;
				int count=0;
				
				while(true){
					data=fis.read(); //1byte 읽기 ->while문 돌려서 다 읽어라
					if(data==-1)break; //데이터가 없을 때 끝내라
					count++; //읽을때마다 카운트를 센다
					fos.write(data); //data를 1byte 출력하겠다
					int v=(int)getPercent(count); //강제형변환. setValue는 int형만 원하므로
					//프로그래스바에 적용
					bar.setValue(v);
					bar.setString(v+"%");
				}
				JOptionPane.showMessageDialog(this, "복사완료");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		
	}


	//Runnable->우리가 쓰레드 제어하겠다는 의도
	public void run() {
		copy(); 
	}

	//현재 진행률 구하기 공식 
	//진행율=100%*현/전체크기
	public long getPercent(int currentRead){ //읽어들이는 데이터는 계속 변하므로 받아온다
		return (100*currentRead)/total; //int형을 long형으로 나눔-> 데이터 손실 막기 위해 자동으로 long형으로 반환됨
		
	}
	
	public static void main(String[] args) {
		new CopyMain();

	}



}
