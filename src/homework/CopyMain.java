/*31���� ���� Ǯ��*/
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
	JFileChooser chooser; //�̸� �÷���
	File file; //��������� ����-> �ٸ� �޼��忡�� �̿��� �� �ְ�. �о���� ����, �������
	Thread thread; //���縦 ������ ���� ������! ,Runnable�� ����� ���� ���� �����
	//���� �޼���� �츮�� �˰��ִ� �� ����ζ� �Ҹ��� ���ø����̼��� ��� ����ϴ� ������ �����Ѵ�..
	//���� ���� ���ѷ����� �����¿� ������ �ؼ��� �ȵȴ�!!
	long total; //���������� ��ü �뷮
	
	public CopyMain() {
		bar=new JProgressBar();
		bar.setStringPainted(true);
	
		bt_open=new JButton("����");
		bt_save=new JButton("����");
		bt_copy=new JButton("�������");
	
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
		setLocationRelativeTo(null); //������Ʈ�� �������� �ʰ� ȭ�� ��� ���
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	

	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource(); //�̺�Ʈ�� ����Ų �̺�Ʈ �ҽ� (�̺�Ʈ �����ʿ� ����� ��ü)
		if(obj==bt_open){
			open();
		}
		else if(obj==bt_save){
			save();
		}
		else if(obj==bt_copy){
			//������ ���� ���縦 �������� ���� 
			//�����忡�� ��Ű��!!
			//������ �����ڿ� Runnable ������ü�� �μ��� ������,
			//Runnable ��ü���� �������� run()�޼��带 �����Ѵ�
			thread=new Thread(this); //���� ������ �ڷḦ �����ϰڴ�
			thread.start(); //�츮�� run ����!!
		}
		
	}
	
	public void open() {
		
		int result=chooser.showOpenDialog(this); //parent ���⼭ container
	
		if(result==JFileChooser.APPROVE_OPTION){
			file=chooser.getSelectedFile(); //��������� ��. �ؿ��� ������
			t_open.setText(file.getAbsolutePath());
			total=file.length();
		}
	}
	


	public void save() {
		int result=chooser.showSaveDialog(this);
		
		if(result==JFileChooser.APPROVE_OPTION){
			File file=chooser.getSelectedFile(); //���������� save�� ���Ϸ� �ٲ�� ������ �����߻�! ����� ����������
			t_save.setText(file.getAbsolutePath());
		}
		
	}
	
	//��ư�� ������ ���� ��� ��������-> ���ξ����忡�� ������ �ϱ� ������, ���ξ������ gui�������� ���� 
	//���ξ������ ���ѷ����� ���߸��� ����! ->���ξ������ ���α׷� ���, �����忡�� �� �۾��� �Ѵ�
	//�� copy�� �����尡 �ϸ� �ȴ�
	public void copy() {
		FileInputStream fis=null;
		FileOutputStream fos=null;
		
		try {
			fis=new FileInputStream(file); //���븦 �ɾҴ�!
			fos=new FileOutputStream(t_save.getText()); //������ ���� ������
			
			//������ ��Ʈ���� ���� ������ �б�!!
			try {
				int data;
				int count=0;
				
				while(true){
					data=fis.read(); //1byte �б� ->while�� ������ �� �о��
					if(data==-1)break; //�����Ͱ� ���� �� ������
					count++; //���������� ī��Ʈ�� ����
					fos.write(data); //data�� 1byte ����ϰڴ�
					int v=(int)getPercent(count); //��������ȯ. setValue�� int���� ���ϹǷ�
					//���α׷����ٿ� ����
					bar.setValue(v);
					bar.setString(v+"%");
				}
				JOptionPane.showMessageDialog(this, "����Ϸ�");
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


	//Runnable->�츮�� ������ �����ϰڴٴ� �ǵ�
	public void run() {
		copy(); 
	}

	//���� ����� ���ϱ� ���� 
	//������=100%*��/��üũ��
	public long getPercent(int currentRead){ //�о���̴� �����ʹ� ��� ���ϹǷ� �޾ƿ´�
		return (100*currentRead)/total; //int���� long������ ����-> ������ �ս� ���� ���� �ڵ����� long������ ��ȯ��
		
	}
	
	public static void main(String[] args) {
		new CopyMain();

	}



}
