/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    private static java.util.logging.Logger _logger = java.util.logging.Logger.getLogger("au.org.ala.delta");

    public static void log(String format, Object... args) {
        String message = String.format(format, args);
        _logger.fine(message);
    }

    public static void debug(String format, Object... args) {
        String message = String.format(format, args);
        _logger.fine(message);
    }

    public static void error(String format, Object... args) {
        String message = String.format(format, args);
        _logger.fine(message);
    }
    
    public static void error(Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);
        _logger.fine(sw.toString());
    }

}
