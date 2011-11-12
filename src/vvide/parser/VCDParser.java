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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */


package vvide.parser;

import java.io.File;

import vvide.logger.Logger;
import vvide.parser.VCDParserThread;

/**
 * Background parser for VCD-Files Start a thread, that parse the file
 */

public class VCDParser extends AbstractParser {

	/*
	 * =========================== Attributes ================================
	 */
	/**
	 * A background thread for a parser
	 */
	private VCDParserThread	thread;

	/*
	 * ============================ Methods ==================================
	 */
	@Override
	public boolean startParse(File file) {
		if (this.thread == null) {
			this.thread = new VCDParserThread(this, file);
		}
		if (!thread.canStart()) {
			return false;
		}
		if (!thread.isAlive()) {
			thread.start();
		}
		return true;
	}

	@Override
	public boolean stopParse() {
		if ((thread == null) || (!thread.isAlive())) return true;
		boolean isStopped = true;

		try {
			synchronized (thread) {
				thread.interrupt();
				thread.join(2000);
				if (!thread.isAlive()) {
					isStopped = true;
				}
			}
		} catch (InterruptedException e) {
			Logger.logError(this, e);
			return false;
		}
		return isStopped;
	}
}
