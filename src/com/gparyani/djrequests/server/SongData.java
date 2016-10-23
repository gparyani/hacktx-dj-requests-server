package com.gparyani.djrequests.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class SongData {
	private String title, artist, album, deepLink;
	private int timesRequested;
	
	private static Set<SongData> cache = Collections.synchronizedSet(new HashSet<>());

	private SongData(String title, String artist, String album, String deepLink) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.deepLink = deepLink;
	}
	
	public static SongData getSong(String deepLink) {
		//get title, artist, album, and album art from Spotify API
		SongData toCompare = new SongData(null, null, null, deepLink);
		if(cache.contains(toCompare))
			for(SongData song : cache)
				if(toCompare.equals(song)) {
					song.incrementTimesRequested();
					return song;
				}
		cache.add(toCompare);
		return toCompare;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getDeepLink() {
		return deepLink;
	}

	public int getTimesRequested() {
		return timesRequested;
	}
	
	public void incrementTimesRequested() {
		timesRequested++;
	}
	
	public void resetTimesRequested() {
		timesRequested = 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((album == null) ? 0 : album.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SongData other = (SongData) obj;
		if (album == null) {
			if (other.album != null)
				return false;
		} else if (!album.equals(other.album))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	public static class TimesComparator implements Comparator<SongData> {
		@Override
		public int compare(SongData o1, SongData o2) {
			return o1.timesRequested - o2.timesRequested;
		}
	}
	
	public static class TitleComparator implements Comparator<SongData> {
		@Override
		public int compare(SongData o1, SongData o2) {
			return o1.title.compareTo(o2.title);
		}
	}
	
	public static class ArtistComparator implements Comparator<SongData> {
		@Override
		public int compare(SongData o1, SongData o2) {
			return o1.artist.compareTo(o2.artist);
		}
	}
	
	public static class AlbumComparator implements Comparator<SongData> {
		@Override
		public int compare(SongData o1, SongData o2) {
			return o1.album.compareTo(o2.album);
		}
	}
}
