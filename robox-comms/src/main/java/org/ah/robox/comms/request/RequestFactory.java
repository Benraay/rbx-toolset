/*******************************************************************************
 * Copyright (c) 2014 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Creative Sphere - initial API and implementation
 *
 *
 *******************************************************************************/
package org.ah.robox.comms.request;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Daniel Sendula
 */
public class RequestFactory {

    public static int PRINTER_PAUSE_RESUME_COMMAND = 0x98;
    public static int PRINTER_STATUS_REQ_COMMAND = 0xb0;
    public static int GET_PRINTER_DETAILS = 0xb2;
    public static int WRITE_PRINTER_DETAILS = 0xc1;
    public static int ABORT_PRINT_COMMAND = 0xff;

    private OutputStream out;

    public RequestFactory(OutputStream out) {
        this.out = out;
    }

    public void sendPrinterStatusRequest() throws IOException {
        byte[] buffer = new byte[1];
        buffer[0] = (byte)PRINTER_STATUS_REQ_COMMAND;
        out.write(buffer, 0, 1);
        out.flush();
    }

    public void sendPrinterPause() throws IOException {
        byte[] buffer = new byte[2];
        buffer[0] = (byte)PRINTER_PAUSE_RESUME_COMMAND;
        buffer[1] = (byte)'1';
        out.write(buffer, 0, 2);
        out.flush();
    }

    public void sendPrinterResume() throws IOException {
        byte[] buffer = new byte[2];
        buffer[0] = (byte)PRINTER_PAUSE_RESUME_COMMAND;
        buffer[1] = (byte)'0';
        out.write(buffer, 0, 2);
        out.flush();
    }

    public void sendPrinterAbortPrint() throws IOException {
        byte[] buffer = new byte[1];
        buffer[0] = (byte)ABORT_PRINT_COMMAND;
        out.write(buffer, 0, 1);
        out.flush();
    }

    public void sendGetPrinterDetails() throws IOException {
        byte[] buffer = new byte[1];
        buffer[0] = (byte)GET_PRINTER_DETAILS;
        out.write(buffer, 0, 1);
        out.flush();
    }
}
