package org.subethamail.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.internal.Constants;
import org.subethamail.smtp.server.SMTPServer;

public final class BasicSMTPServer {

    static int defaultListenPort = 25000;

    SMTPServer start(int listenPort) {
        BasicMessageHandlerFactory myFactory = new BasicMessageHandlerFactory();
        SMTPServer smtpServer = SMTPServer.port(listenPort).messageHandlerFactory(myFactory).build();
        System.out.println("Starting Basic SMTP Server on port " + listenPort + "...");
        smtpServer.start();
        return smtpServer;
    }
    
    static final class BasicMessageHandlerFactory implements MessageHandlerFactory {

        @Override
        public MessageHandler create(MessageContext ctx) {
            return new Handler();
        }

        static final class Handler implements MessageHandler {

            Handler() {
            }

            @Override
            public void from(String from) throws RejectException {
                System.out.println("FROM:" + from);
            }

            @Override
            public void recipient(String recipient) throws RejectException {
                System.out.println("RECIPIENT:" + recipient);
            }

            @Override
            public void data(InputStream data) throws IOException {
                System.out.println("MAIL DATA");
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                System.out.println(this.convertStreamToString(data));
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
            }

            @Override
            public void done() {
                System.out.println("Finished");
            }

            private String convertStreamToString(InputStream is) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, Constants.SMTP_CHARSET));
                StringBuilder sb = new StringBuilder();

                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return sb.toString();
            }

        }
    }

    public static void main(String[] args) {
        new BasicSMTPServer().start(defaultListenPort);
        System.out.println("Server running!");
    }
}
