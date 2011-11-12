/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 *
 * Copyright (C) 2010-2011 Department of Digital Technology
 * of the University of Kassel, Germany
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package vvide.ui.views.wave;

/**
 * A model for a horizontal scroller at the signal panel
 */
public class ScrollRegion {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Begin value
	 */
	public long begin = 0;
	/**
	 * End value
	 */
	public long end = 0;
	/**
	 * Max value
	 */
	public long max = 0;
	/**
	 * Value for Scrollbar
	 */
	public int value = 0;
	/**
	 * Value for visible rage of scroller
	 */
	public int visibleRange = 0;
	/**
	 * Maximum value for scroll
	 */
	public int scrollMax = 1000000;

	/*
	 * =============================== Methods ==============================
	 */
	/**
	 * Constructor
	 * 
	 * @param max
	 *        - max value
	 */
	public ScrollRegion( long max ) {
		this.max = max;
	}

	/**
	 * Scroll for the specified value
	 * 
	 * @param delta
	 *        - how the value changes
	 * @param zoom
	 *        - zoom factor
	 */
	public void scroll( long delta, double zoom ) {
		long change = (long) (delta * zoom);

		// Calculate new viewValues
		calcViewValues( change );

		// Calculate new values for scrollbar
		calcScrollValue();
	}

	/**
	 * Move the region to the specified begin value
	 * 
	 * @param value
	 */
	public void moveTo( long value ) {
		long positionDelta =
				(long) (((double) value - this.value) / (double) scrollMax * max);
		calcViewValues( positionDelta );
	}

	/**
	 * move the region to the specified begin value and specified aligment
	 * 
	 * @param value
	 */
	public void moveTo( long value, ScrollAlignment alignment ) {

		// Correct a value
		if ( value < 0 ) {
			value = 0;
		}
		if ( value > max ) {
			value = max;
		}

		// Change the scroll position
		long delta = end - begin;
		switch ( alignment ) {
		case LEFT:
			if ( begin + delta > max ) {
				begin = max - delta;
			}
			else {
				begin = value;
			}
			end = begin + delta;
			break;
		case RIGHT:
			end = value;
			begin = end - delta;
			break;
		case CENTER:
			begin = value - delta / 2;
			end = begin + delta;
		}
		if ( begin < 0 ) {
			begin = 0;
		}
		if ( end > max ) {
			end = max;
		}
		calcScrollValue();
	}

	/**
	 * Calculate new begin and end values
	 * 
	 * @param positionDelta
	 *        - changes in the position
	 */
	private void calcViewValues( long positionDelta ) {
		// Changing of the begin
		begin += positionDelta;
		if ( begin < 0 ) {
			positionDelta = begin - positionDelta;
			begin = 0;
		}

		// Changing of the end
		end += positionDelta;
	}

	/**
	 * Calculate new values for scrollbar
	 */
	private void calcScrollValue() {
		value = getScrollValueForPosition( begin );
		// BugFix: if the Signal length > 1kk and value = 0 and begin != 0, then
		// scroll to link don't work
		if ( (value == 0) && (begin != 0) ) {
			value = 1;
		}
		// BugFix: if the Signal length > 1kk and value = 1kk and end !=
		// SignalLength , then scroll to right don't work
		if ( (value == scrollMax) && (end < max) ) {
			value = scrollMax - 1;
		}
		visibleRange =
				(int) (((float) (end - begin) / (float) max) * scrollMax);
	}

	/**
	 * Return a scroll value for a specified position
	 * 
	 * @param position
	 *        time
	 * @return value for scroller
	 */
	private int getScrollValueForPosition( long position ) {
		return (int) ((float) position / (float) max * scrollMax);
	}

	/**
	 * Change the Distance between start and end
	 * 
	 * @param region
	 *        - new Distance between start and end
	 */
	public void changeVisibleRegion( long region ) {

		if ( begin + region > max ) {
			end = max;
			begin = end - region;
			if ( begin < 0 ) {
				begin = 0;
			}
		}
		else {
			end = begin + region;
		}
		calcScrollValue();
	}
}
