/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This file based on the code of WaveForm Viewer project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
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
package vvide;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import vvide.annotations.Export;
import vvide.annotations.Import;
import vvide.logger.Logger;
import vvide.signal.NumeralSystem;
import vvide.simulator.AbstractSimulator;
import vvide.simulator.simulators.GHDLSimulator;
import vvide.utils.IOMethods;
import vvide.utils.XMLUtils;

/**
 * A Manager for settings.<br>
 * Can load and save setting to the xml file
 */
public class SettingsManager {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * Settings file name
	 */
	public static String SETTINGS_FILE_NAME = "settings.xml";
	/**
	 * Default numeral system to use by showing the signal values
	 */
	private NumeralSystem defaultNumeralSystem = NumeralSystem.HEXADECIMAL;
	/**
	 * Multiply for a time
	 */
	private int timeRatio = 10;
	/**
	 * The height of the time line
	 */
	private int timeLineHeight = 45;
	/**
	 * Width of the signal information's area
	 */
	private int infoWidth = 150;
	/**
	 * The height of the signal image
	 */
	private int signalHeight = 45;
	/**
	 * Font for a time on timeline
	 */
	private Font timeFont = new Font( "Monospaced", 0, 10 );
	/**
	 * Color of the text with the time
	 */
	private Color timeColor = new Color( -12136704 );
	/**
	 * Color of the line
	 */
	private Color timeLineColor = new Color( -16768768 );
	/**
	 * Font for a text with a smallest displayed time
	 */
	private Font startTimeFont = new Font( "Times New Roman", 0, 10 );
	/**
	 * Color of the text with the starttime
	 */
	private Color startTimeColor = new Color( -12540913 );
	/**
	 * The bottom position of the signal
	 */
	private int lowOffset = 40;
	/**
	 * The top position of the signal
	 */
	private int highOffset = 15;
	/**
	 * Color of the signal bound lines
	 */
	private Color signalBoundColor = new Color( -16768768 );
	/**
	 * Font for a text with signal name
	 */
	private Font signalNameFont = new Font( "Times New Roman", 1, 14 );
	/**
	 * Font for a text with bit number
	 */
	private Font bitNrFont = new Font( "Arial", 0, 10 );
	/**
	 * Font for a text with signal value
	 */
	private Font signalValueFont = new Font( "Dialog", 0, 10 );
	/**
	 * Font for a distance text between two markers
	 */
	private Font markerDistanceFont = new Font( "Monospaced", 0, 12 );
	/**
	 * Color of the selected marker
	 */
	private Color selectedMarkerColor = new Color( -65536 );
	/**
	 * Color of the non-selected marker
	 */
	private Color nonSelectedMarkerColor = new Color( -256 );
	/**
	 * Color of the text with the distance
	 */
	private Color distanceTextColor = new Color( -256 );
	/**
	 * Default font for editor
	 */
	private Font editorFont = new Font( "Monospaced", 0, 13 );
	/**
	 * The path to the GHDL Simulator
	 */
	private String GHDLPath = "ghdl";
	/**
	 * The path to the Icarus Verilog Compiler
	 */
	private String icarusCompilerPath = "iverilog";
	/**
	 * The path to the Icarus Verilog Simulator
	 */
	private String icarusSimulatorPath = "vvp";
	/**
	 * The path to the ModelSim Simulator
	 */
	private String modelSimBinPath = "";
	/**
	 * Default simulator
	 */
	private AbstractSimulator defaultSimulator = new GHDLSimulator();
	/**
	 * Path to user dictionary
	 */
	private String userDictionaryPath = "userdict.txt";
	/**
	 * Path to the english dictionary
	 */
	private String englishDictionaryPath = "english_dic.zip";
	/**
	 * Color of the selected signal's border
	 */
	private Color selectedSignalBorderColor = new Color( 134, 170, 253 );
	/**
	 * Background of the selected signal
	 */
	private Color selectedSignalBackgroundColor = new Color( 134, 170, 253 );
	/**
	 * The size of sensitivity area for marker selection
	 */
	private int markerSelectionSensitivityArea = 7;

