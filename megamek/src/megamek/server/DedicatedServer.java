/*
 * MegaMek - Copyright (C) 2005 Ben Mazur (bmazur@sev.org)
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation; either version 2 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *  for more details.
 */
package megamek.server;

import java.io.File;
import java.io.IOException;

import megamek.MegaMek;
import megamek.common.preference.PreferenceManager;
import megamek.common.util.AbstractCommandLineParser;

public class DedicatedServer {
    private static final String INCORRECT_ARGUMENTS_MESSAGE = "Incorrect arguments:";
    private static final String ARGUMENTS_DESCRIPTION_MESSAGE = "Arguments syntax:\n\t "
            + "[-password <pass>] [-port <port>] [-competitive <competitive>] [<saved game>]";

    public static void start(String[] args) {
        CommandLineParser cp = new CommandLineParser(args);
        try {
            cp.parse();
            String saveGameFileName = cp.getGameFilename();
            int usePort;
            if (cp.getPort() != -1) {
                usePort = cp.getPort();
            } else {
                usePort = PreferenceManager.getClientPreferences().getLastServerPort();
            }
            String announceUrl = cp.getAnnounceUrl();
            String password = cp.getPassword();
            boolean competitive = cp.getCompetitive();

            // kick off a RNG check
            megamek.common.Compute.d6();
            // start server
            Server dedicated = startServer(password, usePort, announceUrl, competitive);
            if (dedicated == null) {
                return;
            }
            if (null != saveGameFileName) {
                dedicated.loadGame(new File(saveGameFileName));
            }
        } catch (AbstractCommandLineParser.ParseException e) {
            MegaMek.getLogger().error(INCORRECT_ARGUMENTS_MESSAGE + e.getMessage() + '\n'
                            + ARGUMENTS_DESCRIPTION_MESSAGE);
        }
    }

    public static Server startServer(String password, int usePort, String announceUrl, boolean competitive) {
        Server dedicated;
        try {
            if (password == null || password.length() == 0) {
                password = PreferenceManager.getClientPreferences().getLastServerPass();
            }
            dedicated = new Server(password, usePort, !announceUrl.equals(""), announceUrl, competitive);
        } catch (IOException ex) {
            MegaMek.getLogger().error("Error: could not start server at localhost" + ":" + usePort + " ("
                    + ex.getMessage() + ").");
            return null;
        }
        return dedicated;
    }

    public static void main(String[] args) {
        start(args);
    }

    public static class CommandLineParser extends AbstractCommandLineParser {
        private String gameFilename;
        private int port;
        private String password;
        private boolean competitive = false;
        private String announceUrl = "";

        // Options
        private static final String OPTION_PORT = "port";
        private static final String OPTION_PASSWORD = "password";
        private static final String OPTION_ANNOUNCE = "announce";
        private static final String OPTION_COMPETITIVE = "competitive";

        public CommandLineParser(String[] args) {
            super(args);
        }

        /**
         *
         * @return port option value or <code>-1</code> if it wasn't set
         */
        public int getPort() {
            return port;
        }
        
        /**
         * 
         * @return the password option value, will be null if not set.
         */
        public String getPassword() {
            return password;
        }

        public String getAnnounceUrl() {
            return announceUrl;
        }

        public boolean getCompetitive() {
            return competitive;
        }

        /**
         *
         * @return the game file name option value or <code>null</code> if it wasn't set
         */
        public String getGameFilename() {
            return gameFilename;
        }

        @Override
        protected void start() throws ParseException {
            while (hasNext()) {
                int tokType = getToken();
                switch (tokType) {
                case TOK_OPTION:
                    switch (getTokenValue()) {
                        case OPTION_PORT:
                            nextToken();
                            parsePort();
                            break;
                        case OPTION_ANNOUNCE:
                            nextToken();
                            parseAnnounce();
                            break;
                        case OPTION_PASSWORD:
                            nextToken();
                            parsePassword();
                            break;
                        case OPTION_COMPETITIVE:
                            nextToken();
                            parseCompetitive();
                            break;
                    }
                    break;
                case TOK_LITERAL:
                    gameFilename = getTokenValue();
                    nextToken();
                    break;
                case TOK_EOF:
                    // Do nothing, although this shouldn't happen
                    break;
                default:
                    throw new ParseException("unexpected input");
                }
                nextToken();                
            }
        }

        private void parsePort() throws ParseException {
            if (getToken() == TOK_LITERAL) {
                int newPort = -1;
                try {
                    newPort = Integer.decode(getTokenValue());
                } catch (NumberFormatException ignored) {
                    //ignore, leave at -1
                }
                if ((newPort < 0) || (newPort > 65535)) {
                    throw new ParseException("invalid port number");
                }
                port = newPort;
            } else {
                throw new ParseException("port number expected");
            }
        }

        private void parseAnnounce() throws ParseException {
            if (getToken() == TOK_LITERAL) {
                announceUrl = getTokenValue();
            } else {
                throw new ParseException("meta server announce URL expected");
            }
        }
        
        private void parsePassword() throws ParseException {
            if (getToken() == TOK_LITERAL) {
                password = getTokenValue();
            } else {
                throw new ParseException("password expected");
            }
        }

        private void parseCompetitive() throws ParseException {
            if (getToken() == TOK_LITERAL) {
                competitive = Boolean.parseBoolean(getTokenValue());
            } else {
                throw new ParseException("password expected");
            }
        }
    }
}
