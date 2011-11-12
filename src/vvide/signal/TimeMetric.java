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

package vvide.signal;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vvide.Application;

/**
 * A class with timeMetrics
 */
public class TimeMetric {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Formatter for decimal values
	 */
	private static final DecimalFormat format = new DecimalFormat( "#,###" );
	/**
	 * Exponent for a FemtoSeconds
	 */
	public static final int FEMTO_SECOND = -15;
	/**
	 * Exponent for a PicoSeconds
	 */
	public static final int PICO_SECOND = -12;
	/**
	 * Exponent for a NanoSeconds
	 */
	public static final int NANO_SECOND = -9;
	/**
	 * Exponent for a MicroSeconds
	 */
	public static final int MICRO_SECOND = -6;
	/**
	 * Exponent for a MilliSeconds
	 */
	public static final int MILLI_SECOND = -3;
	/**
	 * Exponent for a Seconds
	 */
	public static final int SECOND = 0;
	/**
	 * Regexp to get a timeMetric
	 */
	private static Pattern timeMetricPattern = Pattern.compile( "([fpnmu]?s)$" );

	/*
	 * ============================= Methods =================================
	 */
	/**
	 * Convert a string name of the metric to double value
	 * 
	 * @param metric
	 *        a string with a metric
	 * @return int value of the metric
	 */
	public static int fromString( String metric ) {
		if ( metric != null ) {
			metric = metric.trim().toUpperCase();
			if ( metric.equals( "FS" ) ) return FEMTO_SECOND;
			if ( metric.equals( "PS" ) ) return PICO_SECOND;
			if ( metric.equals( "NS" ) ) return NANO_SECOND;
			if ( metric.equals( "US" ) ) return MICRO_SECOND;
			if ( metric.equals( "MS" ) ) return MILLI_SECOND;
		}
		return SECOND;
	}

	public static long fromString( String text, int scaleUnit ) {
		if ( text == null || text.trim().length() == 0 ) return -1;

		long position = 0;
		text = text.trim();

		// Getting a time entity
		Matcher m = timeMetricPattern.matcher( text );
		int timeMetric = scaleUnit;
		if ( m.find() ) timeMetric = fromString( m.group( 0 ) );

		// Getting position
		text = text.replaceAll( "[ \\-.a-zA-Z]", "" );
		int commaPosition = text.indexOf( ',' );
		if ( commaPosition == -1 ) commaPosition = text.length();
		if ( timeMetric < scaleUnit ) { return (commaPosition > 2) ? Long
				.parseLong( text.substring( 0, commaPosition ) ) * Application.settingsManager.getTimeRatio() : 0; }

		text = text.replaceAll( "\\D", "" );

		// Count of chars to move the comma, to equals timeMetric and ScaleUnits
		int commaPositionDelta = timeMetric - scaleUnit;
		if ( commaPosition + commaPositionDelta < text.length() ) { return Long
				.parseLong( text.substring( 0, commaPosition
					+ commaPositionDelta ) )
			* Application.settingsManager.getTimeRatio();

		}

		position =
				Long.parseLong( text )
					* (long) Math.pow( 10, commaPosition + commaPositionDelta
						- text.length() )
					* Application.settingsManager.getTimeRatio();

		// Adjust position
		if ( position > Application.signalManager.getSignalLength() )
			position = Application.signalManager.getSignalLength();

		return position;
	}

	/**
	 * Convert a double value of the TimeMetric to a String
	 * 
	 * @param metric
	 *        double value of the metric
	 * @return a string with a metric
	 */
	public static String toSting( int metric ) {
		switch ( metric ) {
		case FEMTO_SECOND:
			return "fs";
		case PICO_SECOND:
			return "ps";
		case NANO_SECOND:
			return "ns";
		case MICRO_SECOND:
			return "us";
		case MILLI_SECOND:
			return "ms";
		case SECOND:
			return "s";
		}
		return "";
	}

	/**
	 * Convert a time value to the best possible metric format
	 * 
	 * @param value
	 *        time value
	 * @param metric
	 *        int value of the metric
	 * @return string with a time format
	 */
	public static String toString( long time, int metric ) {

		// get a string xx.xx.xx.xxx
		String result =
				format.format( time
					/ Application.settingsManager.getTimeRatio() );
		int rest = (int) (time % Application.settingsManager.getTimeRatio());

		if ( rest == 0 ) {
			// Remove unused .000
			while ( result.endsWith( ".000" ) ) {
				result = result.substring( 0, result.length() - 4 );
				metric += 3;
			}
			// Adjust the metric
			metric += 3 * (result.length() / 4);
			// Remove last 0
			while ( result.contains( "." ) && result.endsWith( "0" ) ) {
				result = result.substring( 0, result.length() - 1 );
			}
		}
		else {
			// Adjust the metric
			metric += 3 * (result.length() / 4);
		}

		if ( rest != 0 ) {
			result += "." + rest;
		}
		result += " " + toSting( metric );
		result = result.replaceFirst( "\\.", "," );
		return result;
	}
}