	/*
	 * ======================= Getters / Setters =============================
	 */
	/**
	 * Return the setting with the specified name
	 * 
	 * @param name
	 *        name of the setting
	 */
	public Object getSetting( String name ) {
		try {
			Method m = SettingsManager.class.getMethod( "get" + name );
			return m.invoke( this );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}
		return null;
	}

	/**
	 * Set a new value for the setting
	 * 
	 * @param name
	 *        name of the setting
	 * @param value
	 *        new value for the setting
	 */
	public void setSetting( String name, Object value ) {
		try {
			Method m =
				SettingsManager.class
					.getMethod( "set" + name, value.getClass() );
			m.invoke( this, value );
		}
		catch ( Exception e ) {
			Logger.logError( this, e );
		}

	}

	/**
	 * Getter for defaultNumeralSystem
	 * 
	 * @return the defaultNumeralSystem
	 */
	@Export( tagName = "DefaultNumeralSystem", type = "NumeralSystem" )
	public NumeralSystem getDefaultNumeralSystem() {
		return defaultNumeralSystem;
	}

	/**
	 * Setter for defaultNumeralSystem
	 * 
	 * @param defaultNumeralSystem
	 *        the defaultNumeralSystem to set
	 */
	@Import( tagName = "DefaultNumeralSystem" )
	public void setDefaultNumeralSystem( NumeralSystem defaultNumeralSystem ) {
		this.defaultNumeralSystem = defaultNumeralSystem;
	}

	/**
	 * Getter for timeRatio
	 * 
	 * @return the timeRatio
	 */
	@Export( tagName = "TimeRatio", type = "Integer" )
	public int getTimeRatio() {
		return timeRatio;
	}

	/**
	 * Setter for timeRatio
	 * 
	 * @param timeRatio
	 *        the timeRation to set
	 */
	@Import( tagName = "TimeRatio" )
	public void setTimeRatio( Integer timeRatio ) {
		this.timeRatio = timeRatio;
	}

	/**
	 * Getter for timeLineHeight
	 * 
	 * @return the timeLineHeight
	 */
	@Export( tagName = "TimeLineHeight", type = "Integer" )
	public int getTimeLineHeight() {
		return timeLineHeight;
	}

	/**
	 * Setter for timeLineHeight
	 * 
	 * @param timeLineHeight
	 *        the timeLineHeight to set
	 */
	@Import( tagName = "TimeLineHeight" )
	public void setTimeLineHeight( Integer timeLineHeight ) {
		this.timeLineHeight = timeLineHeight;
	}

	/**
	 * Getter for infoWidth
	 * 
	 * @return the infoWidth
	 */
	@Export( tagName = "InfoWidth", type = "Integer" )
	public int getInfoWidth() {
		return infoWidth;
	}

	/**
	 * Setter for infoWidth
	 * 
	 * @param infoWidth
	 *        the infoWidth to set
	 */
	@Import( tagName = "InfoWidth" )
	public void setInfoWidth( Integer infoWidth ) {
		this.infoWidth = infoWidth;
	}

	/**
	 * Getter for signalHeight
	 * 
	 * @return the signalHeight
	 */
	@Export( tagName = "SignalHeight", type = "Integer" )
	public int getSignalHeight() {
		return signalHeight;
	}

	/**
	 * Setter for signalHeight
	 * 
	 * @param signalHeight
	 *        the signalHeight to set
	 */
	@Import( tagName = "SignalHeight" )
	public void setSignalHeight( Integer signalHeight ) {
		this.signalHeight = signalHeight;
	}

	/**
	 * Getter for timeFont
	 * 
	 * @return the timeFont
	 */
	@Export( tagName = "TimeFont", type = "Font" )
	public Font getTimeFont() {
		return timeFont;
	}

