package com.gparyani.djrequests.server;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class SongRequestListModel implements ListModel<JPanel> {
	
	private PriorityBlockingQueue<SongData> songs = new PriorityBlockingQueue<>(11, new SongData.TimesComparator());
	
	private class SongDataPanel extends JPanel {
		private static final long serialVersionUID = -1467842604567232246L;
		
		private SongData thisSong;

		SongDataPanel(SongData song) {
			super();
			thisSong = song;
			setLayout(new BorderLayout());
			JPanel left = new JPanel();
			left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
			JLabel title = new JLabel(song.getTitle());
			title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			JLabel artist = new JLabel(song.getArtist());
			artist.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
			JLabel album = new JLabel(song.getAlbum());
			album.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
			JLabel timesRequested = new JLabel("Requested " + song.getTimesRequested() + " times");
			timesRequested.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			left.add(title);
			left.add(artist);
			left.add(album);
			left.add(timesRequested);
			add(BorderLayout.CENTER, left);
			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					thisSong.resetTimesRequested();
					songs.remove(thisSong);
				}
			});
			add(BorderLayout.EAST, clearButton);
		}
	}
	
	public void addSong(SongData song) {
		songs.remove(song);	//remove outdated, cached song
		songs.add(song);	//add newly updated version
	}

	@Override
	public int getSize() {
		return songs.size();
	}

	@Override
	public JPanel getElementAt(int index) {
		SongData element = songs.toArray(new SongData[0])[index];
		return new SongDataPanel(element);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
	}

}
