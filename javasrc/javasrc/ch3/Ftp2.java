// ftp �v���O����Ftp2.java
// ���̃v���O������,ftp �T�[�o�Ɛڑ����܂�
// �f�[�^�p�R�l�N�V������p�ӂ�,LIST�R�}���h�����s���܂�
// �t�@�C���]���@�\�͂���܂���
// �g����java Ftp2 �T�[�o�A�h���X
// �N���̗�java Ftp2 kiku.fuis.fukui-u.ac.jp

// ���C�u�����̗��p
import java.net.*;
import java.io.*;

// Ftp2�N���X
public class Ftp2 {
	// �\�P�b�g�̏���
	Socket ctrlSocket;//����p�\�P�b�g
	public PrintWriter ctrlOutput;//����o�͗p�X�g���[��
	public BufferedReader ctrlInput;// �����͗p�X�g���[��

	final int CTRLPORT = 21 ;// ftp �̐���p�|�[�g

	// openConnection���\�b�h
	//�A�h���X�ƃ|�[�g�ԍ�����\�P�b�g����萧��p�X�g���[�����쐬���܂�
	public void openConnection(String host)
		throws IOException,UnknownHostException
	{
		ctrlSocket = new Socket(host, CTRLPORT);
		ctrlOutput = new PrintWriter(ctrlSocket.getOutputStream());
		ctrlInput
			= new BufferedReader(new InputStreamReader
								(ctrlSocket.getInputStream()));
	}

	// closeConnection���\�b�h
	//����p�̃\�P�b�g����܂�
	public void closeConnection()
		throws IOException
	{
		ctrlSocket.close() ;
	}

	// showMenu���\�b�h
	// Ftp �̃R�}���h���j���[���o�͂��܂�
	public void showMenu()
	{
		System.out.println(">Command?") ;
		System.out.print("1 login") ;
		System.out.print(" 2 ls") ;
		System.out.print(" 3 cd") ;
		System.out.println(" 9 quit") ;
	}