	/**
	 * Setter for timeFont
	 * 
	 * @param timeFont
	 *        the timeFont to set
	 */
	@Import( tagName = "TimeFont" )
	public void setTimeFont( Font timeFont ) {
		this.timeFont = timeFont;
	}

	/**
	 * Getter for timeColor
	 * 
	 * @return the timeColor
	 */
	@Export( tagName = "TimeColor", type = "Color" )
	public Color getTimeColor() {
		return timeColor;
	}

	/**
	 * Setter for timeColor
	 * 
	 * @param timeColor
	 *        the timeColor to set
	 */
	@Import( tagName = "TimeColor" )
	public void setTimeColor( Color timeColor ) {
		this.timeColor = timeColor;
	}

	/**
	 * Getter for timeLineColor
	 * 
	 * @return the timeLineColor
	 */
	@Export( tagName = "TimeLineColor", type = "Color" )
	public Color getTimeLineColor() {
		return timeLineColor;
	}

	/**
	 * Setter for timeLineColor
	 * 
	 * @param timeLineColor
	 *        the timeLineColor to set
	 */
	@Import( tagName = "TimeLineColor" )
	public void setTimeLineColor( Color timeLineColor ) {
		this.timeLineColor = timeLineColor;
	}

	/**
	 * Getter for startTimeFont
	 * 
	 * @return the startTimeFont
	 */
	@Export( tagName = "StartTimeFont", type = "Font" )
	public Font getStartTimeFont() {
		return startTimeFont;
	}

	/**
	 * Setter for startTimeFont
	 * 
	 * @param startTimeFont
	 *        the startTimeFont to set
	 */
	@Import( tagName = "StartTimeFont" )
	public void setStartTimeFont( Font startTimeFont ) {
		this.startTimeFont = startTimeFont;
	}

	/**
	 * Getter for startTimeColor
	 * 
	 * @return the startTimeColor
	 */
	@Export( tagName = "StartTimeColor", type = "Color" )
	public Color getStartTimeColor() {
		return startTimeColor;
	}

	/**
	 * Setter for startTimeColor
	 * 
	 * @param startTimeColor
	 *        the startTimeColor to set
	 */
	@Import( tagName = "StartTimeColor" )
	public void setStartTimeColor( Color startTimeColor ) {
		this.startTimeColor = startTimeColor;
	}

	/**
	 * Getter for lowOffset
	 * 
	 * @return the lowOffset
	 */
	@Export( tagName = "LowOffset", type = "Integer" )
	public int getLowOffset() {
		return lowOffset;
	}

	/**
	 * Setter for lowOffset
	 * 
	 * @param lowOffset
	 *        the lowOffset to set
	 */
	@Import( tagName = "LowOffset" )
	public void setLowOffset( Integer lowOffset ) {
		this.lowOffset = lowOffset;
	}

	/**
	 * Getter for highOffset
	 * 
	 * @return the highOffset
	 */
	@Export( tagName = "HighOffset", type = "Integer" )
	public int getHighOffset() {
		return highOffset;
	}

	/**
	 * Setter for highOffset
	 * 
	 * @param highOffset
	 *        the highOffset to set
	 */
	@Import( tagName = "HighOffset" )
	public void setHighOffset( Integer highOffset ) {
		this.highOffset = highOffset;
	}

	/**
	 * Getter for signalBoundColor
	 * 
	 * @return the signalBoundColor
	 */
	@Export( tagName = "SignalBoundColor", type = "Color" )
	public Color getSignalBoundColor() {
		return signalBoundColor;
	}

	/**
	 * Setter for signalBoundColor
	 * 
	 * @param signalBoundColor
	 *        the signalBoundColor to set
	 */
	@Import( tagName = "SignalBoundColor" )
	public void setSignalBoundColor( Color signalBoundColor ) {
		this.signalBoundColor = signalBoundColor;
	}

