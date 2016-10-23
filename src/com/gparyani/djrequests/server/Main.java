package com.gparyani.djrequests.server;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

public class Main {
	
	static NetworkServer server;

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		server = new NetworkServer(48736);
		server.start();
		
		JFrame window = new JFrame("DJ Requests");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SongRequestListModel requests = new SongRequestListModel();
		window.getContentPane().add(BorderLayout.CENTER, new JScrollPane(new JList<JPanel>(requests)));
		window.getContentPane().add(BorderLayout.NORTH, new IPAndClientsLabel());
		window.setVisible(true);
		
		while(true) {
			for(String newRequest : server.newRequests()) {	//as this only includes the new requests, duplicates are not added
				SongData requestedSong = SongData.getSong(newRequest);
				requests.addSong(requestedSong);
			}
			window.getContentPane().add(BorderLayout.NORTH, new IPAndClientsLabel());
			window.repaint();
		}
	}
	
	static class IPAndClientsLabel extends JPanel {
		private static final long serialVersionUID = 2371626810666570290L;
		
		private static Font font = new Font(Font.SANS_SERIF, Font.ITALIC, 13);

		IPAndClientsLabel() {
			super();
			setLayout(new BorderLayout());
			JLabel ipAddress = new JLabel("IP address: " + server.getIPAddress());
			ipAddress.setFont(font);
			add(BorderLayout.WEST, ipAddress);
//			JLabel clients = new JLabel("Guests connected: " + server.numClients());
//			clients.setFont(font);
//			add(BorderLayout.EAST, clients);
		}
	}
}
