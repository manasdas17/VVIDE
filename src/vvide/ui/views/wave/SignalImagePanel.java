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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Signal panel. Create a panel with two background image. One for Signal and
 * one for overlay infos
 */
public class SignalImagePanel extends JPanel {

	/**
	 * UID
	 */
	private static final long serialVersionUID = 6669155607444659896L;
	/**
	 * Image with signal to be displayed
	 */
	private BufferedImage signalImage = null;
	/**
	 * Graphic object for a signal image
	 */
	private Graphics2D signalGraphics = null;
	/**
	 * Font render context for a signal image
	 */
	private FontRenderContext signalFontRenderContext = null;
	/**
	 * Image with overlay to be displayed
	 */
	private BufferedImage overlayImage = null;
	/**
	 * Graphic object for a overlay image
	 */
	private Graphics2D overlayGraphics = null;
	/**
	 * Font render context for a overlay image
	 */
	private FontRenderContext overlayFontRenderContext = null;

	/**
	 * Get a signal graphic for a drawing
	 */
	public Graphics2D getSignalGraphics() {
		return signalGraphics;
	}

	/**
	 * Get a signal font render context for a drawing
	 */
	public FontRenderContext getSignalFontRenderContext() {
		return signalFontRenderContext;
	}

	/**
	 * Get a overlay graphic for a drawing
	 */
	public Graphics2D getOverlayGraphics() {
		return overlayGraphics;
	}

	/**
	 * Get a overlay font render context for a drawing
	 */
	public FontRenderContext getOverlayFontRenderContext() {
		return overlayFontRenderContext;
	}

	/**
	 * Constructor
	 */
	public SignalImagePanel() {
		super();
		setDoubleBuffered( true );
	}

	@Override
	public void paint( Graphics g ) {
		if ( signalImage == null ) {
			super.paint( g );
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage( signalImage, null, 0, 0 );
		if ( overlayImage != null ) {
			g2d.drawImage( overlayImage, null, 0, 0 );
		}
	}

	/**
	 * Create a new images for signal and overlay
	 * 
	 * @param width
	 *        width of the images
	 * @param height
	 *        height of the images
	 */
	public void updateImages() {

		int width = (getWidth() == 0) ? 1 : getWidth();
		int height = (getHeight() == 0) ? 1 : getHeight();

		signalImage =
				new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		signalGraphics = (Graphics2D) signalImage.createGraphics();
		signalFontRenderContext = signalGraphics.getFontRenderContext();

		overlayImage =
				new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		overlayGraphics = (Graphics2D) overlayImage.createGraphics();
		overlayFontRenderContext = overlayGraphics.getFontRenderContext();
	}

	/**
	 * Remove images form a panel
	 */
	public void removeImages() {
		signalFontRenderContext = null;
		signalGraphics = null;
		signalImage = null;

		overlayFontRenderContext = null;
		overlayGraphics = null;
		overlayImage = null;
	}
}
