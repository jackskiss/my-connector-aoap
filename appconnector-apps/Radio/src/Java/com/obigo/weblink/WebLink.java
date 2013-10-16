// This file automatically generated by:
//   Apache Etch 1.3.0-incubating (LOCAL-0) / java 1.3.0-incubating (LOCAL-0)
//   Wed Sep 25 20:31:58 KST 2013
// This file is automatically created and should not be edited!

package com.obigo.weblink;

import java.io.Serializable;


@SuppressWarnings("unused")
public interface WebLink
{
	public enum RemoteControl
	{
		/**
		 */
		VOLUME_UP
		/**
		 */
		, VOLUME_DOWN
		/**
		 */
		, PLAY
		/**
		 */
		, PAUSE
		/**
		 */
		, NEXT
		/**
		 */
		, PREV
	}

	@SuppressWarnings("serial")
	public class RadioStation
		implements Serializable
	{
		/**
		 * Constructs the RadioStation. Don't init any fields.
		 */
		public RadioStation()
		{
			// don't init any fields.
		}

		/**
		 * Constructs the RadioStation.
		 */
		public RadioStation
		(
			Integer id
			, String title
			, String genre
			, String url
			, String thumb
		)
		{
			this.id = id;
			this.title = title;
			this.genre = genre;
			this.url = url;
			this.thumb = thumb;
		}

		@Override
		public String toString()
		{
			return String.format( "RadioStation(id=%s; title=%s; genre=%s; url=%s; thumb=%s)", id, title, genre, url, thumb );
		}

		public Integer id;

		/**
		 * Gets the value.
		 *
		 *
		 * @return the value.
		 */
		public Integer getId()
		{
			return id;
		}

		/**
		 * Sets the value.
		 *
		 *
		 * @param value the value.
		 */
		public void setId( Integer value )
		{
			this.id = value;
		}

		public String title;

		/**
		 * Gets the value.
		 *
		 *
		 * @return the value.
		 */
		public String getTitle()
		{
			return title;
		}

		/**
		 * Sets the value.
		 *
		 *
		 * @param value the value.
		 */
		public void setTitle( String value )
		{
			this.title = value;
		}

		public String genre;

		/**
		 * Gets the value.
		 *
		 *
		 * @return the value.
		 */
		public String getGenre()
		{
			return genre;
		}

		/**
		 * Sets the value.
		 *
		 *
		 * @param value the value.
		 */
		public void setGenre( String value )
		{
			this.genre = value;
		}

		public String url;

		/**
		 * Gets the value.
		 *
		 *
		 * @return the value.
		 */
		public String getUrl()
		{
			return url;
		}

		/**
		 * Sets the value.
		 *
		 *
		 * @param value the value.
		 */
		public void setUrl( String value )
		{
			this.url = value;
		}

		public String thumb;

		/**
		 * Gets the value.
		 *
		 *
		 * @return the value.
		 */
		public String getThumb()
		{
			return thumb;
		}

		/**
		 * Sets the value.
		 *
		 *
		 * @param value the value.
		 */
		public void setThumb( String value )
		{
			this.thumb = value;
		}
	}

}