	// getCommand���\�b�h
	// ���p�҂̎w�肵���R�}���h�ԍ���ǂݎ��܂�
	public String getCommand()
	{
		String buf = "" ;
		BufferedReader lineread
			= new BufferedReader(new InputStreamReader(System.in)) ;

		while(buf.length() != 1){// �P�����̓��͂��󂯂�܂ŌJ��Ԃ�
			try{
				buf = lineread.readLine() ;
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		return (buf) ;
	}

	// doLogin���\�b�h
	// ftp �T�[�o�Ƀ��O�C�����܂�
	public void doLogin()
	{
		String loginName = "" ;
		String password = "" ;
		BufferedReader lineread
			= new BufferedReader(new InputStreamReader(System.in)) ;

		try{
			System.out.println("���O�C��������͂��Ă�������") ;
			loginName = lineread.readLine() ;
			// USER�R�}���h�ɂ�郍�O�C��
			ctrlOutput.println("USER " + loginName) ;
			ctrlOutput.flush() ;
			// PASS�R�}���h�ɂ��p�X���[�h�̓���
			System.out.println("�p�X���[�h����͂��Ă�������") ;
			password = lineread.readLine() ;
			ctrlOutput.println("PASS " + password) ;
			ctrlOutput.flush() ;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	// doQuit���\�b�h
	// ftp �T�[�o���烍�O�A�E�g���܂�
	public void doQuit()
	{
		try{
			ctrlOutput.println("QUIT ") ;// QUIT�R�}���h�̑��M
			ctrlOutput.flush() ;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	// doCd���\�b�h
	// �f�B���N�g����ύX���܂�
	public void doCd()
	{
		String dirName = "" ;
		BufferedReader lineread
			= new BufferedReader(new InputStreamReader(System.in)) ;

		try{
			System.out.println("�f�B���N�g��������͂��Ă�������") ;
			dirName = lineread.readLine() ;
			ctrlOutput.println("CWD " + dirName) ;// CWD�R�}���h
			ctrlOutput.flush() ;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	// doLs���\�b�h
	// �f�B���N�g�����𓾂܂�
	public void doLs()
	{
		try{
			int n ;
			byte[] buff = new byte[1024] ;
			// �f�[�^�p�R�l�N�V�������쐬���܂�
			Socket dataSocket = dataConnection("LIST") ;
			// �f�[�^�ǂݎ��p�X�g���[����p�ӂ��܂�
			BufferedInputStream dataInput
				= new BufferedInputStream(dataSocket.getInputStream()) ;
			// �f�B���N�g������ǂݎ��܂�
			while((n = dataInput.read(buff)) > 0){
				System.out.write(buff,0,n) ;
			}
			dataSocket.close() ;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	// dataConnection���\�b�h
	// �T�[�o�Ƃ̃f�[�^�����p�Ƀ\�P�b�g�����܂�
	// �܂�,�T�[�o�ɑ΂���port�R�}���h�Ń|�[�g��ʒm���܂�
	public Socket dataConnection(String ctrlcmd)
	{
		String cmd = "PORT " ; //PORT�R�}���h�ő���f�[�^�̊i�[�p�ϐ�
		int i ;
		Socket dataSocket = null ;// �f�[�^�]���p�\�P�b�g
		try{
			// �����̃A�h���X�����߂܂�
			byte[] address = InetAddress.getLocalHost().getAddress() ;
			// �K���ȃ|�[�g�ԍ��̃T�[�o�\�P�b�g�����܂�
			ServerSocket serverDataSocket = new ServerSocket(0,1) ;
			// PORT�R�}���h�p�̑��M�f�[�^��p�ӂ��܂�
			for(i = 0; i < 4; ++i)
				cmd = cmd + (address[i] & 0xff) + "," ;
			cmd = cmd + (((serverDataSocket.getLocalPort()) / 256) & 0xff)
					  + ","
					  + (serverDataSocket.getLocalPort() & 0xff) ;
			// PORT�R�}���h�𐧌�p�X�g���[����ʂ��đ���܂�
			ctrlOutput.println(cmd) ;
			ctrlOutput.flush() ;
			// �����ΏۃR�}���h�iLIST,RETR,�����STOR�j���T�[�o�ɑ���܂�
			ctrlOutput.println(ctrlcmd) ;
			ctrlOutput.flush() ;
			// �T�[�o����̐ڑ����󂯕t���܂�
			dataSocket = serverDataSocket.accept() ;
			serverDataSocket.close() ;
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		return dataSocket ;
	}

	// execCommand���\�b�h
	// �R�}���h�ɑΉ�����e�������Ăяo���܂�
	public boolean execCommand(String command)
	{
		boolean cont = true ;

		switch(Integer.parseInt(command)){
		case 1 : // login ����
			doLogin() ;
			break ;
		case 2 : // �T�[�o�̃f�B���N�g���\������
			doLs() ;
			break ;
		case 3 : // �T�[�o�̍�ƃf�B���N�g���ύX����
			doCd() ;
			break ;
		case 9 : // �����̏I��
			doQuit() ;
			cont = false ;
			break ;
		default : //����ȊO�̓���
			System.out.println("�ԍ���I�����Ă�������") ;
		}
		return(cont) ;
	}

	// main_proc���\�b�h
	// Ftp �̃R�}���h���j���[���o�͂���,�e�������Ăяo���܂�
	public void main_proc()
		throws IOException
	{
		boolean cont = true ;
		try {
			while(cont){
				// ���j���[���o�͂��܂�
				showMenu() ;
				// �R�}���h���󂯎����s���܂�
				cont = execCommand(getCommand()) ;
			}
		}
		catch(Exception e){
			System.err.print(e);
			System.exit(1);
		}
	}

	// getMsgs���\�b�h
	// ����X�g���[���̎�M�X���b�h���J�n���܂�
	public void getMsgs(){
		try {
			CtrlListen listener = new CtrlListen(ctrlInput) ;
			Thread listenerthread = new Thread(listener) ;
			listenerthread.start() ;
		}catch(Exception e){
			e.printStackTrace() ;
			System.exit(1) ;
		}
	}

	// main���\�b�h
	// TCP�R�l�N�V�������J���ď������J�n���܂�
	public static void main(String[] arg){
		try {
			Ftp2 f = null;
			f = new Ftp2();
			f.openConnection(arg[0]);	// ����p�R�l�N�V�����̐ݒ�
			f.getMsgs() ;				// ��M�X���b�h�̊J�n
			f.main_proc();				// ftp ����
			f.closeConnection() ;		// �R�l�N�V�����̃N���[�Y
			System.exit(0) ;			// �v���O�����̏I��
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
}

// CtrlListen �N���X
class CtrlListen implements Runnable{
	BufferedReader ctrlInput = null ;
	// �R���X�g���N�^�ǂݎ���̎w��
	public CtrlListen(BufferedReader in){
		ctrlInput = in ;
	}

	public void run(){
		while(true){
			try{ // �Ђ�����s��ǂݎ��,�W���o�͂ɃR�s�[���܂�
				System.out.println(ctrlInput.readLine()) ;
			} catch (Exception e){
				System.exit(1) ;
			}
		}
	}
}