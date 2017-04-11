package game.word;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GamePanel extends JPanel implements ItemListener, Runnable, ActionListener{
	GameWindow gameWindow;
	
	JPanel p_west; //왼쪽 컨트롤 영역
	JPanel p_center; //단어 그래픽 처리 영역
	
	JLabel la_user; //게임 로그인 유저명
	JLabel la_score; //게임 점수
	Choice choice; //단어 선택 드랍박스
	JTextField t_input; //게임 입력창
	JButton bt_start, bt_pause, bt_stop; //게임 시작 버튼
	String res="C:/java_workspace2/project0329/res/"; //조사할 경로
	
	FileInputStream fis;
	InputStreamReader reader; //파일을 대상으로 한 문자기반 스트림
	BufferedReader buffr; //문자 기반 버퍼스트림
	
	//조사한 단어를 담아놓자! 게임에 써먹기 위해
	ArrayList<String> wordList=new ArrayList<String>();
	Thread thread; //단어게임을 진행할 스레드
	boolean flag=true; //flag를 통해 스레드 제어예정 false면 게임중지
	boolean isDown=true;
	
	ArrayList<Word> words=new ArrayList<Word>();
	
	
	public GamePanel(GameWindow gameWindow) {
		this.gameWindow=gameWindow;
		setLayout(new BorderLayout());
		
		p_west=new JPanel();
		p_center=new JPanel(){
			//이 영역은 지금부터 그림을 그릴 영역!!
			public void paintComponent(Graphics g) {
				//기존 그림 지우기!!
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 750, 700);
				
				g.setColor(Color.BLUE);
				
				//모든 워드들에 대한 render();
				for(int i=0; i<words.size(); i++){
					words.get(i).render(g);
				}
			
			}
		};
		
		
		la_user=new JLabel("한예지 님");
		la_score=new JLabel("0점");
		choice=new Choice();
		t_input=new JTextField(10);
		bt_start=new JButton("start");
		bt_pause=new JButton("pause");
		bt_stop=new JButton("종료");
		
		p_west.setPreferredSize(new Dimension(150, 700));
		p_west.setBackground(Color.orange);
		
		choice.add("▼카테고리 선택");
		choice.setPreferredSize(new Dimension(135, 40));
		choice.addItemListener(this);
		
		p_west.add(la_user);
		p_west.add(choice);
		p_west.add(t_input);
		p_west.add(bt_start);
		p_west.add(bt_pause);
		p_west.add(bt_stop);
		p_west.add(la_score);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		//버튼과 리스너 연결
		bt_start.addActionListener(this);
		bt_pause.addActionListener(this);
		bt_stop.addActionListener(this);
		
		//텍스트 필드와 리스너 연결
		t_input.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					//화면에 존재하는 words와 입력값 비교하여
					//맞으면, words에서 객체 삭제
					String value=t_input.getText();
					
					for(int i=0; i<words.size(); i++){
						if(words.get(i).name.equals(value)){
							words.remove(i);
						}
					}
				}
			}
		});
		
		//setBackground(Color.CYAN);
		setVisible(false); //최초에 등장 안함!!
		setPreferredSize(new Dimension(900, 700));
		
		getCategory();
		p_center.repaint();
	}
	
	//초이스 컴포넌트에 채워질 파일명 조사하기
	public void getCategory(){
		File file=new File(res);
		
		//파일+디렉토리 섞여있는 배열반환
		File[] files=file.listFiles();
		for(int i=0; i<files.length; i++){
			if(files[i].isFile()){
				String name=files[i].getName();
				String[] arr=name.split("\\.");
				if(arr[1].equals("txt")){ //메모장이라면
					choice.add(name);
					
				}
			}
		}
		
	}

	// 단어 읽어오기
	public void getWord(){
		int index=choice.getSelectedIndex();
		
		if(index!=0){ //첫번째 요소는 빼고..
		String name=choice.getSelectedItem();
		System.out.println(res+name);
		
		try {
			fis=new FileInputStream(res+name);
		
			reader=new InputStreamReader(fis,"utf-8");
			
			//스트림을 버퍼 처리 수준까지 올림!!
			buffr=new BufferedReader(reader);
			String data;
			
			//기존의 wordList를 비운다!!
			wordList.removeAll(wordList);
			
			while(true){
				data=buffr.readLine(); //한 줄
				if(data==null)break;
				//System.out.println(data);
				wordList.add(data);
			}
			//준비된 단어를 화면에 보여주기
			//System.out.println("현재까지 wordList는 "+wordList.size());
			createWord();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(buffr!=null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}if(fis!=null){
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
		
	public void createWord(){
		for(int i=0; i<wordList.size(); i++){
			String name=wordList.get(i);
			Word word=new Word(name,(i*(75)+10),100);
			
			words.add(word); //워드 객체 명단 만들기
		}
	}
	
	
	//게임 시작
	public void startGame(){
		if(thread==null){ //thread를 메모리에 올린적이 없다면
			flag=true; //stopGame에서 flag를 false로 놔서 실행이 안되기 때문에 다시 심장이 뛰도록( while문 실행) 설정
			thread=new Thread(this); //우리의 runnable이 수행됨
			thread.start();
		}//1번만 수행함. 
	}
	
	//게임 중지 or 재시작
	public void pauseGame(){
		isDown=!isDown; //원터치 방식
	}
	
	/*게임종료--결국 처음으로 돌아가자!!
	 * 1.wordList(단어들이 들어있는) 비우기
	 * 2.words(Word 인스턴스들이 들어있는) 비우기
	 * 3.choice 초기화(index=0)
	 * 4.flag=false
	 * 5.thread를 null로 다시 초기화
	 * */
	public void stopGame(){
		wordList.removeAll(wordList);
		words.removeAll(words);
		choice.select(0); //첫번째 요소 강제 선택
		flag=false; //while문 중지 목적
		thread=null; //thread 초기화
	}
	
	public void itemStateChanged(ItemEvent e) {
		System.out.println("나 바꿈?");
		getWord();
		
	}

	
	//버튼 이벤트 처리
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		
		if(obj==bt_start){
			startGame();
		}else if(obj==bt_pause){
			pauseGame();
		}else if(obj==bt_stop){
			stopGame();
		}
		
	}
	
	//게임의 심장부
	public void run() {
		while(flag){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isDown==true){
			//모든 단어들에 대해서 tick()
			for(int i=0; i<words.size(); i++){
				words.get(i).tick();		
			}
			//모든 단어들에 대해서 render()
			p_center.repaint();
		}
	}
}
}