	/**
	 * Getter for signalNameFont
	 * 
	 * @return the signalNameFont
	 */
	@Export( tagName = "SignalNameFont", type = "Font" )
	public Font getSignalNameFont() {
		return signalNameFont;
	}

	/**
	 * Setter for signalNameFont
	 * 
	 * @param signalNameFont
	 *        the signalNameFont to set
	 */
	@Import( tagName = "SignalNameFont" )
	public void setSignalNameFont( Font signalNameFont ) {
		this.signalNameFont = signalNameFont;
	}

	/**
	 * Getter for bitNrFont
	 * 
	 * @return the bitNrFont
	 */
	@Export( tagName = "BitNrFont", type = "Font" )
	public Font getBitNrFont() {
		return bitNrFont;
	}

	/**
	 * Setter for bitNrFont
	 * 
	 * @param bitNrFont
	 *        the bitNrFont to set
	 */
	@Import( tagName = "BitNrFont" )
	public void setBitNrFont( Font bitNrFont ) {
		this.bitNrFont = bitNrFont;
	}

	/**
	 * Getter for signalValueFont
	 * 
	 * @return the signalValueFont
	 */
	@Export( tagName = "SignalValueFont", type = "Font" )
	public Font getSignalValueFont() {
		return signalValueFont;
	}

	/**
	 * Setter for signalValueFont
	 * 
	 * @param signalValueFont
	 *        the signalValueFont to set
	 */
	@Import( tagName = "SignalValueFont" )
	public void setSignalValueFont( Font signalValueFont ) {
		this.signalValueFont = signalValueFont;
	}

	/**
	 * Getter for markerDistanceFont
	 * 
	 * @return the markerDistanceFont
	 */
	@Export( tagName = "MarkerDistanceFont", type = "Font" )
	public Font getMarkerDistanceFont() {
		return markerDistanceFont;
	}

	/**
	 * Setter for markerDistanceFont
	 * 
	 * @param markerDistanceFont
	 *        the markerDistanceFont to set
	 */
	@Import( tagName = "MarkerDistanceFont" )
	public void setMarkerDistanceFont( Font markerDistanceFont ) {
		this.markerDistanceFont = markerDistanceFont;
	}

	/**
	 * Getter for selectedMarkerColor
	 * 
	 * @return the selectedMarkerColor
	 */
	@Export( tagName = "SelectedMarkerColor", type = "Color" )
	public Color getSelectedMarkerColor() {
		return selectedMarkerColor;
	}

	/**
	 * Setter for selectedMarkerColor
	 * 
	 * @param selectedMarkerColor
	 *        the selectedMarkerColor to set
	 */
	@Import( tagName = "SelectedMarkerColor" )
	public void setSelectedMarkerColor( Color selectedMarkerColor ) {
		this.selectedMarkerColor = selectedMarkerColor;
	}

	/**
	 * Getter for nonSelectedMarkerColor
	 * 
	 * @return the nonSelectedMarkerColor
	 */
	@Export( tagName = "NonSelectedMarkerColor", type = "Color" )
	public Color getNonSelectedMarkerColor() {
		return nonSelectedMarkerColor;
	}

	/**
	 * Setter for nonSelectedMarkerColor
	 * 
	 * @param nonSelectedMarkerColor
	 *        the nonSelectedMarkerColor to set
	 */
	@Import( tagName = "NonSelectedMarkerColor" )
	public void setNonSelectedMarkerColor( Color nonSelectedMarkerColor ) {
		this.nonSelectedMarkerColor = nonSelectedMarkerColor;
	}

	/**
	 * Getter for distanceTextColor
	 * 
	 * @return the distanceTextColor
	 */
	@Export( tagName = "DistanceTextColor", type = "Color" )
	public Color getDistanceTextColor() {
		return distanceTextColor;
	}

