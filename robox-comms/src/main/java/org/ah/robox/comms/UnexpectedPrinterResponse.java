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
package org.ah.robox.comms;

import java.io.IOException;

import org.ah.robox.comms.response.Response;

/**
 *
 * @author Daniel Sendula
 */
public class UnexpectedPrinterResponse extends IOException {

    private Response response;

    public UnexpectedPrinterResponse(Response response) {
        this.response = response;
    }

    @Override
    public String getMessage() {
        return "Unexpected printer response " + getResponse().toString();
    }

    public Response getResponse() {
        return response;
    }
}
