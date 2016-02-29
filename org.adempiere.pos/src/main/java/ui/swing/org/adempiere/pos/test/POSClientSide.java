/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2014 E.R.P. Consultores y Asociados, C.A.               *
 * All Rights Reserved.                                                       *
 * Contributor(s): Raul Muñoz www.erpcya.com					              *
 *****************************************************************************/

package org.adempiere.pos.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;
import javax.swing.Timer;

import org.adempiere.pos.POSScalesPanel;
import org.compiere.model.MSysConfig;


/**
 * @author Mario Calderon, mario.calderon@westfalia-it.com, Systemhaus Westfalia, http://www.westfalia-it.com
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 * @author Raul Muñoz, rmunoz@erpcya.com, ERPCYA http://www.erpcya.com
 * @author victor.perez@e-evolution.com , http://www.e-evolution.com
 **/

public class POSClientSide extends Thread implements ActionListener {  

	public POSClientSide(String p_Host, String p_Print, JTextArea m_Terminal, String m_TimerDelay) {
		m_Host = p_Host;
		m_Print = p_Print;
		fTerminal = m_Terminal;
		m_port = MSysConfig.getIntValue("ZK_PORT_SERVER_PRINT", PORT);
		System.out.println(m_port);
		timerDelay = Integer.parseInt(m_TimerDelay);
		if(!connect())
			this.start();
	}
	/**  Port	Default			*/
	public 	static final int 	PORT = 5400;	
	/** Socket Client 			*/
	private Socket 				socketClient = null;
	/** Host Name 				*/
	private String 				m_Host = null;
	/** Print Name 				*/
	private String 				m_Print = null;
	/** Is Stop 				*/
	private boolean 			isStopped = true;
	/** Data Input Stream 		*/
	private DataInputStream 	dis = null;
	/** Field Terminal    		*/
	private JTextArea 			fTerminal = null;
	private POSScalesPanel	   scalesPanel = new POSScalesPanel(0);
	private Timer time;
	private int timerDelay;
	private int m_port;
	/**
	 * Connect with Server
	 * @return
	 * @return boolean
	 */
	private boolean connect() {
		if(isStopped()){
			
			try {
				socketClient = new Socket(m_Host, m_port);
				socketClient.setKeepAlive(true);
				isStopped = false;
		    	setText("Connected");
				return isStopped;
			} catch (IOException e) {
		    	setText("Error Connecting: "+e.getMessage());
		    	return isStopped;
			}
		}
		else {
			return isStopped;
		}
	}
		
	public void run(){
		
	    try {
	    	time = new Timer(timerDelay, this);

			String aux = "";
	
	      while(!isStopped || !isInterrupted()) {
			 connect();
	    	 dis = new DataInputStream(socketClient.getInputStream());
	    	 String value=dis.readUTF().toString();
	    	 if(value.equals("Obtener Peso")) {
	    		 
	    		
	    		 DataOutputStream dos = new DataOutputStream( socketClient.getOutputStream() );
	    		 dos.writeUTF(scalesPanel.getWeight());
	    		 System.out.println(scalesPanel.getWeight());
	    		 
	 			if(!scalesPanel.getWeight().equals("0") && !scalesPanel.getWeight().equals(aux)){
//	 				time.start();
	 			}
	 			aux=scalesPanel.getWeight();
	    	 }
	    	 else if(value.equals("Stop")){
	    		 DataOutputStream dos = new DataOutputStream( socketClient.getOutputStream() );
	    		 dos.writeUTF("Ultimo");
	    		 System.out.println("ULTIMO");
	    	 }
	    	 else {
	    	 
	    		 // Name File
             String name = "zk"+dis.readUTF().toString(); 

              // Size File
              int tam = dis.readInt(); 

              String path = System.getProperty("user.home")+File.separator+name ;
              FileOutputStream fos = new FileOutputStream(path);
              BufferedOutputStream out = new BufferedOutputStream( fos );
              BufferedInputStream in = new BufferedInputStream( socketClient.getInputStream() );

              byte[] buffer = new byte[tam];
              for( int i = 0; i < buffer.length; i++ ) {
                 buffer[ i ] = ( byte )in.read( ); 
              }
              
              out.write( buffer );
			  setText("File Received");
              
              out.flush(); 
    		  out.close();
    		  
    		  try{
    			  String[] cmd = new String[] { "lp" , "-d", m_Print, path};
    			  Runtime.getRuntime().exec(cmd);
    			  setText("Printing File");
    		  }catch(Exception a){
    			  setText("Error Printing: "+a.getMessage());
    		  }
	    	 }
  			
	       }     
	      	
	    } catch (IOException e) {
	    	isStopped=true;
	    	setText(e.getLocalizedMessage());
	    	connect();
	    }
	}
	
	/**
	 * Set Text
	 * @param m_Text
	 * @return void
	 */
	private void setText(String m_Text) {
		fTerminal.setText(getText()+m_Text+"\n");
	}
	
	/**
	 * Get Text
	 * @return String
	 */
	private String getText() {
		return fTerminal.getText();
	}
	
	/**
	 * IsStopped
	 * @return boolean
	 */
	public boolean isStopped() {
		return isStopped;
	}
	
	/**
	 * Close Connection 
	 * @return void
	 */
	public void closeConnect(){
		
			isStopped = true;
			scalesPanel.stopService();
			this.interrupt();
			setText("Disconnected");
		
	}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource().equals(time)){
				if(time.isRunning()){
				
					DataOutputStream dos;
					try {
						dos = new DataOutputStream( socketClient.getOutputStream() );

						dos.writeUTF("Ultimo");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
		}
			
		}
}