	/**
	 * Setter for distanceTextColor
	 * 
	 * @param distanceTextColor
	 *        the distanceTextColor to set
	 */
	@Import( tagName = "DistanceTextColor" )
	public void setDistanceTextColor( Color distanceTextColor ) {
		this.distanceTextColor = distanceTextColor;
	}

	/**
	 * Getter for editorFont
	 * 
	 * @return the editorFont
	 */
	@Export( tagName = "EditorFont", type = "Font" )
	public Font getEditorFont() {
		return editorFont;
	}

	/**
	 * Setter for editorFont
	 * 
	 * @param editorFont
	 *        the editorFont to set
	 */
	@Import( tagName = "EditorFont" )
	public void setEditorFont( Font editorFont ) {
		this.editorFont = editorFont;
	}

	/**
	 * Getter for gHDLPath
	 * 
	 * @return the gHDLPath
	 */
	@Export( tagName = "GHDLPath", type = "String" )
	public String getGHDLPath() {
		return GHDLPath;
	}

	/**
	 * Setter for gHDLPath
	 * 
	 * @param gHDLPath
	 *        the gHDLPath to set
	 */
	@Import( tagName = "GHDLPath" )
	public void setGHDLPath( String gHDLPath ) {
		GHDLPath = gHDLPath;
	}

	/**
	 * Setter for icarusCompilerPath
	 * 
	 * @param icarusCompilerPath
	 *        the icarusCompilerPath to set
	 */
	@Import( tagName = "IcarusCompilerPath" )
	public void setIcarusCompilerPath( String icarusCompilerPath ) {
		this.icarusCompilerPath = icarusCompilerPath;
	}

	/**
	 * Getter for icarusCompilerPath
	 * 
	 * @return the icarusCompilerPath
	 */
	@Export( tagName = "IcarusCompilerPath", type = "String" )
	public String getIcarusCompilerPath() {
		return icarusCompilerPath;
	}

	/**
	 * Setter for icarusSimulatorPath
	 * 
	 * @param icarusSimulatorPath
	 *        the icarusSimulatorPath to set
	 */
	@Import( tagName = "IcarusSimulatorPath" )
	public void setIcarusSimulatorPath( String icarusSimulatorPath ) {
		this.icarusSimulatorPath = icarusSimulatorPath;
	}

	/**
	 * Getter for icarusSimulatorPath
	 * 
	 * @return the icarusSimulatorPath
	 */
	@Export( tagName = "IcarusSimulatorPath", type = "String" )
	public String getIcarusSimulatorPath() {
		return icarusSimulatorPath;
	}
	
	/**
	 * Setter for modelSimBinPath
	 * 
	 * @param modelSimBinPath
	 *        the modelSimBinPath to set
	 */
	@Import( tagName = "ModelSimBinPath" )
	public void setModelSimBinPath( String modelSimBinPath ) {
		this.modelSimBinPath = modelSimBinPath;
	}

	/**
	 * Getter for modelSimBinPath
	 * 
	 * @return the modelSimBinPath
	 */
	@Export( tagName = "ModelSimBinPath", type = "String" )
	public String getModelSimBinPath() {
		return modelSimBinPath;
	}

	/**
	 * Getter for defaultSimulator
	 * 
	 * @return the defaultSimulator
	 */
	public AbstractSimulator getDefaultSimulator() {
		return defaultSimulator;
	}

	/**
	 * Setter for defaultSimulator
	 * 
	 * @param defaultSimulator
	 *        the defaultSimulator to set
	 */
	public void setDefaultSimulator( AbstractSimulator defaultSimulator ) {
		this.defaultSimulator = defaultSimulator;
	}

	/**
	 * Getter for userDictionaryPath
	 * 
	 * @return the userDictionaryPath
	 */
	@Export( tagName = "UserDictionaryPath", type = "String" )
	public String getUserDictionaryPath() {
		return userDictionaryPath;
	}

