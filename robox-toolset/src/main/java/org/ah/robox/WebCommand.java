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
package org.ah.robox;

import java.io.File;
import java.util.List;

import org.ah.robox.comms.Printer;
import org.ah.robox.comms.PrinterChannel;
import org.ah.robox.comms.PrinterDiscovery;
import org.ah.robox.comms.RoboxPrinter;
import org.ah.robox.web.WebServer;

/**
 *
 *
 * @author Daniel Sendula
 */
public class WebCommand {

    public static final int MAIN_BODY = 0;
    public static final int CAPTURE_IMAGE = 1;
    public static final int STATIC_FILE = 2;
    public static final int NOT_FOUND = 3;

    public static void execute(PrinterDiscovery printerDiscovery, String printerId, List<String> args) throws Exception {

        boolean portFlag = false;
        boolean imageCommandFlag = false;
        boolean refreshIntervalFlag = false;
        boolean imageRefreshIntervalFlag = false;
        boolean postRefreshCommandFlag = false;
        boolean staticDirFlag = false;
        boolean templateFileFlag = false;
        boolean refreshCommandFormatFlag = false;
//        boolean allowCommandsFlag = false;

        WebServer webServer = new WebServer(printerDiscovery);
        String templateFileName = null;

        boolean raspberryPi = System.getProperty("os.name").contains("Linux") || System.getProperty("os.arch").equalsIgnoreCase("arm");
        if (raspberryPi) {
            if (new File("/usr/bin/raspistill").exists()) {
                webServer.setImageCommand("raspistill -e jpg -w 800 -h 600 -o -");
            }
        }

        for (String a : args) {
            if (portFlag) {
                try {
                    webServer.setPort(Integer.parseInt(a));
                } catch (NumberFormatException e) {
                    System.err.println("Bad port number '" + a + "'");
                    System.exit(1);
                }
                portFlag = false;
            } else if (imageCommandFlag) {
                if ("".equals(a)) {
                    webServer.setImageCommand(null);
                } else {
                    webServer.setImageCommand(a);
                }
                imageCommandFlag = false;
            } else if (refreshIntervalFlag) {
                try {
                    webServer.setRefreshInterval(Integer.parseInt(a));
                } catch (NumberFormatException e) {
                    System.err.println("Bad number '" + a + "'");
                    System.exit(1);
                }
                refreshIntervalFlag = false;
            } else if (imageRefreshIntervalFlag) {
                try {
                    webServer.setImageRefreshInterval(Integer.parseInt(a));
                } catch (NumberFormatException e) {
                    System.err.println("Bad number '" + a + "'");
                    System.exit(1);
                }
                imageRefreshIntervalFlag = false;
            } else if (postRefreshCommandFlag) {
                if ("".equals(a)) {
                    webServer.setPostRefreshCommand(null);
                } else {
                    webServer.setPostRefreshCommand(a);
                }
                postRefreshCommandFlag = false;
            } else if (refreshCommandFormatFlag) {
                webServer.setRefreshCommandFormat(a);
                refreshCommandFormatFlag = false;
            } else if (templateFileFlag) {
                templateFileName = a;
                templateFileFlag = false;
            } else if (staticDirFlag) {
                webServer.setStaticDir(a);
                staticDirFlag = false;
            } else if ("-p".equals(a) || "--port".equals(a)) {
                portFlag = true;
            } else if ("-rs".equals(a) || "--refresh-status-interval".equals(a)) {
                refreshIntervalFlag = true;
            } else if ("-ri".equals(a) || "--refresh-image-interval".equals(a)) {
                imageRefreshIntervalFlag = true;
            } else if ("-ic".equals(a) || "--image-command".equals(a)) {
                imageCommandFlag = true;
            } else if ("-pc".equals(a) || "--post-refresh-command".equals(a)) {
                postRefreshCommandFlag = true;
            } else if ("-cf".equals(a) || "--post-refresh-comamnd-format".equals(a)) {
                refreshCommandFormatFlag = true;
            } else if ("-ac".equals(a) || "--allow-commands".equals(a)) {
                webServer.setAllowCommandsFlag(true);
            } else if ("-t".equals(a) || "--template-file".equals(a)) {
                templateFileFlag = true;
            } else if ("-sf".equals(a) || "--static-files".equals(a)) {
                staticDirFlag = true;
            } else {
                System.err.println("Unknown option: '" + a + "'");
                printHelp();
                System.exit(1);
            }
        }
        if (postRefreshCommandFlag) {
            System.err.println("Missing post refresh command.");
            System.exit(1);
        } else if (imageCommandFlag) {
            System.err.println("Missing image command inverval.");
            System.exit(1);
        } else if (imageRefreshIntervalFlag) {
            System.err.println("Missing image refresh inverval.");
            System.exit(1);
        } else if (refreshIntervalFlag) {
            System.err.println("Missing refresh interval.");
            System.exit(1);
        } else if (portFlag) {
            System.err.println("Missing port.");
            System.exit(1);
        } else if (refreshCommandFormatFlag) {
            System.err.println("Missing refresh command format.");
            System.exit(1);
        } else if (templateFileFlag) {
            System.err.println("Missing template file format.");
            System.exit(1);
        } else if (staticDirFlag) {
            System.err.println("Missing static directory.");
            System.exit(1);
        }

        if (templateFileName == null) {
            webServer.setTemplateFile(new File(templateFileName));
            if (!webServer.getTemplateFile().exists()) {
                System.err.println("Template file does not exist '" + templateFileName + "'.");
                System.exit(1);
            }
        }

        // Printer printer = new RoboxPrinter(selectedChannel);

        webServer.setPreferredPrinterId(printerId);
        webServer.init();
        webServer.start();

        System.out.println("Started web server at " + webServer.getAddress());
        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void printHelp() {
        System.out.println("Usage: rbx [<general-options>] web [<specific-options>]");
        System.out.println("");
        Main.printGeneralOptions();
        System.out.println("");
        Main.printSpecificOptions();
        System.out.println("");
        System.out.println("  -h | --help | -?     - this page");
        System.out.println("  -a | --all           - displays all status information");
        System.out.println("  -s | --short         - displays values only");
        System.out.println("                         It is machine readable format.");

        System.out.println("  -p | --port                     - port to start web server.");
        System.out.println("  -rs | --refresh-status-interval - refresh status interval in seconds.");
        System.out.println("        It is how often printer is going to be queried for the status.");
        System.out.println("        Default is 15 seconds.");
        System.out.println("  -ri | --refresh-image-interval  - refresh image interval in seconds.");
        System.out.println("        It is how image is fetched is going to be queried for the status.");
        System.out.println("        Default is 5 seconds. Also, if on RPi and raspistill is detected");
        System.out.println("        it will automatically be used.");
        System.out.println("  -ic | --image-command              - imaage command. This is shell command to be used");
        System.out.println("        to fetch image. Command should send image data in jpg format to stdout.");
        System.out.println("  -pc | --post-refresh-command       - comamnd to be called after printer status was");
        System.out.println("        fetched. It will be called with estimage format string as first parameter.");
        System.out.println("  -cf | --post-refresh-command-format - Format post refresh command is going to get");
        System.out.println("        estimate in. Placeholders are %c - command, %h -hours, %m - minutes (in.");
        System.out.println("        two digit format), %s - seconds (in two digit format).");
        System.out.println("        Default format is %c: %h:%m");
        System.out.println("  -ac | --allow-commands              - if set web pages will allow commaindg printer:");
        System.out.println("        sending pause, resume and abort commands.");
        System.out.println("  -t  | --template-file               - template html file for status file. See");
        System.out.println("        -sf/--static-files switch for extra resources like css/images...");
        System.out.println("  -sf | --static-files                - directory where static files are stored.");
        System.out.println("        They are going to be served from root of web app ('/').");
        System.out.println("");
        System.out.println("Template file should have following placeholders:");
        System.out.println("${status}   - printing status (\"Unknown\", \"Working\", \"Pausing\", \"Paused\", \"Resuming\")");
        System.out.println("${busy}     - is printer busy or not (\"true\", \"false\"). Can be used directly in javascript.");
        System.out.println("${job_id}   - printer job id.");
        System.out.println("${estimate}       - estimate time in %h:%m:%s format");
        System.out.println("${estimate_hours} - estimate time hours");
        System.out.println("${estimate_mins}  - estimate time minutes (with leading zero)");
        System.out.println("${estimate_secs}  - estimate time seconds (with leading zero)");
        System.out.println("${current_line}   - current line");
//        System.out.println("${}");
    }
}