	/**
	 * Setter for userDictionaryPath
	 * 
	 * @param userDictionaryPath
	 *        the userDictionaryPath to set
	 */
	@Import( tagName = "UserDictionaryPath" )
	public void setUserDictionaryPath( String userDictionaryPath ) {
		this.userDictionaryPath = userDictionaryPath;
	}

	/**
	 * Getter for englishDictionaryPath
	 * 
	 * @return the englishDictionaryPath
	 */
	@Export( tagName = "EnglishDictionaryPath", type = "String" )
	public String getEnglishDictionaryPath() {
		return englishDictionaryPath;
	}

	/**
	 * Setter for englishDictionaryPath
	 * 
	 * @param englishDictionaryPath
	 *        the englishDictionaryPath to set
	 */
	@Import( tagName = "EnglishDictionaryPath" )
	public void setEnglishDictionaryPath( String englishDictionaryPath ) {
		this.englishDictionaryPath = englishDictionaryPath;
	}

	/**
	 * Setter for selectedSignalBorderColor
	 * 
	 * @param selectedSignalBorderColor
	 *        the selectedSignalBorderColor to set
	 */
	@Import( tagName = "SelectedSignalBorderColor" )
	public void setSelectedSignalBorderColor( Color selectedSignalBorderColor ) {
		this.selectedSignalBorderColor = selectedSignalBorderColor;
	}

	/**
	 * Getter for selectedSignalBorderColor
	 * 
	 * @return the selectedSignalBorderColor
	 */
	@Export( tagName = "SelectedSignalBorderColor", type = "Color" )
	public Color getSelectedSignalBorderColor() {
		return selectedSignalBorderColor;
	}

	/**
	 * Setter for selectedSignalBackgroundColor
	 * 
	 * @param selectedSignalBackgroundColor
	 *        the selectedSignalBackgroundColor to set
	 */
	@Import( tagName = "SelectedSignalBackgroundColor" )
	public void setSelectedSignalBackgroundColor(
		Color selectedSignalBackgroundColor ) {
		this.selectedSignalBackgroundColor = selectedSignalBackgroundColor;
	}

	/**
	 * Getter for selectedSignalBackgroundColor
	 * 
	 * @return the selectedSignalBackgroundColor
	 */
	@Export( tagName = "SelectedSignalBackgroundColor", type = "Color" )
	public Color getSelectedSignalBackgroundColor() {
		return selectedSignalBackgroundColor;
	}

	/**
	 * Return the size of sensitivity area for marker selection
	 * 
	 * @return size of sensitivity area for marker selection
	 */
	@Export( tagName = "MarkerSelectionSensitivityArea", type = "Integer" )
	public int getMarkerSelectionSensitivityArea() {
		return markerSelectionSensitivityArea;
	}

	/**
	 * Setter for the size of sensitivity area for marker selection
	 * 
	 * @param markerSelectionSensitivityArea
	 *        the size of sensitivity area for marker selection
	 */
	@Import( tagName = "MarkerSelectionSensitivityArea" )
	public void setMarkerSelectionSensitivityArea(
		Integer markerSelectionSensitivityArea ) {
		this.markerSelectionSensitivityArea = markerSelectionSensitivityArea;
	}

	/*
	 * ============================ Methods ==================================
	 */
	/**
	 * Save settings to the file
	 */
	public void saveSettings() {
		File settingsFile = new File( SETTINGS_FILE_NAME );
		if ( settingsFile.exists() ) {
			try {
				IOMethods.backUpFile( settingsFile );
			}
			catch ( IOException e ) {}
		}
		new XMLUtils().saveToXMLFile( this, settingsFile );
	}

	/**
	 * Load Settings from the file
	 * 
	 * @return the loaded Settings manager
	 */
	public static SettingsManager loadSettings() {
		File settingsFile = new File( SETTINGS_FILE_NAME );
		if ( settingsFile.exists() ) { return (SettingsManager) (new XMLUtils())
			.loadFromXMLFile( settingsFile ); }
		return new SettingsManager();
	}
}
